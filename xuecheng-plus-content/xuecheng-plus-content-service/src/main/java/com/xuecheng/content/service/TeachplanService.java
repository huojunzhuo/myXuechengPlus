package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* @author HJZ
* @description 针对表【teachplan(课程计划)】的数据库操作Service
* @createDate 2023-10-15 11:30:13
*/
public interface TeachplanService extends IService<Teachplan> {

    /**
     * 删除课程计划绑定媒资
     * @param teachPlanId  课程计划id
     * @param mediaId 媒资文件id
     * @return
     */
    public int deleteAssociationMedia( Long teachPlanId, String mediaId);

    /**
     * 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * 查询课程计划
     * @param courseId
     * @return
     */
    public List<TeachplanDto> getTreeNodes( Long courseId);

    /**
     * 新增课程计划
     * @param saveTeachplanDto
     * @return 新增结果
     */
    public boolean saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 修改课程计划
     * @param saveTeachplanDto
     * @return
     */
    public boolean updateTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @return
     */
    public boolean deleteTeachPlan(Long id);

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return
     */
    public List<Teachplan>  selectByCourseId(Long courseId);


    /**
     * 向上移动课程计划
     * @param id
     * @return
     */
    public boolean moveUp(Long id);
}
