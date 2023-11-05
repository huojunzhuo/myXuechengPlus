package com.xuecheng.content.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: MediaServiceClientFallbackFactory
 *
 * @Description 熔断降级工厂
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 05 15:27
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

    /**
     * 编写降级方法
     * Returns an instance of the fallback appropriate for the given cause.
     * @param throwable 远程调用服务捕获到的异常
     * @return fallback
     */
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile upload, String objectName) {
                //降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}",throwable.toString(),throwable);
                return null;
            }
        };
    }
}
