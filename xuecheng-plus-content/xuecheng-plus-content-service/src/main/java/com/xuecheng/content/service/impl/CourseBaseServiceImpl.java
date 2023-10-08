package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;

import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HJZ
* @description 针对表【course_base(课程基本信息)】的数据库操作Service实现
* @createDate 2023-10-08 16:42:27
*/
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService{
    @Autowired
    CourseBaseMapper courseBaseMapper;

    /**
     * 条件分页查询课程基本信息
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    @Override
    public PageResult<CourseBase> list(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        String courseName = queryCourseParamsDto.getCourseName();
        String auditStatus = queryCourseParamsDto.getAuditStatus();
        String status = queryCourseParamsDto.getPublishStatus();
        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //封装等值查询条件
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(auditStatus),CourseBase::getAuditStatus, auditStatus)
                          .eq(StringUtils.isNotEmpty(status),CourseBase::getStatus, status);
        //封装模糊查询条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(courseName),CourseBase::getName,courseName);
        //封装分页参数
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, lambdaQueryWrapper);
        //获取记录
        List<CourseBase> records = courseBasePage.getRecords();
        long current = courseBasePage.getCurrent();//当前页码
        long pageSize = courseBasePage.getSize();//每页记录数
        long total = courseBasePage.getTotal();//总数

        PageResult<CourseBase> result = new PageResult<CourseBase>(current,pageSize,records,total);
        return result;
    }
}




