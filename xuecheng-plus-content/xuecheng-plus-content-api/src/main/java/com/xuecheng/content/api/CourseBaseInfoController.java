package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: CourseBaseInfoController
 *
 * @Description 课程基础信息controller
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 08 17:11
 */
@Api(value = "课程基础信息接口",tags = "课程基础信息接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseService courseBaseService;
    @ApiOperation("课程查询接口")
    //pageParam参数通过URL请求参数传递，QueryCourseParamsDto参数通过请求体获取
    @RequestMapping(method = RequestMethod.POST,value = "/course/list")
    @ResponseBody
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.list(pageParams,queryCourseParamsDto);
    }
}
