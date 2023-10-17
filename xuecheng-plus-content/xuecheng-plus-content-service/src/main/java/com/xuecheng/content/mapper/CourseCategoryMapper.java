package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author huojz
* @description 针对表【course_category(课程分类)】的数据库操作Mapper
* @createDate 2023-10-09 19:11:09
* @Entity generator.domain.CourseCategory
*/
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * MySQL8.0递归查询
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> selectTreeNodes( String id);

    /**
     * mysql内连接查询
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> selectTreeNodesByUnion();

    /**
     * 查询全部记录（有参）
     */
    public List<CourseCategoryTreeDto> selectAll( String id);
    /**
     * 查询全部记录（无参）
     */
    public List<CourseCategoryTreeDto> selectAllList();



}




