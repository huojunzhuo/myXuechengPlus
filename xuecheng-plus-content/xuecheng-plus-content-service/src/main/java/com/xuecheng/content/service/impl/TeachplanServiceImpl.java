package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HJZ
 * @description 针对表【teachplan(课程计划)】的数据库操作Service实现
 * @createDate 2023-10-15 11:30:13
 */
@Service
@Slf4j
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan>
        implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    /**
     * 查询课程计划
     *
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> getTreeNodes(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }


    /**
     * 新增课程计划
     *
     * @param saveTeachplanDto
     * @return 新增结果
     */
    @Override
    @Transactional
    public boolean saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Teachplan teachplan = new Teachplan();
        BeanUtil.copyProperties(saveTeachplanDto, teachplan);
        Long courseId = teachplan.getCourseId();
        teachplan.setCreateDate(LocalDateTime.now());
        int orderBy = findOrderBy(teachplan, courseId);
        teachplan.setOrderby(orderBy + 1);
        int insert = teachplanMapper.insert(teachplan);
        if (insert <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 修改课程计划
     * @param saveTeachplanDto
     * @return
     */
    @Override
    public boolean updateTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        Teachplan teachplanFromDb = teachplanMapper.selectById(id);
        if (BeanUtil.isEmpty(teachplanFromDb)) {
            return false;
        }
        Teachplan teachplan = new Teachplan();
        BeanUtil.copyProperties(saveTeachplanDto, teachplan);
        teachplan.setChangeDate(LocalDateTime.now());
        int update = teachplanMapper.updateById(teachplan);
        if (update <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取最新的排序序号
     * @param teachplan
     * @param courseId
     * @return
     */
    private int findOrderBy(Teachplan teachplan, Long courseId) {
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(ObjectUtil.isNotEmpty(teachplan.getCourseId()), Teachplan::getCourseId, courseId)
                .eq(ObjectUtil.isNotEmpty(teachplan.getParentid()), Teachplan::getParentid, teachplan.getParentid());
        Long count = teachplanMapper.selectCount(teachplanLambdaQueryWrapper);
        return count.intValue();
    }
}




