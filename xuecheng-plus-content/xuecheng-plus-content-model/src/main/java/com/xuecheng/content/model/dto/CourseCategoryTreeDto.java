package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: CourseCategoryTreeDto
 * Package: com.xuecheng.content.model.dto
 * Description: 分类查询树形结构模型类dto
 *
 * @Author huojz
 * @Create 2023/10/9 19:17
 * @Version 1.0
 */
@Data
@ApiModel(value = "分类查询树形结构模型类",description = "分类查询树形结构模型类")
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    private List<CourseCategoryTreeDto> childrenTreeNodes;

}
