package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * ClassName: MediaFileProcessService
 *
 * @Description 媒资文件处理接口
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 28 15:27
 */
public interface MediaFileProcessService {
    /**
     * 获取任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片数量
     * @param count 提取任务数
     * @return
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 获取数据库分布式锁开启任务
     * @param id 任务id
     * @return 更新结果
     */
    public boolean startTask(long id);

    /**
     * 保存任务处理结果
     * @param taskId 任务id
     * @param status 状态
     * @param fileId 文件id
     * @param url 路径
     * @param errorMsg 错误信息
     */
    public void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);



}
