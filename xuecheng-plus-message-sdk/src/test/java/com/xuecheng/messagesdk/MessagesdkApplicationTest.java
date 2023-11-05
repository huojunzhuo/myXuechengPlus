package com.xuecheng.messagesdk;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class MessagesdkApplicationTest {
    @Autowired
    MessageProcessClass messageProcessClass;

    @Test
    public void testProcess() throws InterruptedException {
        System.out.println("开始执行-----》" + LocalDateTime.now());
        messageProcessClass.process(0, 1, "test", 5, 30);
        System.out.println("结束执行-----》" + LocalDateTime.now());
        Thread.sleep(900);
    }
}