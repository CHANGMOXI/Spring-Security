package org.javaboy.session2.service;

import org.javaboy.session2.dao.UserDao;
import org.javaboy.session2.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义UserService，需要实现 UserDetailsService 接口的loadUserByUsername方法
 *      方法的参数 username 就是登录时传入的用户名，根据用户名查询用户信息(查出来后，系统自动进行密码比对)
 * @author chenzhisheng
 * @date 2022/09/29 14:23
 **/
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }
}
