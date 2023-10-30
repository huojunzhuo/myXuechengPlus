package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName: BindTeachplanMediaDto
 * Package: com.xuecheng.content.model.dto
 * Description: BindTeachplanMediaDto
 *
 * @Author huojz
 * @Create 2023/10/30 16:35
 * @Version 1.0
 */
@Data
@ApiModel(value = "BindTeachplanMediaDto",description = "教学计划-媒资绑定提交数据模型类")
public class BindTeachplanMediaDto {


    @ApiModelProperty(value = "媒资文件id", required = true)
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称", required = true)
    private String mediaFilename;

    @ApiModelProperty(value = "课程计划标识", required = true)
    private Long teachplanId;

    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId ;

}
