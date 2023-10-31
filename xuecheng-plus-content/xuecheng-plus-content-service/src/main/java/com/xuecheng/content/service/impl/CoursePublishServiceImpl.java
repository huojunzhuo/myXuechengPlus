package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.JsonUtil;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: CoursePublishServiceImpl
 * Package: com.xuecheng.content.service.impl
 * Description: CoursePublishServiceImpl
 *
 * @Author huojz
 * @Create 2023/10/30 19:53
 * @Version 1.0
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseTeacherService courseTeacherService;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;



    /**
     * @param courseId 课程id
     * @return void
     * @description 课程提交审核
     */
    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("当前课程不存在");
        }
        //提交审核的不允许重复提交
        if ("202003".equals(courseBase.getAuditStatus())) {
            XueChengPlusException.cast("已提交审核，请勿重复提交");
        }
        //本机构只允许提交本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("不允许提交其它机构的课程");
        }
        //课程图片未上传的不允许提交
        if (StringUtil.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("课程图片为空");
        }

        //封装返回对象
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        BeanUtil.copyProperties(courseBaseInfo, coursePublishPre);
        //获取课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (ObjectUtil.isEmpty(courseMarket)){
            XueChengPlusException.cast("课程营销信息不存在");
        }
        String courseMarketJsonString = JsonUtil.objectTojson(courseMarket);
        coursePublishPre.setMarket(courseMarketJsonString);
        //获取课程计划信息
        List<TeachplanDto> teachplanDtoList = teachplanService.getTreeNodes(courseId);
        if (ObjectUtil.isEmpty(teachplanDtoList)) {
            XueChengPlusException.cast("课程计划不存在不允许提交审核");
        }
        String teachplanDtoListJstr = JsonUtil.listTojson(teachplanDtoList);
        //设置课程计划信息
        coursePublishPre.setTeachplan(teachplanDtoListJstr);
        //查询课程师资信息
        List<CourseTeacher> courseTeacherList = courseTeacherService.findCourseTeacher(courseId);
        String courseTeacherListJstr = JsonUtil.listTojson(courseTeacherList);
        //设置课程师资信息
        coursePublishPre.setTeachers(courseTeacherListJstr);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setStatus("202003");
        coursePublishPre.setValidDays(365);

        //查询课程预发布表
        CoursePublishPre coursePublishPreSelect = coursePublishPreMapper.selectById(courseId);
        //如果没有记录则插入记录
        if (coursePublishPreSelect == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        }else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本信息表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 获取课程预览信息
     *
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //调用已有接口查询结果
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        List<TeachplanDto> TeachplanDtos = teachplanService.getTreeNodes(courseId);
        List<CourseTeacher> courseTeachers = courseTeacherService.findCourseTeacher(courseId);

        //封装返回结果对象模型类：CoursePreviewDto
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(TeachplanDtos);
        coursePreviewDto.setCourseTeachers(courseTeachers);
        return coursePreviewDto;
    }
}
