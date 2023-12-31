package com.xuecheng.content.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.JsonUtil;
import com.xuecheng.base.utils.StringUtil;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: CoursePublishServiceImpl
 * Package: com.xuecheng.content.service.impl
 * Description: CoursePublishServiceImpl
 *
 * @Author huojz
 * @Create 2023/10/30 19:53
 * @Version 1.0
 */
@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseTeacherService courseTeacherService;
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    private CoursePublishMapper coursePublishMapper;

    /**
     * @param courseId 课程id
     * @return File 静态化文件
     * @description 课程静态化
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public File generateCourseHtml(Long courseId) {

        File htmlfile = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());
            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setClassForTemplateLoading(this.getClass(), "/templates/");
            // configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");
            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");
            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);
            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlfile = File.createTempFile("course", ".html");
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlfile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}", e.toString());
            XueChengPlusException.cast("课程静态化异常");
        }
        return htmlfile;
    }
    @Autowired
    MediaServiceClient mediaServiceClient;
    /**
     * @param courseId
     * @param file     静态化文件
     * @return void
     * @description 上传课程静态化页面
     * @author Mr.M
     * @date 2022/9/23 16:59
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        //调用配置类获取MultipartFile类型文件
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        //远程调用媒资服务接口上传文件
        String course = mediaServiceClient.uploadFile(multipartFile, "course/" + courseId + ".html");
        if(course == null){
            XueChengPlusException.cast("上传文件异常");
        }
    }

    /**
     * 课程发布接口实现方法
     * @param companyId 机构id
     * @param courseId  课程id
     */
    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        // 1.预发布表有数据
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("请先提交审核再发布");
        }
        // 2.判断课程状态已审核通过
        String audiStatus = coursePublishPre.getStatus();
        if (!"202004".equals(audiStatus)) {
            XueChengPlusException.cast("请先审核通过，方可发布");
        }
        // 3.本机构只可发布本机构的课程
        if (!companyId.equals(coursePublishPre.getCompanyId())) {
            XueChengPlusException.cast("本机构只可发布本机构的课程");
        }
        // 4.保存课程发布信息
        saveCoursePublish(courseId);
        // 5.向消息表中存储数据
        saveCoursePublishMessage(courseId);
        // 6.删除课程预发布表数据
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 保存课程发布信息到发布表私有方法
     *
     * @param courseId 课程id
     */
    private void saveCoursePublish(Long courseId) {
        // 1.查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("请先提交审核再发布");
        }
        // 2.查询课程发布表
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null) {
            coursePublish = new CoursePublish();
            BeanUtil.copyProperties(coursePublishPre, coursePublish);
            coursePublish.setStatus("203002");
            // 3.如果没有记录则插入课程发布表
            int insert = coursePublishMapper.insert(coursePublish);
            if (insert <= 0) {
                XueChengPlusException.cast("保存课程发布信息失败");
            }
        } else {
            //如果有记录则更新
            coursePublishMapper.updateById(coursePublish);
        }

        // 4.更新课程基本信息表
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        int update = courseBaseMapper.updateById(courseBase);
        if (update <= 0) {
            XueChengPlusException.cast("更新课程基本信息表发布状态字段失败");
        }
    }

    @Autowired
    MqMessageService mqMessageService;

    /**
     * 向消息表中存储记录
     *
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }

    }


    /**
     * @param courseId 课程id
     * @return void
     * @description 课程提交审核
     */
    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("当前课程不存在");
        }
        //提交审核的不允许重复提交
        if ("202003".equals(courseBase.getAuditStatus())) {
            XueChengPlusException.cast("已提交审核，请勿重复提交");
        }
        //本机构只允许提交本机构的课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("不允许提交其它机构的课程");
        }
        //课程图片未上传的不允许提交
        if (StringUtil.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("课程图片为空");
        }

        //封装返回对象
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //获取课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        BeanUtil.copyProperties(courseBaseInfo, coursePublishPre);
        //获取课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (ObjectUtil.isEmpty(courseMarket)) {
            XueChengPlusException.cast("课程营销信息不存在");
        }
        String courseMarketJsonString = JsonUtil.objectTojson(courseMarket);
        coursePublishPre.setMarket(courseMarketJsonString);
        //获取课程计划信息
        List<TeachplanDto> teachplanDtoList = teachplanService.getTreeNodes(courseId);
        if (ObjectUtil.isEmpty(teachplanDtoList)) {
            XueChengPlusException.cast("课程计划不存在不允许提交审核");
        }
        String teachplanDtoListJstr = JsonUtil.listTojson(teachplanDtoList);
        //设置课程计划信息
        coursePublishPre.setTeachplan(teachplanDtoListJstr);
        //查询课程师资信息
        List<CourseTeacher> courseTeacherList = courseTeacherService.findCourseTeacher(courseId);
        String courseTeacherListJstr = JsonUtil.listTojson(courseTeacherList);
        //设置课程师资信息
        coursePublishPre.setTeachers(courseTeacherListJstr);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        coursePublishPre.setStatus("202003");
        coursePublishPre.setValidDays(365);

        //查询课程预发布表
        CoursePublishPre coursePublishPreSelect = coursePublishPreMapper.selectById(courseId);
        //如果没有记录则插入记录
        if (coursePublishPreSelect == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本信息表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //调用已有接口查询结果
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        List<TeachplanDto> TeachplanDtos = teachplanService.getTreeNodes(courseId);
        List<CourseTeacher> courseTeachers = courseTeacherService.findCourseTeacher(courseId);

        //封装返回结果对象模型类：CoursePreviewDto
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(TeachplanDtos);
        coursePreviewDto.setCourseTeachers(courseTeachers);
        return coursePreviewDto;
    }
}
