package com.xuecheng.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.config.MinioConfig;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.stream.FileImageInputStream;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description MediaFileServiceImpl
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl extends ServiceImpl<MediaFilesMapper, MediaFiles> implements MediaFileService {

    @Autowired
    MinioClient minioClient;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    @Lazy
    MediaFileService currentProxy;

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.xuecheng.base.model.RestResponse
     * @description 合并分块
     * @author Mr.M
     * @date 2022/9/13 15:56
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //先临时定义bucketName
        String bucket_videoFiles = "video";
        //根据md5获取分块文件路径（5/8/5878a684ee9a36daae5d0741aaca0747/chunk/）
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //遍历分块文件路径组成，将所有分块文件组成List<ComposeSource>
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal) //截断流的长度chunkTotal
                .map(i -> ComposeSource.builder() //对每一个流元素映射到ComposeSource
                        .bucket(bucket_videoFiles)
                        .object(chunkFileFolderPath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        //=====1.合并=====
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        //调用私有方法获取合并文件路径
        String mergeFilePath = getFilePathByMd5(fileMd5, extension);
        //调用minio合并文件
        try {
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build()
            );
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail(false, "合并文件失败。");
        }
        // ====2.验证md5====
        //下载合并后的文件
        File minoFile = downloadFileFromMinIO(bucket_videoFiles, mergeFilePath);
        if (minoFile == null) {
            log.debug("下载合并后文件失败,mergeFilePath:{}", mergeFilePath);
            return RestResponse.validfail(minoFile, "下载合并后文件失败。");
        }

        try {
            InputStream fileInputStream = new FileInputStream(minoFile);
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            //比较md5值，不一致则说明文件不完整
            if (!fileMd5.equals(md5Hex)) {
                return RestResponse.validfail("文件合并校验失败，最终上传失败。");
            }
            //定义文件大小封装到返回结果
            uploadFileParamsDto.setFileSize(minoFile.length());
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("校验文件失败,fileMd5:{}", "异常:{}", fileMd5, e.getMessage(), e);
            return RestResponse.validfail("文件合并校验失败，最终上传失败。");
        } finally {
            //删除合并文件
            if (minoFile != null) {
                minoFile.delete();
            }
        }
        //====3.文件入库====
        currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
        //=====清除分块文件=====
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        //清除成功，返回最终成功结果
        return RestResponse.success(true);
    }

    /**
     * 清除分块文件
     * @param chunkFileFolderPath 分块文件路径
     * @param chunkTotal          分块文件总数
     */
    private void clearChunkFiles(String chunkFileFolderPath, int chunkTotal) {
        try {
            //遍历分块目录，对每一个元素映射创建DeleteObject，结果封装到 List<DeleteObject>
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> i++)
                    .limit(chunkTotal) //截取前chunkTotal截断流
                    .map(i -> new DeleteObject(chunkFileFolderPath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            //定义删除对象
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder()
                    .bucket("video")
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            //判断是否删除成功
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清除分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            log.error("清除分块文件失败,chunkFileFolderPath:{}",chunkFileFolderPath,e);
        }
    }


    /**
     * 从minio下载文件
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName) {
        //声明接收文件
        File minioFile = null;
        //定义输出文件流
        FileOutputStream outputStream = null;
        try {
            //从minio下载，获取输入流加载到内存
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            //接收文件赋值临时文件
            minioFile = File.createTempFile("minio", ".temp");
            //创建输出到接收文件的流
            outputStream = new FileOutputStream(minioFile);
            //流拷贝
            IOUtils.copy(inputStream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 根据md5获取合并文件的地址
     * @param fileMd5
     * @param extension
     * @return
     */
    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }

    /**
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查文件是否已上传成功
     * @author Mr.M
     * @date 2022/9/13 15:38
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询数据库是否包含该媒资文件
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileMd5);
        if (BeanUtil.isEmpty(mediaFile)) {
            //数据库查询结果为空，媒资文件未上传
            log.info("数据库查询结果为空，媒资文件未上传");
            return RestResponse.success(false);
        }
        //如果数据库查询结果不为空，获取相关数据去MinIo查询
        String bucket = mediaFile.getBucket();
        String filePath = mediaFile.getFilePath();
        //查询MinIo是否包含该文件
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filePath)
                            .build());
            if (inputStream != null) {
                //文件已存在
                log.info("MinIo查询成功，文件已存在");
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询MIinIO报错抛异常");
            XueChengPlusException.cast("查询MIinIO报错");
        }
        //文件不存在
        log.info("文件不存在");
        return RestResponse.success(false);
    }

    /**
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @description 检查分块是否已上传
     * @author Mr.M
     * @date 2022/9/13 15:39
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String bucket_videoFiles = "video";
        //调用私有方法获取分块文件目录（5/8/5878a684ee9a36daae5d0741aaca0747/chunk/）
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //拼接分块文件的目录（5/8/5878a684ee9a36daae5d0741aaca0747/chunk/1）
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        //调用minio的sdk方法查询分块文件
        InputStream fileInputStream = null;
        try {
            fileInputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket_videoFiles)
                            .object(chunkFilePath)
                            .build()
            );
            if (fileInputStream != null) {
                //分块已经存在
                log.info("分块已经存在");
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        log.info("分块未存在");
        return RestResponse.success(false);
    }

    /**
     * 得到分块文件的目录
     *
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5) {
        String foldPath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
        return foldPath;
    }

    /**
     * @param fileMd5            文件md5
     * @param chunk              分块序号
     * @param localChunkFilePath 分块文件本地路径
     * @return com.xuecheng.base.model.RestResponse
     * @description 上传分块文件
     * @author Mr.M
     * @date 2022/9/13 15:50
     */
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        String bucket_videoFiles = "video";
        //根据md5获取分块存储路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //增加分块序号，拼接分块文件本身的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        //获取文件类型：mimeType（分块文件的扩展名为空）
        String mimeType = getMimeType(null);
        //调用sdk将文件存储至minIO
        Boolean aBoolean = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_videoFiles, chunkFilePath);
        if (!aBoolean) {
            log.debug("上传分块文件失败chunkFilePath:{}", chunkFilePath);
            //返回给前端上传失败的结果
            return RestResponse.validfail(false, "上传分块文件失败");
        }
        return RestResponse.success(true);
    }

    /**
     * @param companyId           机构id
     * @param uploadFileParamsDto 上传文件参数的模型类
     * @param localFilePath       本地文件路径
     * @return 上传文件返回结果的模型类
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            //如果文件不存在
            XueChengPlusException.cast("文件不存在");
        }
        //获取文件名字
        String filename = uploadFileParamsDto.getFilename();
        //获取文件扩展名(截取字符串)
        String extension = filename.substring(filename.lastIndexOf("."));
        //私有方法计算文件mimeType
        String mimeType = getMimeType(extension);
        //私有方法计算文件的md5值
        String fileMd5 = getMd5(file);
        //私有方法获取文件的默认目录（年/月/日+/）
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        String objectName = defaultFolderPath + fileMd5 + extension;
        String fileType = uploadFileParamsDto.getFileType();
        String bucket = null;
        if ("001001".equals(fileType)) {
            bucket = "mediafiles";
        }
        //将文件上传到minio
        Boolean aBoolean = addMediaFilesToMinIO(localFilePath, mimeType, bucket, objectName);
        uploadFileParamsDto.setFileSize(file.length());
        //将文件保存到数据库(事务提交)
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket, objectName);
        System.out.println(currentProxy);
        //封装返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtil.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket              桶
     * @param objectName          对象名称
     * @return
     * @description 将文件信息添加到文件表
     */
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto
            , String bucket, String objectName) {
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileMd5);
        if (BeanUtil.isEmpty(mediaFile)) {
            mediaFile = new MediaFiles();
            BeanUtil.copyProperties(uploadFileParamsDto, mediaFile);
            mediaFile.setId(fileMd5);
            mediaFile.setFileId(fileMd5);
            mediaFile.setCompanyId(companyId);
            mediaFile.setUrl("/" + bucket + "/" + objectName);
            mediaFile.setFilePath(objectName);
            mediaFile.setCreateDate(LocalDateTime.now());
            mediaFile.setAuditStatus("002003");
            mediaFile.setStatus("1");
            mediaFile.setBucket(bucket);
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFile);
            if (insert < 0) {
                log.error("保存文件到数据库失败:{}", mediaFile.toString());
                XueChengPlusException.cast("保存文件到数据库失败");
            }
            log.debug("保存文件到数据库成功", mediaFile.toString());
            return mediaFile;
        }
        XueChengPlusException.cast("文件已存在");
        return mediaFile;
    }

    //私有方法：生成文件的存储路径:年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = simpleDateFormat.format(new Date()) + "/";
        return format;
    }

    //私有方法：DigestUtils工具获取文件的md5
    private String getMd5(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //私有方法：根据扩展名获取文件MimeType
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 将小文件文件写入minIO
     *
     * @param localFilePath 文件本地地址
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    对象名称
     * @return 是否上传成功
     */
    public Boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}", bucket, objectName, e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return false;
    }


}
