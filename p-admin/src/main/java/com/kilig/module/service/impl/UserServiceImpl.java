package com.kilig.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.kilig.module.dao.UserRoleDao;
import com.kilig.module.dto.UpdatePasswordParam;
import com.kilig.module.dto.UserDetailsDto;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @Autowired
    private UserRoleDao userRoleDao;

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
        String token = null;
        //密码需要客户端加密后传递
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
//            insertLoginLog(username);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }

        return token;
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtTokenUtil.refreshHeadToken(oldToken);
    }

    @Override
    public int updatePassword(UpdatePasswordParam param) {
        if (StrUtil.isEmpty(param.getUsername()) || StrUtil.isEmpty(param.getOldPassword()) || StrUtil.isEmpty(param.getNewPassword())) {
            return -1;
        }
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(param.getUsername());
        List<User> userList = userMapper.selectByExample(example);
        if (CollUtil.isEmpty(userList)) return -2;
        User user = userList.get(0);
        if (!passwordEncoder.matches(param.getOldPassword(), user.getPassword())) return -3;
        user.setPassword(passwordEncoder.encode(param.getNewPassword()));
        userMapper.updateByPrimaryKey(user);
        return 1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户信息
        User user = getUserByUsername(username);
        if (user != null) {
            List<Role> roleList = getRoleList(user.getId());
            return new UserDetailsDto(user, roleList);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public int updateInfo(Integer id, User user) {
        user.setId(id);
        //密码需要单独修改
        user.setPassword(null);
        return userMapper.updateByPrimaryKeySelective(user);
    }


    private List<Role> getRoleList(Integer userId) {
        return userRoleDao.getRolesByUserId(userId);
    }
}
