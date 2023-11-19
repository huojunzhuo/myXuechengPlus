package com.xuecheng.ucenter.feignclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * ClassName: CheckCodeClientFactory
 *
 * @Description CheckCodeClientFactory
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 14:34
 */
@Slf4j
@Component
public class CheckCodeClientFactory implements FallbackFactory<CheckCodeClient> {
    /**
     * Returns an instance of the fallback appropriate for the given cause.
     * @param throwable cause of an exception.
     * @return fallback
     */
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {

            @Override
            public Boolean verify(String key,String code){
                log.debug("调用验证码服务熔断异常:{}", throwable.getMessage());
                return null;
            }
        };
    }
}
