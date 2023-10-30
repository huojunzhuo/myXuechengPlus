package com.xuecheng.media.api;

/**
 * ClassName: MediaOpenController
 * Package: com.xuecheng.media.api
 * Description: MediaOpenController
 *
 * @Author huojz
 * @Create 2023/10/30 20:51
 * @Version 1.0
 */

import cn.hutool.core.bean.BeanUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;
    @ApiOperation("预览视频")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String>  getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId){
        MediaFiles mediaFile = mediaFileService.getById(mediaId);
        if (BeanUtil.isEmpty(mediaFile)) {
            XueChengPlusException.cast("视频还没有转码处理");
        }
        return RestResponse.success(mediaFile.getUrl());
    }

}
