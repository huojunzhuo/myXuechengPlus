package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

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
     * 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
