package org.javaboy.session2.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 用户实体类
 * 实现UserDetails接口并实现接口中方法
 *
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
    /**
     * 账号是否过期
     **/
    private boolean accountNonExpired;
    /**
     * 账号是否被锁定
     **/
    private boolean accountNonLocked;
    /**
     * 密码是否过期
     **/
    private boolean credentialsNonExpired;
    /**
     * 账号是否可用
     **/
    private boolean enabled;

    /**
     * User和Role是多对多关系，用@ManyToMany注解描述
     **/
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private List<Role> roles;

    /**
     * 返回用户的角色信息
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    /**
     * Spring Security 通过 SessionRegistryImpl类 实现对会话信息的统一管理 {@link org.springframework.security.core.session.SessionRegistryImpl}
     * 一开始声明了 {@link org.springframework.security.core.session.SessionRegistryImpl#principals}
     * 这个Concurrent集合的 key 是 用户主体principal，value 是 set集合(保存这个用户的sessionId)
     * 如果 有新的session需要添加 或 用户注销登录移除sessionId，在以下两个方法中完成
     * {@link org.springframework.security.core.session.SessionRegistryImpl#registerNewSession(String, Object)}
     * {@link org.springframework.security.core.session.SessionRegistryImpl#removeSessionInformation(String)}
     * 其中是使用 principals.compute 和 principals.computeIfPresent 进行操作
     * key 就是 principal，这里会有问题 ---> 用对象做key，没有重写 equals/hashCode方法 就会在第一次存完数据之后，下次存数据就找不到了
     * <p>
     * 因此自定义的用户类要 重写equals/hashCode方法，可参照 基于内存的用户 已经重写好的方法
     * {@link org.springframework.security.core.userdetails.User#equals(Object)} {@link org.springframework.security.core.userdetails.User#hashCode()}
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
