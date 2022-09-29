package com.javaboy.securitydatajpa.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户实体类
 *      实现UserDetails接口并实现接口中方法
 * @author chenzhisheng
 * @date 2022/09/29 13:52
 **/
@Data
@Entity(name = "t_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    /** 账号是否过期 **/
    private boolean accountNonExpired;
    /** 账号是否被锁定 **/
    private boolean accountNonLocked;
    /** 密码是否过期 **/
    private boolean credentialsNonExpired;
    /** 账号是否可用 **/
    private boolean enabled;

    /** User和Role是多对多关系，用@ManyToMany注解描述 **/
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Role> roles;

    /**
     * 返回用户的角色信息
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : getRoles()){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }
}
