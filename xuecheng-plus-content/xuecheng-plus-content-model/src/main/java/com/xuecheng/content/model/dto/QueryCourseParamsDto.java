package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: QueryCourseParamsDto
 *
 * @Description 查询条件模型类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 08 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCourseParamsDto {

    //审核状态
    @ApiModelProperty("审核状态")
    private String auditStatus;
    //课程名称
    @ApiModelProperty("课程名称")
    private String courseName;
    //发布状态
    @ApiModelProperty("发布状态")
    private String publishStatus;

}
