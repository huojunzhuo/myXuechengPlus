package com.xuecheng.media;

import cn.hutool.core.bean.BeanUtil;
import com.xuecheng.media.model.Data;
import com.xuecheng.media.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public void testGetFolderPath() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = simpleDateFormat.format(new Date()) + "/";
        System.out.println(format);

    }

    @Test
    public void testStream() {
        List<Student> students = Data.initData();
        List<Student> studentList = students.stream().map(student -> {
            if (student.getName().equals("王五")) {
                Student s = new Student();
                BeanUtil.copyProperties(student, s);
                s.setMathScore(1);
                return s;
            }
            return student;
        }).collect(Collectors.toList());
//        studentList.stream().forEach(System.out::println);
        System.out.println("原list流");
//        students.stream().forEach(System.out::println);
        List<Integer> mathScoreList = studentList.stream()
                .filter(student -> student.getMathScore().equals(1))
                .map(student -> student.getMathScore())
                .collect(Collectors.toList());
        mathScoreList.stream().forEach(System.out::println);
    }

}
