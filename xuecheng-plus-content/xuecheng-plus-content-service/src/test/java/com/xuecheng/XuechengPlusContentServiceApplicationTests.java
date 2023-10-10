package com.xuecheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class XuechengPlusContentServiceApplicationTests {

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Test
    public void testCourseBaseMapper() {
        List<CourseBase> courseBases = courseBaseMapper.selectList(null);
        for (CourseBase c : courseBases) {
            System.out.println("c = " + c);
        }
    }

    @Test
    public void testSelectList(){
        LambdaQueryWrapper<CourseBase> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("ja");
        queryCourseParamsDto.setAuditStatus("202001");
        queryCourseParamsDto.setPublishStatus("203001");
        //封装等值查询条件
        lambdaQueryWrapper.eq(CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus())
                .eq(CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //封装模糊查询条件
        lambdaQueryWrapper.like(CourseBase::getName,queryCourseParamsDto.getCourseName());

        //封装分页参数
        PageParams pageParams = new PageParams();
        pageParams.setPageNo(1L);
        pageParams.setPageSize(10L);
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        //分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, lambdaQueryWrapper);
        List<CourseBase> records = courseBasePage.getRecords();
        long current = courseBasePage.getCurrent();//当前页码
        long pageSize = courseBasePage.getSize();//每页记录数
        long total = courseBasePage.getTotal();//总数

        PageResult<CourseBase> result = new PageResult<>(current,pageSize,records,total);
        System.out.println("result = " + result);

    }
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Test
    public void testSelectTreeNodeMapper(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1-1");
        System.out.println(courseCategoryTreeDtos);
    }
    @Test
    public void test(){
        List<CourseCategory> courseCategoryTreeDtos = courseCategoryMapper.selectAll("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
