package com.kilig.module.dao;

import com.kilig.module.entity.Role;

import java.util.List;

/**
 * @author L.Willian
 * @date 2020/1/20
 */
public interface UserRoleDao {

    List<Role> getRolesByUserId(Integer userId);

}
