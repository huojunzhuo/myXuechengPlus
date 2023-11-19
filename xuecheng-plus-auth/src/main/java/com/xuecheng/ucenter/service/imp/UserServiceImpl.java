package com.xuecheng.ucenter.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.AuthService;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserServiceImpl
 *
 * @Description 自定义UserServiceImpl
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 9:24
 */
@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    ApplicationContext applicationContext;


    /**
     * 根据账号信息去查询用户信息
     *
     * @param s the username identifying the user whose data is required.
     * @return UserDetails
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSONUtil.toBean(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证项目不符合要求：{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        String authType = authParamsDto.getAuthType();
        AuthService authService = applicationContext.getBean(AuthService.class);
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        return getUserPrincipal(xcUserExt);
    }

    /**
     * 获取用户返回信息
     * @param xcUserExt
     * @return
     */
    private UserDetails getUserPrincipal(XcUserExt xcUserExt){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
        String password = xcUserExt.getPassword();
        //为了安全在令牌中不放密码
        xcUserExt.setPassword(null);
        //将user转JSON字符串,生成的jwt令牌可以存储信息
        String userString = JSONUtil.toJsonStr(xcUserExt);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        return userDetails;
    }
}
