package org.javaboy.session2;

import org.javaboy.session2.dao.UserDao;
import org.javaboy.session2.entity.Role;
import org.javaboy.session2.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class Session2ApplicationTests {
    @Resource
    private UserDao userDao;

    @Test
    void contextLoads() {
        User u1 = new User();
        u1.setUsername("changmoxi");
        u1.setPassword("111111");
        u1.setAccountNonExpired(true);
        u1.setAccountNonLocked(true);
        u1.setCredentialsNonExpired(true);
        u1.setEnabled(true);
        List<Role> rs1 = new ArrayList<>();
        Role r1 = new Role();
        r1.setName("ROLE_admin");
        r1.setNameZh("管理员");
        rs1.add(r1);
        u1.setRoles(rs1);
        userDao.save(u1);

        User u2 = new User();
        u2.setUsername("长莫西666");
        u2.setPassword("666666");
        u2.setAccountNonExpired(true);
        u2.setAccountNonLocked(true);
        u2.setCredentialsNonExpired(true);
        u2.setEnabled(true);
        List<Role> rs2 = new ArrayList<>();
        Role r2 = new Role();
        r2.setName("ROLE_user");
        r2.setNameZh("普通用户");
        rs2.add(r2);
        u2.setRoles(rs2);
        userDao.save(u2);
    }

}
