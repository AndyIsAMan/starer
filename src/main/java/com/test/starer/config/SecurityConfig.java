package com.test.starer.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests(req -> req.mvcMatchers("/api/greeting").hasRole("ADMIN")); // 又有ADMIN角色才可以访问
        http.formLogin(Customizer.withDefaults())
        .authorizeRequests(req -> req.antMatchers("/api/greeting").permitAll());  // 不需要认证就可以访问的接口

    }
}
