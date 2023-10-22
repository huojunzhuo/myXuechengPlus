package com.xuecheng.content.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.CourseMarket;

/**
* @author huojz
* @description 针对表【course_market(课程营销信息)】的数据库操作Service
* @createDate 2023-10-12 20:23:14
*/
public interface CourseMarketService extends IService<CourseMarket> {

    /**
     *
     * @return
     */
    public Boolean deleteCourseMarket(Long id);
}
