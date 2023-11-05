package com.test.starer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
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
                .formLogin(form->form.loginPage("/login").permitAll().successHandler((req,res, auth)->{
                    // 下边登录成功以后的逻辑
                    res.setStatus(HttpStatus.OK.value());
                    res.getWriter().println();
                    log.debug("认证成功");
                }))  // successHandler 代表登录成功以后的执行的逻辑
                .authorizeRequests(req->req.anyRequest().authenticated())
                .logout(logout->logout.logoutUrl("/perform_logout"))
                .rememberMe(remeberMe->remeberMe.tokenValiditySeconds(30 * 24 *2600).rememberMeCookieName("someKeyRemeber")); // 对api路径下的所有请求进行认证

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("123456"))
                .roles("USER", "ADMIN");  // 此处必须要设置角色不然会报错
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();}

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**", "/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 这行代码的配置是如果访问public目录下的资源是不会走SpringSecurity的过滤器链的
    }
}
