package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;

import java.util.List;

/**
* @author huojz
* @description 针对表【course_category(课程分类)】的数据库操作Service
* @createDate 2023-10-09 19:11:09
*/
public interface CourseCategoryService extends IService<CourseCategory> {
    /**
     * 根据id查询树形结构
     * @param id
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);


    public List<CourseCategoryTreeDto> queryTreeNodes1(String id);
}
