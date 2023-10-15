package com.xuecheng.testPo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: User
 *
 * @Description User
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 15 10:30
 */
@Data
public class User {
    String id;
    String name;
    LocalDateTime birthday;
    Home home;
}
