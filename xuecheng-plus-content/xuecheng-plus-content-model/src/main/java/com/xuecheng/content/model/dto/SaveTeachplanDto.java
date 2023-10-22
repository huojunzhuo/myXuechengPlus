package com.xuecheng.content.model.dto;

import com.xuecheng.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * ClassName: SaveTeachplanDto
 * Package: com.xuecheng.content.model.dto
 * Description: 保存课程计划dto，包括新增、修改
 *
 * @Author huojz
 * @Create 2023/10/18 19:37
 * @Version 1.0
 */
@Data
@ApiModel(value="SaveTeachplanDto", description="新增/修改课程计划DTO")
public class SaveTeachplanDto {

    //分组校验；新增及修改课程计划
    @NotNull(groups = {ValidationGroups.Update.class},message = "修改课程计划时id不为空")
    @ApiModelProperty(value = "教学计划id")
    /***
     * 教学计划id
     */
    private Long id;
    @NotEmpty(message = "课程计划名称不为空")
    @ApiModelProperty(value = "课程计划名称", required = true)
    /**
     * 课程计划名称
     */
    private String pname;

    @NotNull(message = "章节不为空")
    @ApiModelProperty(value = "课程计划父级Id", required = true)
    /**
     * 课程计划父级Id
     */
    private Long parentid;
    @NotNull(message = "层级不为空")
    @ApiModelProperty(value = "层级，分为1、2、3级", required = true)
    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;
    @NotEmpty(message = "课程类型不为空")
    @ApiModelProperty(value = "课程类型:1视频、2文档", required = true)
    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;
    @NotNull(message = "课程标识不为空")
    @ApiModelProperty(value = "课程标识")
    /**
     * 课程id
     */
    private Long courseId;
    @ApiModelProperty(value = "课程发布标识")
    /**
     * 课程发布标识
     */
    private Long coursePubId;
    @NotEmpty(message = "是否支持试学或预览不为空")
    @ApiModelProperty(value = "是否支持试学或预览（试看）", required = true)
    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

}
