package com.xuecheng;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.xuecheng.content.service.CourseCategoryService;
import com.xuecheng.testPo.Home;
import com.xuecheng.testPo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<CourseCategoryTreeDto> courseCategories = courseCategoryMapper.selectTreeNodes("1-1");
        System.out.println(courseCategories);
    }
    @Autowired
    CourseCategoryService courseCategoryService;

    /**
     * 根据id查询树形结构(Mysql8.0递归查询)
     */
    @Test
    public void testCourseCategoryService(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }

    /**
     * 根据id查询树形结构(Mysql8.0递归查询)-不同的service方法
     */
    @Test
    public void testCourseCategoryService1(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes1("1");
        System.out.println(courseCategoryTreeDtos);
    }

    //java递归查询树形结构代码
    @Test
    public void testTreeSelect(){
        String id = "1";
        List<CourseCategoryTreeDto> list = courseCategoryMapper.selectAllList();
        List<CourseCategoryTreeDto> collectReturn = list.stream().filter(p -> p.getParentid().equals(id))
                .peek(treeEntity -> treeEntity.setChildrenTreeNodes(this.buildTree(treeEntity,list)))
                .collect(Collectors.toList());
        String s = JSONUtil.toJsonStr(collectReturn);
        System.out.println(s);
    }
    private List<CourseCategoryTreeDto> buildTree(CourseCategoryTreeDto treeEntity, List<CourseCategoryTreeDto> list) {
        List<CourseCategoryTreeDto> collect = list.stream()
                .filter(p -> p.getParentid().equals(treeEntity.getId()))
                .peek(q -> q.setChildrenTreeNodes(buildTree(q, list)))
                .collect(Collectors.toList());
        return collect;
    }
    //测试java递归查询树形结构
    @Test
    public void testtestTreeSelect2(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes2("1");
        String jsonStr = JSONUtil.toJsonStr(courseCategoryTreeDtos);
        System.out.println(jsonStr);
    }
    //测试内连接查询封装属性数据
    @Test
    public void testInnerJoinSelect(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodesByUnion();
        String s = JSONUtil.toJsonStr(courseCategoryTreeDtos);
        System.out.println(s);
    }

}
