package com.xuecheng.ucenter;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * ClassName: AuthService
 *
 * @Description 认证service
 * @Author huojz
 * @project myXuechengPlus
 * @create 2023 11 19 10:55
 */
public interface AuthService {

    /**
     * 认证方法
     * @param authParamsDto
     * @return 用户数据模型类
     */
    XcUserExt execute(AuthParamsDto authParamsDto);

}
