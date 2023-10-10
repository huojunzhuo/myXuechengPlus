package com.xuecheng.base.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: PageResult
 *
 * @Description 分页查询响应结果模型类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 08 16:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "分页返回信息",description = "分页返回信息")
public class PageResult<T> {

    @ApiModelProperty(value ="当前页码",required = true )
    private Long pageNo ;
    @ApiModelProperty(value ="每页记录数",required = true )
    private Long pageSize ;
    @ApiModelProperty(value ="数据列表",required = true )
    private List<T> items;
    @ApiModelProperty(value ="总记录数",required = true )
    private long counts;

}
