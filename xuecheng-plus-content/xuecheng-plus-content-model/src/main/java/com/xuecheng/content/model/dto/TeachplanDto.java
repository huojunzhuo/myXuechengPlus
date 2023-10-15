package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * ClassName: TeachplanDto
 *
 * @Description TeachplanDto课程计划树形结构模型类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 15 11:41
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {

    /**
     * 关联的媒资文件信息
     */
    TeachplanMedia teachplanMedia;

    /**
     * 关联的子节点
     */
    List<TeachplanDto> teachPlanTreeNodes;
}
