package com.xuecheng.base.model;

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
public class PageResult<T> {
    //当前页码
    private Long pageNo ;

    //每页记录数
    private Long pageSize ;

    // 数据列表
    private List<T> items;

    //总记录数
    private long counts;

}
