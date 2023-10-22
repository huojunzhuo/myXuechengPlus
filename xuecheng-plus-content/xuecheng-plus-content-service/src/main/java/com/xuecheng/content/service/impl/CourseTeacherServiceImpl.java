package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author huojz
 * @description 针对表【course_teacher(课程-教师关系表)】的数据库操作Service实现
 * @createDate 2023-10-19 20:47:33
 */
@Service
@Slf4j
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher>
        implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    /**
     * 根据id删除教师
     * @param id
     * @return
     */
    @Override
    public boolean deleteCourseTeacher(Long courseId,Integer id) {
        CourseTeacher courseTeacherSelect = courseTeacherMapper.selectOne(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId,courseId)
                .eq(CourseTeacher::getId,id));
        if(BeanUtil.isEmpty(courseTeacherSelect)){
            log.error("教师id不存在{}",id);
            XueChengPlusException.cast("教师id不存在，请重新输入");
        }
        int i = courseTeacherMapper.deleteById(id);
        if(i<= 0 ){
            XueChengPlusException.cast("删除课程教师失败，请重试");
        }
        return true;
    }

    /**
     * 根据课程id查询师资列表
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> findCourseTeacher(Long courseId) {
        return courseTeacherMapper.selectList(new LambdaQueryWrapper<CourseTeacher>()
                .eq(CourseTeacher::getCourseId, courseId)
        );
    }

    /**
     * 新增课程教师实现类
     * @param courseTeacherDto
     * @return
     */
    @Override
    public boolean saveCourseTeacher(CourseTeacherDto courseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtil.copyProperties(courseTeacherDto,courseTeacher);
        courseTeacher.setCreateDate(LocalDateTime.now());
        courseTeacherMapper.insert(courseTeacher);
        return true;
    }

    /**
     * 修改课程教师
     * @param courseTeacherDto
     * @return
     */
    @Override
    public boolean updateCourseTeacher(CourseTeacherDto courseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtil.copyProperties(courseTeacherDto,courseTeacher);
        courseTeacherMapper.updateById(courseTeacher);
        return true;
    }
}




