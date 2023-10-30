package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * ClassName: CoursePreviewDto
 * Package: com.xuecheng.content.model.dto
 * Description: 课程预览模型类
 *
 * @Author huojz
 * @Create 2023/10/30 19:47
 * @Version 1.0
 */
@ApiModel(value = "课程发布信息模型类", description = "课程发布信息模型类")
@Data
public class CoursePreviewDto {

    @ApiModelProperty(value = "课程基本信息,课程营销信息模型类")
    CourseBaseInfoDto courseBase;

    @ApiModelProperty(value = "课程计划信息")
    List<TeachplanDto> teachplans;

    @ApiModelProperty(value = "课程师资信息")
    List<CourseTeacher> courseTeachers;

}
