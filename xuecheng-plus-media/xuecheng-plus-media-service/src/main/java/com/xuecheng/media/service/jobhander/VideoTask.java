package com.xuecheng.media.service.jobhander;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: VideoTask
 * @Description 视频处理任务类
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 28 16:22
 */
@Slf4j
@Component
public class VideoTask {
    @Autowired
    MediaFileService mediaFileService;
    @Autowired
    MediaFileProcessService mediaFileProcessService;


    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;

    @XxlJob("videoJobHandler")
    public void videoJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcessList = null;
        int size = 0;
        try {
            //取出cpu核心数作为一次处理数据的条数
            int processors = Runtime.getRuntime().availableProcessors();
            //一次处理视频数量不要超过cpu核心数
            mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
            size = mediaProcessList.size();
            log.debug("取出待处理视频任务{}条", size);
            if (size <= 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //启动size个线程的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        //将处理任务加入线程池
        mediaProcessList.forEach(mediaProcess -> {
            threadPool.execute(
                    () -> {
                        try {
                            //对集合中的每个任务元素执行处理逻辑
                            doMediaProcess(mediaProcess);
                        }
                        finally {
                            //执行完毕，执行计数器
                            countDownLatch.countDown();
                        }
                    }
            );
        });
    }

    //对每个集合元素抽取执行逻辑
    private void doMediaProcess(MediaProcess mediaProcess){

        //任务id
        Long taskId = mediaProcess.getId();
        //抢占任务，获取锁
        boolean b = mediaFileProcessService.startTask(taskId);
        if (!b) {
            //抢占失败
            return;
        }
        log.debug("开始执行任务:{}", mediaProcess);
        //任务处理逻辑
        //桶
        String bucket = mediaProcess.getBucket();
        //存储路径
        String filePath = mediaProcess.getFilePath();
        //原始视频的md5值
        String fileId = mediaProcess.getFileId();
        //原始文件名称
        String filename = mediaProcess.getFilename();
        //将要处理的文件下载到服务器上
        File file = mediaFileService.downloadFileFromMinIO(bucket, filePath);
        if (file == null) {
            log.debug("下载待处理文件失败,originalFile:{}", mediaProcess.getBucket().concat(mediaProcess.getFilePath()));
            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, bucket.concat(filePath), "下载待处理文件失败");
            return;
        }
        //处理下载后的文件
        File mp4File = null;
        try {
            mp4File = File.createTempFile("temp", ".mp4");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建mp4临时文件失败");
            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId, bucket.concat(filePath), "创建mp4临时文件失败");
            return;
        }
        //视频处理结果
        String result = "";
        try {
            //开始处理视频
            Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath, file.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
            //开始视频转换，成功将返回success
            result = videoUtil.generateMp4();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
        }
        if(!result .equals("success")){
            //记录报错信息
            log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId,null, result);
            return;
        }
        //将mp4上传至minio
        //minio存储路径
        String objectName = getFilePath(fileId, ".mp4");
        //访问url
        String url = "/" + bucket + "/" + objectName;
        try {
            mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
            //将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
            mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(), "2", fileId, url, null);
        }catch (Exception e)
        {
            e.printStackTrace();
            log.debug("上传视频失败或入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
            mediaFileProcessService.saveProcessFinishStatus(taskId, "3", fileId,null, "处理后上传视频失败或入库失败,");
        }
    }


    /**
     * 私有方法：根据fileMd5及扩展名获取文件存储路径
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private  String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }


}
