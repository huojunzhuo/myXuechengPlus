package com.xuecheng.media.model;

import lombok.Data;

/**
 * ClassName: Student
 * Package: com.xuecheng.media.com.xuecheng.media.model
 * Description: Student
 *
 * @Author huojz
 * @Create 2023/10/27 16:37
 * @Version 1.0
 */
@Data
public class Student {
    /**
     * 姓名
     */
    private String name;
    /**
     * 班级
     */
    private String schoolClass;

    /**
     *语文成绩
     */
    private Integer chineseScore;
    /**
     * 数学成绩
     */
    private Integer MathScore;
}
