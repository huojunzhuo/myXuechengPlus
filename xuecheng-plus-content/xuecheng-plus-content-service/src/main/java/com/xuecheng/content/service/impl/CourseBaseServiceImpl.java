package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;

    /**
     * 修改课程信息接口的实现类
     * @param editCourseDto 修改课程信息模型类
     * @return CourseBaseInfoDto 课程基本信息模型类
     */
    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto) {
        Long courseId = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            XueChengPlusException.cast("课程不存在");
        }
        if(!companyId.equals(courseBase.getCompanyId())){
            XueChengPlusException.cast("只能编辑本机构的课程");
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(editCourseDto,courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        BeanUtils.copyProperties(editCourseDto,courseMarket);
        int i = courseBaseMapper.updateById(courseBase);
        if(i<= 0){
            XueChengPlusException.cast("课程基本信息更新失败");
        }
        int j = courseMarketMapper.updateById(courseMarket);
        if(j<=0){
            XueChengPlusException.cast("课程营销信息更新失败");
        }
        return this.getCourseBaseInfo(courseId);
    }

    /**
     * 查询课程基本信息
     * @param courseId 课程id
     * @return 课程基本信息模型类
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        courseBaseInfoDto.setMtName(courseCategoryMapper.selectById(courseBase.getMt()).getName());
        courseBaseInfoDto.setStName(courseCategoryMapper.selectById(courseBase.getSt()).getName());
        return courseBaseInfoDto;
    }

    /**
     * @param companyId    教学机构id
     * @param addCourseDto 课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 添加课程并返回课程相关信息
     * @author Mr.M
     * @date 2022/9/7 17:51
     */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        //封装CourseBase对象用于存储课程基本信息数据库
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto,courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());//后面修改
        courseBase.setCreatePeople(null);//后面修改
        courseBase.setAuditStatus("202002");//审核状态为未审核
        courseBase.setStatus("203001");//发布状态为未发布
        int insert = courseBaseMapper.insert(courseBase);
        if (insert <=0){
            throw new RuntimeException("保存课程基本信息失败");
        }
        //获取插入课程的id
        Long id = courseBase.getId();
        //封装CourseMarketd对象用于存储课程营销信息数据库
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto,courseMarket);
        courseMarket.setId(id);
        int insert2 = courseMarketMapper.insert(courseMarket);
        if (insert2<=0){
            throw new RuntimeException("保存课程营销信息失败");
        }
        CourseBase courseBaseSelect = courseBaseMapper.selectById(id);
        CourseMarket courseMarketSelect = courseMarketMapper.selectById(id);
        //创建返回数据的模型类
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBaseSelect,courseBaseInfoDto);//拷贝基本信息
        BeanUtils.copyProperties(courseMarketSelect,courseBaseInfoDto);//拷贝营销信息
        courseBaseInfoDto.setId(id);
        courseBaseInfoDto.setCompanyId(companyId);
        courseBaseInfoDto.setCompanyName(null);
        courseBaseInfoDto.setMtName(courseCategoryMapper.selectById(courseBaseSelect.getMt()).getName());
        courseBaseInfoDto.setStName(courseCategoryMapper.selectById(courseBaseSelect.getSt()).getName());
        return courseBaseInfoDto;
    }

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




