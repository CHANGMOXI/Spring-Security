package org.javaboy.session2.dao;

import org.javaboy.session2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chenzhisheng
 * @date 2022/09/29 14:20
 **/
public interface UserDao extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
