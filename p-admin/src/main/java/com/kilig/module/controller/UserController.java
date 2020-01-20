package com.kilig.module.controller;

import com.kilig.module.common.api.CommonResult;
import com.kilig.module.dao.UserRoleDao;
import com.kilig.module.dto.UpdatePasswordParam;
import com.kilig.module.dto.UserRegisterParam;
import com.kilig.module.entity.Role;
import com.kilig.module.entity.User;
import com.kilig.module.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author L.Willian
 * @date 2020/1/20
 */
@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleDao userRoleDao;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public CommonResult register(@RequestBody UserRegisterParam userRegisterParam, BindingResult result) {
        //执行注册方法
        User user = userService.register(userRegisterParam);
        //判断是否注册成功 null = 失败
        if (user == null) return CommonResult.failed();
        return CommonResult.success(user);
    }

    @ApiOperation(value = "刷新token")
    @GetMapping("/refreshToken")
    public CommonResult refreshToken(HttpServletRequest request) {
        //获取当前token
        String token = request.getHeader(tokenHeader);
        //刷新token
        String newToken = userService.refreshToken(token);
        if (newToken == null) return CommonResult.failed("token已经过期");
        Map<String, Object> map = new HashMap<>();
        map.put("token", newToken);
        map.put("tokenHead", tokenHead);
        return CommonResult.success(map);
    }

    @ApiOperation(value = "用户登录")
    @GetMapping("/login")
    public CommonResult login(@RequestParam("username") String username,
                              @RequestParam("password") String password) {
        //todo
        String token = userService.login(username, password);
        if (token == null) return CommonResult.validateFailed("用户名或密码错误");
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("tokenHead", tokenHead);
        return CommonResult.success(map);
    }

    @ApiOperation(value = "登出")
    @PostMapping("/logout")
    public CommonResult logout() {
        return CommonResult.success(null);
    }

    @ApiOperation(value = "获取当前用户登录信息")
    @GetMapping("/info")
    public CommonResult getUserInfo(Principal principal) {
        Map<String, Object> data = new HashMap<>();
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        //获得用户权限用于前端判断不同用户的页面跳转
        List<Role> roleList = userRoleDao.getRolesByUserId(user.getId());
        for (Role r : roleList) {
            if (r.getName().equals("ROLE_ADMIN")) data.put("role", "admin");
            else data.put("role", "normal");
        }
        //结果数据
        data.put("username", user.getUsername());
        data.put("nickName", user.getNickName());
        return CommonResult.success(data);
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePwd(@RequestBody UpdatePasswordParam updatePasswordParam) {
        int status = userService.updatePassword(updatePasswordParam);
        if (status > 0) return CommonResult.success(status);
        else if (status == -1) return CommonResult.failed("提交参数不合法");
        else if (status == -2) return CommonResult.failed("找不到该用户");
        else if (status == -3) return CommonResult.failed("旧密码错误");
        else return CommonResult.failed();
    }

    @ApiOperation(value = "修改当前用户信息")
    @PostMapping("/updateInfo/{id}")
    public CommonResult updateInfo(@PathVariable Integer id, @RequestBody User user) {
        int count = userService.updateInfo(id, user);
        if (count > 0) return CommonResult.success(count);
        return CommonResult.failed();
    }
}
