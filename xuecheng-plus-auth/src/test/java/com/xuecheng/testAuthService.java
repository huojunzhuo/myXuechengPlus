package com.xuecheng;

import com.xuecheng.ucenter.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * ClassName: testAuthService
 *
 * @Description AuthService
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 15:08
 */
@SpringBootTest
public class testAuthService {
    @Autowired
    ApplicationContext applicationContext;
    @Test
    public void test(){
        AuthService authService1 =  applicationContext.getBean("passwordAuthServiceImpl",AuthService.class);
        System.out.println("authService1 = " + authService1);
        AuthService authService2 =  applicationContext.getBean("authServiceImpl",AuthService.class);
        System.out.println("authService2 = " + authService2);
    }

}
