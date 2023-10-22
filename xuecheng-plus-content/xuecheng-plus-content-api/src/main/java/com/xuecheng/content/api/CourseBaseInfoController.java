package com.xuecheng.content.api;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ClassName: CourseBaseInfoController
 *
 * @Description 课程基础信息controller
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 08 17:11
 */
@Api(value = "课程基础信息接口", tags = "课程基础信息接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseService courseBaseService;

    @ApiOperation("课程查询接口")
    //pageParam参数通过URL请求参数传递，QueryCourseParamsDto参数通过请求体获取
    @RequestMapping(method = RequestMethod.POST, value = "/course/list")
    @ResponseBody
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseService.list(pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    // @Validated注解开启模型类参数校验
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Inster.class) AddCourseDto addCourseDto) {
        Long companyId = 10101L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.createCourseBase(companyId, addCourseDto);
        return courseBaseInfoDto;
    }


    @ApiOperation("查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated(ValidationGroups.Update.class) EditCourseDto editCourseDto) {
        Long conpanyId = 1232141425L;
        CourseBaseInfoDto courseBaseInfoDto = courseBaseService.updateCourseBase(conpanyId, editCourseDto);
        return courseBaseInfoDto;
    }

    @ApiOperation("删除课程全部信息")
    @DeleteMapping("/course/{courseId}")
    public String removeCourse(@PathVariable Long courseId){
       Boolean b =  courseBaseService.removeCourse(courseId);
       if(b){

           return "删除课程成功";
       }
        return "删除课程失败";
    }

}
