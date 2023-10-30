package com.xuecheng.content.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanMediaService;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TeachplanController
 * @Description TeachplanController
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 15 11:46
 */
@RestController
@Api(value = "教学计划编辑接口", tags = "课程计划编辑接口")
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("删除课程计划和媒资绑定关系")
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    public RestResponse deleteAssociationMedia(@PathVariable Long teachPlanId,@PathVariable String mediaId){
        int i = teachplanService.deleteAssociationMedia(teachPlanId, mediaId);
        if (i <= 0 ) {
            return RestResponse.validfail("删除课程计划和媒资绑定关系失败");
        }
        return RestResponse.success();
    }

    @ApiOperation("课程计划和媒资信息绑定")
    @PostMapping("/teachplan/association/media")
    public RestResponse<TeachplanMedia> associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        TeachplanMedia teachplanMedia = teachplanService.associationMedia(bindTeachplanMediaDto);
        if (BeanUtil.isEmpty(teachplanMedia)){
            return RestResponse.validfail("绑定失败");
        }
        return RestResponse.success(teachplanMedia);
    }

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachplanService.getTreeNodes(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody @Validated SaveTeachplanDto saveTeachplanDto) {
        //判断是否包含id
        Long id = saveTeachplanDto.getId();
        if (ObjectUtil.isNull(id)) {
            //id为空，走新增逻辑
            boolean b = teachplanService.saveTeachplan(saveTeachplanDto);
            if (!b) {
                XueChengPlusException.cast("新增课程计划失败");
            }
            return;
        }
        boolean b = teachplanService.updateTeachplan(saveTeachplanDto);
        if (!b) {
            XueChengPlusException.cast("修改课程计划失败");
        }
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{id}")
    public String deleteTeachplan(@PathVariable Long id) {
        boolean b = teachplanService.deleteTeachPlan(id);
        if (b) {
            return "success";
        }
        return "fail";
    }

    @ApiOperation("向上移动课程计划")
    @GetMapping("/teachplan/{id}")
    public String moveUp(@PathVariable Long id) {
        boolean b = teachplanService.moveUp(id);
        if (b) {
            return "success";
        }
        return "fail";
    }
}
