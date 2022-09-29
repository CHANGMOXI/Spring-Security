package com.javaboy.securitydatajpa.dao;

import com.javaboy.securitydatajpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chenzhisheng
 * @date 2022/09/29 14:20
 **/
public interface UserDao extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
