package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName: CoursePublishController
 * Package: com.xuecheng.content.api
 * Description: 课程预览，发布
 *
 * @Author huojz
 * @Create 2023/10/30 19:39
 * @Version 1.0
 */
@Api(value = "课程发布接口",tags = "课程发布接口")
@Controller
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;
    @ApiOperation("课程发布接口")
    @PostMapping("/coursepublish/{courseId}")
    @ResponseBody
    public void coursepublish(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.publish(1232141425L,courseId);
    }


    @ResponseBody
    @ApiOperation("课程提交审核接口")
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") @ApiParam(value = "课程id",required = true) Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId,courseId);
    }

    @ApiOperation("课程发布预览返回接口")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId")  Long courseId) {
        //调用service查询：获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }


}
