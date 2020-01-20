package com.kilig.module.dao;

import com.kilig.module.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author L.Willian
 * @date 2020/1/20
 */
@Repository
public interface UserRoleDao {

    List<Role> getRolesByUserId(Integer userId);

}
