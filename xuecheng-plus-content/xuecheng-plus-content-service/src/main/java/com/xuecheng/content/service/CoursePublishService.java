package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

/**
 * ClassName: CoursePublishService
 * Package: com.xuecheng.content.service
 * Description: CoursePublishService 课程发布service接口
 *
 * @Author huojz
 * @Create 2023/10/30 19:51
 * @Version 1.0
 */
public interface CoursePublishService {

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public File generateCourseHtml(Long courseId);

    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    public void  uploadCourseHtml(Long courseId,File file);

    /**
     * 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     */
    public void publish(Long companyId,Long courseId);

    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @description 课程提交审核
     * @param courseId  课程id
     * @return void
     */
    public void commitAudit(Long companyId , Long courseId);
}
