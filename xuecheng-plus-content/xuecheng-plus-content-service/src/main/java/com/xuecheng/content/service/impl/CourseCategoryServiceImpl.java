package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huojz
 * @description 针对表【course_category(课程分类)】的数据库操作Service实现
 * @createDate 2023-10-09 19:11:09
 */
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory>
        implements CourseCategoryService {


    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //1.调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtosSelect = courseCategoryMapper.selectTreeNodes(id);
        //2.找到每个节点的子节点，最终封装成List<CourseCategoryTreeDto>作为返回值
        //定义list作为最终返回值
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList();
        //2.1遍历查询出来的结果，转换成Map结构 过滤掉父节点（符合filter条件的留下，为true保留）
        Map<String, CourseCategoryTreeDto> treeDtoMap = courseCategoryTreeDtosSelect.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value, (key1, key2) -> key2));
        //2.2遍历查询结果找到每个父节点放入直接放入集合，找到每个节点的子节点，放入父节点childrenNode属性中
        courseCategoryTreeDtosSelect.stream()
                .filter(item -> !id.equals(item.getId()))
                .forEach(item -> {
                            //找出id的直接子节点，直接放入返回值List中
                            if (id.equals(item.getParentid())) {
                                categoryTreeDtos.add(item);
                            } else {
                                //如果不是id的直接子节点，根据map找到该节点的父节点
                                CourseCategoryTreeDto courseCategoryParent = treeDtoMap.get(item.getParentid());
                                //在map中找到的该父节点才执行对应代码
                                if (courseCategoryParent != null) {
                                    //获取该父节点的childrenTreeNodes属性
                                    List<CourseCategoryTreeDto> childrenTreeNodes = courseCategoryParent.getChildrenTreeNodes();
                                    //判断childrenTreeNodes属性是否为空
                                    if (childrenTreeNodes == null) {
                                        //如果父节点的属性为空，则需要new一个
                                        childrenTreeNodes = new ArrayList<>();
                                        courseCategoryParent.setChildrenTreeNodes(childrenTreeNodes);
                                    }
                                    //如果不为空，则直接将item加入childrenTreeNodes里面
                                    childrenTreeNodes.add(item);
                                }
                            }
                        }
                );

        return categoryTreeDtos;
    }
    //备选方法
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes1(String id) {
        //1.调用mapper递归查询出分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtosSelect = courseCategoryMapper.selectTreeNodes(id);
        //2.找到每个节点的子节点，最终封装成List<CourseCategoryTreeDto>作为返回值
        //定义list作为最终返回值
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList();
        //2.1遍历查询出来的结果，转换成Map结构 过滤掉父节点（符合filter条件的留下，为true保留）
        Map<String, CourseCategoryTreeDto> mepTemp = courseCategoryTreeDtosSelect.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toMap(CourseCategoryTreeDto::getId, value -> value, (key1, key2) -> key2));
        //2.2找到每个节点的子节点，放入childrenNode属性中
        for (CourseCategoryTreeDto item : courseCategoryTreeDtosSelect) {
            if (id.equals(item.getParentid())){
                categoryTreeDtos.add(item);
            }else {

                CourseCategoryTreeDto courseCategoryTreeParent = mepTemp.get(item.getParentid());
                if (courseCategoryTreeParent != null){
                    if (courseCategoryTreeParent.getChildrenTreeNodes()==null){
                        List<CourseCategoryTreeDto> childrenTreeNodes = new ArrayList<CourseCategoryTreeDto>();
                        courseCategoryTreeParent.setChildrenTreeNodes(childrenTreeNodes);
                    }
                    courseCategoryTreeParent.getChildrenTreeNodes().add(item);
                }

            }
        }
        return categoryTreeDtos;
    }
}




