package com.xuecheng.media.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName: MediaFileProcessServiceImpl

 * @Description MediaFileProcessService实现类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 28 15:28
 */
@Service
@Slf4j
public class MediaFileProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaFileProcessService {
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    /**
     * 获取任务列表
     * @param shardIndex 分片序号
     * @param shardTotal 分片数量
     * @param count      提取任务数
     * @return
     */
    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    /**
     * 获取数据库分布式锁开启任务
     * @param id 任务id
     * @return 更新结果
     */
    @Override
    public boolean startTask(long id) {
        int i = mediaProcessMapper.startTask(id);
        return i <= 0 ? false : true;
    }

    /**
     * 保存任务处理结果
     * @param taskId   任务id
     * @param status   状态
     * @param fileId   文件id
     * @param url      路径
     * @param errorMsg 错误信息
     */
    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(BeanUtil.isEmpty(mediaProcess)){
            //如果查询不到记录则直接返回
            return;
        }
        //处理失败、更新任务处理结果
        LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
        if(status.equals("3")){
            MediaProcess m = new MediaProcess();
            m.setStatus("3");
            m.setErrormsg(errorMsg);
            m.setFailCount(m.getFailCount()+1);
            mediaProcessMapper.update(m,queryWrapperById);
            log.debug("更新任务处理失败记录，任务信息:{}",m);
            return;
        }
        //任务处理成功
        //1.更新mediafile文件表
        MediaFiles mediaFile = mediaFilesMapper.selectById(fileId);
        if(BeanUtil.isNotEmpty(mediaFile)){
            mediaFile.setUrl(url);
            mediaFile.setChangeDate(LocalDateTime.now());
            mediaFilesMapper.updateById(mediaFile);
        }
        //2.更新mediaProcess表记录；
        mediaProcess.setStatus("2");
        mediaProcess.setFilePath(url);
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);
        //3.插入mediaProcessHistory表记录；
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtil.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //4.删除mediaProcess表记录；
        mediaProcessMapper.deleteById(mediaProcess);
    }

}
