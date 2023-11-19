package com.xuecheng.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ClassName: CheckCodeClient
 *
 * @Description 远程调用验证码服务生成验证码
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 14:33
 */

@FeignClient(value = "checkcode",fallbackFactory = CheckCodeClientFactory.class)
public interface CheckCodeClient {

    @PostMapping(value = "/checkcode/verify")
    public Boolean verify(@RequestParam ("key")String key, @RequestParam("code") String code);
}

