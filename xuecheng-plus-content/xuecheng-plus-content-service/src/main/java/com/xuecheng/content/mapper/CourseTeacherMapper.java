package com.xuecheng.content.mapper;

import com.xuecheng.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huojz
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Mapper
* @createDate 2023-10-19 20:47:33
* @Entity com.xuecheng.content.model.po.CourseTeacher
*/
@Mapper
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

}




