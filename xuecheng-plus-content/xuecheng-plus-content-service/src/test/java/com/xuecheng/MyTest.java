package com.xuecheng;

import com.xuecheng.testPo.Home;
import com.xuecheng.testPo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: MyTest
 *
 * @Description MyTest
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 10 15 11:02
 */
@SpringBootTest
public class MyTest {
    @Test
    public void testUser(){
        List<User> userList = generatorList();
//        Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, account -> account, (key1, key2) -> key2));
//        userMap.get("1").setName("Tom1");
//        System.out.println(userMap.get("1").getName());
//        System.out.println(userList.get(0).getName());
//        userList.get(0).setName("Tom2");
//        System.out.println(userMap.get("1").getName());
//        System.out.println(userList.get(0).getName());

//        ArrayList<User> users = userList;
//        users.get(0).setName("Tom2");
//        System.out.println(userMap.get("1").getName());
//        System.out.println(userList.get(0).getName());
        Map<String, User> mapTemp = userList.stream().collect(Collectors.toMap(User::getId, account -> account, (key1, key2) -> key2));
        System.out.println(userList);
        System.out.println(mapTemp);
    }
    @Test
    public void testStream(){
        List<User> users = generatorList();
    }
    private List<User> generatorList(){
        User user1 = new User();
        user1.setName("Tom");
        user1.setBirthday(LocalDateTime.of(1998,7,25,0,0));
        user1.setHome(new Home("吉林","长春"));
        user1.setId("1");

        User user2 = new User();
        user2.setName("Jerry");
        user2.setBirthday(LocalDateTime.of(1992,7,25,0,0));
        user2.setHome(new Home("吉林","四平"));
        user2.setId("2");

        User user3 = new User();
        user3.setName("Merry");
        user3.setBirthday(LocalDateTime.of(1996,3,25,0,0));
        user3.setHome(new Home("吉林","松原"));
        user3.setId("3");

        ArrayList<User> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        return userList;
    }
}
