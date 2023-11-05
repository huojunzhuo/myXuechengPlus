package com.xuecheng.content.service.jobhandler;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: CoursePublishTask
 * Package: com.xuecheng.content.service.jobhandler
 * Description: 课程发布任务处理类
 * @Author huojz
 * @Create 2023/11/2 19:56
 * @Version 1.0
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    //任务调度入口
//    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler(){
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        this.process(shardIndex,shardTotal,"course_publish",30,60);
    }

    /**
     * 发布课程任务
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        String businessKey1  = mqMessage.getBusinessKey1();
        long courseId = Long.parseLong(businessKey1);
        //课程静态化
        generateCourseHtml(mqMessage,courseId);
        //课程索引
        saveCourseIndex(mqMessage,courseId);
        //课程缓存
        saveCourseCache(mqMessage,courseId);
        return false;
    }

    /**
     * 生成课程静态化页面并上传至文件系统
     * @param mqMessage 消息
     * @param courseId 课程id
     */
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        log.debug("开始进行课程静态化,课程id:{}",courseId);
        //消息id
        Long id = mqMessage.getId();
        //消息处理的service
        //抽象父类MessageProcessAbstract的属性:MqMessageService权限修饰符为缺省，在包外无法直接使用
        MqMessageService mqMessageService = this.getMqMessageService();
        //获取任务1的状态
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne>0){
            log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
            return;
        }
        //执行课程静态化的代码
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //保存第一阶段状态为完成
        mqMessageService.completedStageOne(id);

    }

    /**
     * 保存课程索引
     * @param mqMessage 消息
     * @param courseId 课程id
     */

    public void saveCourseIndex(MqMessage mqMessage,long courseId){

        log.debug("保存课程索引信息,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 缓存课程发布信息
     * @param mqMessage 消息
     * @param courseId 课程id
     */
    private void  saveCourseCache (MqMessage mqMessage,long courseId){
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();

        log.debug("将课程信息缓存至redis,课程id:{}",courseId);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        mqMessageService.completedStageThree(id);

    }


}
