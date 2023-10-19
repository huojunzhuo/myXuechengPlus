package com.xuecheng.content.api;

import cn.hutool.core.util.ObjectUtil;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CourseTeacherController
 * Package: com.xuecheng.content.api
 * Description:
 *
 * @Author huojz
 * @Create 2023/10/19 20:52
 * @Version 1.0
 */
@RestController
@Api(value = "课程教师接口", tags = "课程教师接口")
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("教师列表查询")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> findCourseTeacher(@PathVariable Long courseId){
        return courseTeacherService.findCourseTeacher(courseId);
    }

    @ApiOperation("新增或者修改教师")
    @PostMapping("/courseTeacher")
    public String saveCourseTeacher(@RequestBody CourseTeacherDto courseTeacherDto){

        Long id = courseTeacherDto.getId();
        if(ObjectUtil.isNull(id)){
            //走新增逻辑
            boolean isTrue = courseTeacherService.saveCourseTeacher(courseTeacherDto);
            return isTrue? "新增success":"新增fail";
        }
        //走修改逻辑
        boolean isTrue = courseTeacherService.updateCourseTeacher(courseTeacherDto);
        return isTrue? "修改success":"修改fail";
    }
}
