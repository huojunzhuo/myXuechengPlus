package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
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
}
