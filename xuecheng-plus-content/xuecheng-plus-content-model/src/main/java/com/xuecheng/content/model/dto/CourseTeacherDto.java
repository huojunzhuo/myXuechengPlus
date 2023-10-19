package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * ClassName: CourseTeacherDto
 * Package: com.xuecheng.content.model.dto
 * Description:
 *
 * @Author huojz
 * @Create 2023/10/19 21:05
 * @Version 1.0
 */
@Data
@ApiModel(value="课程教师模型类", description="课程教师模型类")
public class CourseTeacherDto {
    /**
     * 课程id
     */
    private Long id;
    @NotEmpty(message = "courseId不能为空")
    /**
     * 课程名称
     */
    private Long courseId;
    @NotEmpty(message = "教师name不能为空")
    /**
     * 教师标识
     */
    private String teacherName;

    /**
     * 教师职位
     */
    private String position;

    /**
     * 教师简介
     */
    private String introduction;

    /**
     * 照片
     */
    private String photograph;
}
