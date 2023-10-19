package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author huojz
* @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Service
* @createDate 2023-10-19 20:47:33
*/
public interface CourseTeacherService extends IService<CourseTeacher> {

    /**
     * 根据课程id查询师资列表
     * @param courseId
     * @return
     */
    List<CourseTeacher> findCourseTeacher(Long courseId);

    /**
     * 新增课程教师
     * @param courseTeacherDto
     * @return
     */
    boolean saveCourseTeacher(CourseTeacherDto courseTeacherDto);

    /**
     * 修改课程教师
     * @param courseTeacherDto
     * @return
     */
    boolean updateCourseTeacher(CourseTeacherDto courseTeacherDto);
}
