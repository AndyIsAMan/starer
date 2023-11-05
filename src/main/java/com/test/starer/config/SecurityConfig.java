package com.test.starer.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.authorizeRequests(req -> req.mvcMatchers("/api/greeting").hasRole("ADMIN")); // 又有ADMIN角色才可以访问
//        http.formLogin(Customizer.withDefaults())
//        .authorizeRequests(req -> req.antMatchers("/api/greeting").permitAll());  // 不需要认证就可以访问的接口
//
//    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .formLogin(form->form.loginPage("/login").permitAll())
                .authorizeRequests(req->req.anyRequest().authenticated()); // 对api路径下的所有请求进行认证

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 这行代码的配置是如果访问public目录下的资源是不会走SpringSecurity的过滤器链的
    }
}
