package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService extends IService<MediaFiles> {

    /**
     * @param companyId 机构id
     * @param uploadFileParamsDto 上传文件参数的模型类
     * @param localFilePath 本地文件路径
     * @return 上传文件返回结果的模型类
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath,String objectName);

    /**
     * 从minio下载文件
     * @param bucket     桶
     * @param objectName 对象名称
     * @return 下载后的文件
     */
    public File downloadFileFromMinIO(String bucket, String objectName);
    /**
     * @description 将文件信息添加到文件表
     * @param companyId 机构id
     * @param fileMd5 文件md5值
     * @param uploadFileParamsDto 上传文件的信息
     * @param bucket 桶
     * @param objectName 对象名称
     * @return
     */
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto
            ,String bucket,String objectName);

    /**
     * @description 检查文件是否存在
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author Mr.M
     * @date 2022/9/13 15:38
     */
    public RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @description 检查分块是否存在
     * @param fileMd5  文件的md5
     * @param chunkIndex  分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
     * @author Mr.M
     * @date 2022/9/13 15:39
     */
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @description 上传分块
     * @param fileMd5  文件md5
     * @param chunk  分块序号
     * @param localChunkFilePath  分块文件本地路径
     * @return com.xuecheng.base.model.RestResponse
     * @author Mr.M
     * @date 2022/9/13 15:50
     */
    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    /**
     * @description 合并分块
     * @param companyId  机构id
     * @param fileMd5  文件md5
     * @param chunkTotal 分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.xuecheng.base.model.RestResponse
     * @author Mr.M
     * @date 2022/9/13 15:56
     */
    public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);

    /**
     * 将小文件文件写入minIO
     * @param localFilePath 文件本地地址
     * @param mimeType      文件类型
     * @param bucket        桶
     * @param objectName    对象名称
     * @return 是否上传成功
     */
    public Boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName);



}
