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
import com.xuecheng.media.config.MinioConfig;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description MediaFileServiceImpl
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl extends ServiceImpl<MediaFilesMapper,MediaFiles> implements  MediaFileService  {

    @Autowired
    MinioClient minioClient;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    @Lazy
    MediaFileService currentProxy;

    /**
     * @param companyId           机构id
     * @param uploadFileParamsDto 上传文件参数的模型类
     * @param localFilePath       本地文件路径
     * @return 上传文件返回结果的模型类
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath){
        File file  = new File(localFilePath);
        if (!file.exists()){
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
        String objectName = defaultFolderPath + fileMd5  +  extension;
        String fileType = uploadFileParamsDto.getFileType();
        String bucket =null;
        if ("001001".equals(fileType)){
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
        BeanUtil.copyProperties(mediaFiles,uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * @description 将文件信息添加到文件表
     * @param companyId 机构id
     * @param fileMd5 文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket 桶
     * @param objectName 对象名称
     * @return
     */
    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto
            ,String bucket,String objectName) {
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
    private String getDefaultFolderPath(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = simpleDateFormat.format(new Date())+"/";
        return format;
    }
    //私有方法：DigestUtils工具获取文件的md5
    private String getMd5(File file){
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
    private String getMimeType(String extension){
        if (extension == null){
            extension = "";
        }
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 将文件写入minIO
     * @param localFilePath 文件本地地址
     * @param mimeType 文件类型
     * @param bucket 桶
     * @param objectName 对象名称
     * @return 是否上传成功
     */
    public Boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统失败");
        }
        return false;
    }


}
