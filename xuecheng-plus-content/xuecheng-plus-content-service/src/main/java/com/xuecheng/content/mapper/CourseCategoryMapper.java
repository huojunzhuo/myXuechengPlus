package com.xuecheng.content.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author huojz
* @description 针对表【course_category(课程分类)】的数据库操作Mapper
* @createDate 2023-10-09 19:11:09
* @Entity generator.domain.CourseCategory
*/
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {


    public List<CourseCategoryTreeDto> selectTreeNodes( String id);

//    @Select("select * from course_category where id = #{id}")
    public List<CourseCategory> selectAll( String id);

}




