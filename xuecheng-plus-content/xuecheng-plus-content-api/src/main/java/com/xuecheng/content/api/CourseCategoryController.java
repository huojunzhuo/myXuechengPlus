package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: CourseCategoryController
 * Package: com.xuecheng.content.api
 * Description:
 *
 * @Author huojz
 * @Create 2023/10/9 19:23
 * @Version 1.0
 */
@Api(value = "分类查询接口",tags = "分类查询接口")
@RestController
@Slf4j
public class CourseCategoryController {
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        return null;
    }

}
