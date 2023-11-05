package com.xuecheng;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;


import freemarker.template.TemplateException;
import org.apache.logging.log4j.core.util.IOUtils;
import org.codehaus.groovy.tools.shell.IO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * ClassName: FreemarkerTest
 * Package: com.xuecheng
 * Description: FreemarkerTest
 *
 * @Author huojz
 * @Create 2023/11/2 20:49
 * @Version 1.0
 */
@SpringBootTest
public class FreemarkerTest {
    @Autowired
    CoursePublishService coursePublishService;
    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());

        //加载模板
        //选指定模板路径,classpath下templates下
        //得到classpath路径
        String classpath = this.getClass().getResource("/").getPath();

        configuration.setClassForTemplateLoading(this.getClass(), "/templates/");
//        configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));

        //设置字符编码
        configuration.setDefaultEncoding("utf-8");

        //指定模板文件名称
        Template template = configuration.getTemplate("course_template.ftl");

        //准备数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(117L);

        Map<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewInfo);
        //静态化
        //参数1：模板，参数2：数据模型
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        System.out.println(content);
        //将静态化内容输出到文件中
        InputStream inputStream = org.apache.commons.io.IOUtils.toInputStream(content);
        //输出流
        FileOutputStream outputStream = new FileOutputStream("D:\\develop\\test.html");
        org.apache.commons.io.IOUtils.copy(inputStream, outputStream);
    }

}
