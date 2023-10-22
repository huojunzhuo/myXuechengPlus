package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import com.xuecheng.content.model.po.TeachplanMedia;
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
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return
     */
    @Override
    public List<Teachplan> selectByCourseId(Long courseId) {
        List<Teachplan> teachplans = teachplanMapper.selectList(new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getCourseId, courseId));
        return teachplans;
    }

    /**
     * 查询课程计划
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

    /**
     * 删除课程计划
     * @return
     */
    @Transactional
    @Override
    public boolean deleteTeachPlan(Long id) {
        //判断是一级节点还是二级节点
        Teachplan teachplan = teachplanMapper.selectById(id);
        Integer grade = teachplan.getGrade();
        if (ObjectUtil.isNull(grade)) {
            XueChengPlusException.cast("查询到的grade字段为空");
        }
        //如果是一级节点
        if (grade.equals(1)) {
            //查询一级节点下的二级子节点
            List<Teachplan> teachplansSecondLines = teachplanMapper.selectList(new LambdaQueryWrapper<Teachplan>()
                    .eq(Teachplan::getCourseId, teachplan.getCourseId())
                    .eq(Teachplan::getParentid, id));
            if (CollectionUtil.isEmpty(teachplansSecondLines)) {
                //如果二级节点查询结果为空，执行删除逻辑
                int deleteById = teachplanMapper.deleteById(id);
                if (deleteById <= 0) {
                    XueChengPlusException.cast("删除章节失败");
                }
                return true;
            }
            //如果子节点不为空，抛异常
            XueChengPlusException.cast("删除一级节点前，请确保二级节点已经删除完成");
        }
        //如果是二级节点
        else {
            //删除二级节点
            int deleteById = teachplanMapper.deleteById(id);
            if (deleteById <= 0) {
                log.error("删除二级节点课程计划失败teachplanId:{}", id);
                XueChengPlusException.cast("删除二级节点课程计划失败");
            }
            //删除媒资关联表
            int delete = teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                    .eq(TeachplanMedia::getCourseId, teachplan.getCourseId())
                    .eq(TeachplanMedia::getTeachplanId, id));
            if (delete < 0) {
                log.error("删除媒资关联表失败teachplanId:{}", id);
                XueChengPlusException.cast("删除媒资关联表失败");
            }

        }
        return true;
    }

    /**
     * 向上移动课程计划
     * @param id
     * @return
     */
    @Transactional
    @Override
    public boolean moveUp(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long courseId = teachplan.getCourseId();
        Integer grade = teachplan.getGrade();
        Integer orderby = teachplan.getOrderby();
        //查询同级所有课程计划
        List<Teachplan> teachplanListOrdered = teachplanMapper.selectList(new LambdaQueryWrapper<Teachplan>()
                .eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getGrade, grade)
                .eq(Teachplan::getParentid, teachplan.getParentid())
                .orderBy(true, true, Teachplan::getOrderby)
        );
        int index = 0;
        for (Teachplan item : teachplanListOrdered) {
            if (!item.getId().equals(id)) {
                index++;
            } else {
                break;
            }
        }
        if (index == 0) {
            XueChengPlusException.cast("所选计划已经位于最前面，无法再次向前");
        }
        Teachplan teachplanUp = teachplanListOrdered.get(index - 1);
        int orderByTemp = teachplanUp.getOrderby();
        teachplanUp.setOrderby(orderby);
        int i = teachplanMapper.updateById(teachplanUp);
        teachplan.setOrderby(orderByTemp);
        int j = teachplanMapper.updateById(teachplan);
        if (i <= 0 || j <= 0) {
            XueChengPlusException.cast("更新课程计划顺序字段失败");
        }
        return true;
    }
}




