package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName: CourseOpenController
 * Package: com.xuecheng.content.api
 * Description: 公共开放课程接口：CourseOpenController
 *
 * @Author huojz
 * @Create 2023/10/30 20:37
 * @Version 1.0
 */
@Api(value = "课程公开查询接口", tags = "课程公开查询接口")
@RestController
@RequestMapping("/open") //统一前缀 /open
public class CourseOpenController {
    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation("课程发布预览返回接口")
    @GetMapping("/coursepreview/{courseId}")
    public CoursePreviewDto  getPreviewInfo(@PathVariable("courseId") @ApiParam("课程id") Long courseId) {
        return coursePublishService.getCoursePreviewInfo(courseId);
    }
}
