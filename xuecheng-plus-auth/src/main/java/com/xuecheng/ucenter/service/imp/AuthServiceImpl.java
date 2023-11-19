package com.xuecheng.ucenter.service.imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.AuthService;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ClassName: AuthServiceImpl
 *
 * @Description 单独账号密码校验
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 10:59
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    /**
     * 认证方法
     * @param authParamsDto
     * @return 用户数据模型类
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser user  = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if(BeanUtil.isEmpty(user)){
            //如果账号不存在，则返回null
            return null;
        }
        //账号存在
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtil.copyProperties(user,xcUserExt);
        //密码模式：校验密码
        //取出用户密码
        String passwordFromDb = user.getPassword();
        String passwordFrom = authParamsDto.getPassword();
        boolean isMatched = passwordEncoder.matches(passwordFrom, passwordFromDb);
        if(!isMatched){
            throw new RuntimeException("账号或密码错误");
        }
        return xcUserExt;
    }
}
