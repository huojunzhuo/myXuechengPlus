package com.xuecheng.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: PageParam
 *
 * @Description 分页参数模型类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 08 16:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "分页参数",description = "分页参数模型类")
public class PageParams {

    //当前页码 默认1L
    @ApiModelProperty(value ="当前页码",required = true )
    private Long pageNo = 1L;

    @ApiModelProperty(value ="每页记录数默认值",required = true )
    //每页记录数默认值 默认10L
    private Long pageSize =10L;

}
