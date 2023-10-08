package com.xuecheng.content.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.ibatis.annotations.Mapper;

/**
* @author HJZ
* @description 针对表【course_base(课程基本信息)】的数据库操作Mapper
* @createDate 2023-10-08 16:42:27
* @Entity content.po.CourseBase
*/
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

}




