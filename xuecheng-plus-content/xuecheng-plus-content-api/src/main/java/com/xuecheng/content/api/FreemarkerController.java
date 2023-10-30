package com.xuecheng.content.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName: FreemarkerController
 * Package: com.xuecheng.content.api
 * Description: 模板引擎 freemarker测试
 *
 * @Author huojz
 * @Create 2023/10/30 19:23
 * @Version 1.0
 */
public class FreemarkerController {
    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("name","小明");
        //设置模板名称
        modelAndView.setViewName("test");
        return modelAndView;
    }

}
