package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;


/**
* @author HJZ
* @description 针对表【course_base(课程基本信息)】的数据库操作Service
* @createDate 2023-10-08 16:42:27
*/
public interface CourseBaseService extends IService<CourseBase> {
    /**
     * 条件分页查询课程基本信息
     * @param pageParams
     * @param queryCourseParamsDto
     * @return
     */
    public PageResult<CourseBase> list(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @author Mr.M
     * @date 2022/9/7 17:51
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 查询课程基本信息、营销信息，返回页面模型类
     * @param courseId 课程id
     * @return 课程基本信息模型类
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * 修改课程信息接口
     * @param editCourseDto 修改课程信息模型类
     * @return
     */
    public CourseBaseInfoDto updateCourseBase(Long companyId,EditCourseDto editCourseDto) ;

    /**
     * 删除课程基本信息
     * @param courseId 课程id
     * @return
     */
    Boolean removeCourse(Long courseId);
}
