package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * ClassName: EditCourseDto
 *
 * @Description 修改课程模型类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 14 16:30
 */
@Data
public class EditCourseDto extends AddCourseDto{

    @ApiModelProperty(value ="主键",required = true )
    private Long id;
}
