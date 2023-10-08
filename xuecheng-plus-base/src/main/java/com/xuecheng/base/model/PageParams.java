package com.xuecheng.base.model;

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
public class PageParams {

    //当前页码 默认1L
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    @ApiModelProperty("每页记录数默认值")
    //每页记录数默认值 默认10L
    private Long pageSize =10L;

}
