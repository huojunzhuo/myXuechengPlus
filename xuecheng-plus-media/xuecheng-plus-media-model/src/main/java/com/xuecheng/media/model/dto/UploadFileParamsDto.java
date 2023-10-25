package com.xuecheng.media.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * ClassName: UploadFileParamsDto
 * Package: com.xuecheng.media.model.dto
 * Description: 上传普通文件请求参数
 * @Author huojz
 * @Create 2023/10/24 19:13
 * @Version 1.0
 */
@Data
@ApiModel(value="UploadFileParamsDto", description="上传文件参数模型类")
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件类型（文档，音频，视频）
     */
    private String fileType;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private String username;

    /**
     * 备注
     */
    private String remark;

}
