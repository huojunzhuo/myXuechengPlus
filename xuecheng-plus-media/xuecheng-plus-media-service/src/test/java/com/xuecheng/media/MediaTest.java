package com.xuecheng.media;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName: com.xuecheng.media.MediaTest
 * Package: PACKAGE_NAME
 * Description: com.xuecheng.media.MediaTest
 *
 * @Author huojz
 * @Create 2023/10/24 19:27
 * @Version 1.0
 */
@SpringBootTest(classes = XuechengPlusMediaServiceApplication.class)
public class MediaTest {
    @Test
    public void testGetFolderPath(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = simpleDateFormat.format(new Date())+"/";
        System.out.println(format);

    }
}
