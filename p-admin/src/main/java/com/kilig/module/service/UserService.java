package com.kilig.module.service;

import com.kilig.module.dto.UpdatePasswordParam;
import com.kilig.module.dto.UserRegisterParam;
import com.kilig.module.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author L.Willian
 * @date 2020/1/20
 */
public interface UserService {
    /**
     * 根据用户名获取用户信息
     */

    User getUserByUsername(String username);

    /**
     * 注册功能
     */
    @Transactional
    User register(UserRegisterParam userRegisterParam);

    /**
     * 登录功能
     */

    String login(String username, String password);

    /**
     * 刷新token的功能
     */
    String refreshToken(String oldToken);

    /**
     * 修改密码
     */
    int updatePassword(UpdatePasswordParam updatePasswordParam);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByUsername(String username);

    /**
     * 修改当前用户信息
     *
     * @param user
     * @return
     */
    int updateInfo(Integer id, User user);
}
