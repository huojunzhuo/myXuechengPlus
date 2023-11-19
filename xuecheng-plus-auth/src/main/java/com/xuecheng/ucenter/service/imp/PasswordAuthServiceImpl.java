package com.xuecheng.ucenter.service.imp;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.AuthService;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * ClassName: PasswordAuthServiceImpl
 *
 * @Description 账号密码+验证码service实现
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 14:45
 */
@Service
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CheckCodeClient checkCodeClient;

    /**
     * 认证方法
     * @param authParamsDto
     * @return 用户数据模型类
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        //校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();
        if (BeanUtil.isEmpty(checkcode) || BeanUtil.isEmpty(checkcodekey)) {
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
            throw new RuntimeException("验证码输入错误");
        }
        //获取账号
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (BeanUtil.isEmpty(xcUser)) {
            throw new RuntimeException("账号不存在");
        }
        //账号存在：校验密码
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtil.copyProperties(xcUser,xcUserExt);
        //取出数据库存储的正确密码
        String passwordDb = xcUser.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }
        return xcUserExt;
    }
}
