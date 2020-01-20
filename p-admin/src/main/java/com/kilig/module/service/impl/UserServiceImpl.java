package com.kilig.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.kilig.module.dto.UpdatePasswordParam;
import com.kilig.module.dto.UserRegisterParam;
import com.kilig.module.entity.*;
import com.kilig.module.mapper.RoleMapper;
import com.kilig.module.mapper.UserMapper;
import com.kilig.module.mapper.UserRoleMapper;
import com.kilig.module.service.UserService;
import com.kilig.module.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author L.Willian
 * @date 2020/1/20
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;


    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public User register(UserRegisterParam userRegisterParam) {
        User user = new User();
        BeanUtil.copyProperties(userRegisterParam, user);
        user.setCreateTime(new Date());
        //查询是否有相同的用户
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> userList = userMapper.selectByExample(example);
        if (userList.size() > 0) return null;
        //对密码进行加密
        String encodePwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);
        int count = userMapper.insert(user);
        //添加默认ROLE_NORMAL权限
        if (count > 0) {
            UserRole userRole = new UserRole();
            RoleExample roleExample = new RoleExample();
            roleExample.createCriteria().andNameEqualTo("ROLE_NORMAL");
            List<Role> roleList = roleMapper.selectByExample(roleExample);
            userRole.setRoleId(roleList.get(0).getId());
            userRole.setUserId(user.getId());
            userRoleMapper.insert(userRole);
        }
        return user;
    }

    @Override
    public String login(String username, String password) {


        return null;
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public int updatePassword(UpdatePasswordParam updatePasswordParam) {
        return 0;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return null;
    }
}
