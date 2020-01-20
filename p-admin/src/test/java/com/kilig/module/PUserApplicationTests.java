package com.kilig.module;

import com.kilig.module.dao.UserRoleDao;
import com.kilig.module.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PUserApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private UserRoleDao userRoleDao;

	@Test
	void getRolesByUserId(){
		List<Role> roles = userRoleDao.getRolesByUserId(1);
		for (Role r :roles) {
			System.out.println(r.toString());
		}
	}
}
