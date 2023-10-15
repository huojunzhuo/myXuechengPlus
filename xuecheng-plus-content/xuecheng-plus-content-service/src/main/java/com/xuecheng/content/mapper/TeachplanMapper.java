package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
* @author HJZ
* @description 针对表【teachplan(课程计划)】的数据库操作Mapper
* @createDate 2023-10-15 11:30:13
* @Entity content.po.Teachplan
*/
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 查询某课程的课程计划，组成树型结构
     * @param courseId
     * @return com.xuecheng.content.model.dto.TeachplanDto
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);

}




