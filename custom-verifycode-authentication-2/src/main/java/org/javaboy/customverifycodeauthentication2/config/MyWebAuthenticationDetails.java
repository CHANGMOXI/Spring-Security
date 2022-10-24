package org.javaboy.customverifycodeauthentication2.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证码校验 放在 自定义 WebAuthenticationDetails
 *
 * @author chenzhisheng
 * @date 2022/10/24 12:38
 **/
public class MyWebAuthenticationDetails extends WebAuthenticationDetails {
    //如果想扩展属性，只需要在 MyWebAuthenticationDetails 这里再去定义更多属性，然后从 HttpServletRequest 提取并设置给对应的属性即可

    private boolean isPassed;

    public MyWebAuthenticationDetails(HttpServletRequest request) {
        //WebAuthenticationDetails 原有功能保留，仍然可以获取用户IP和sessionId等信息
        super(request);
        //构造时加入验证码校验
        String code = request.getParameter("code");
        String verify_code = (String) request.getSession().getAttribute("verify_code");
        if (code != null && verify_code != null && code.equals(verify_code)) {
            isPassed = true;
        }
    }

    public boolean isPassed() {
        return this.isPassed;
    }
}
