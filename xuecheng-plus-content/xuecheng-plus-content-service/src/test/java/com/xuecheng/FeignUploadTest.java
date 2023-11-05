package com.xuecheng;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * ClassName: FeignUploadTest
 *
 * @Description 测试使用feign远程上传文件
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 05 14:22
 */
@SpringBootTest 
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;
    
    @Test
    public void test(){
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\develop\\1.html"));
        mediaServiceClient.uploadFile(multipartFile,"course");
    }

}
