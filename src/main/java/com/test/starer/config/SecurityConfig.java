package com.test.starer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.starer.security.filter.RestAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
////        http.authorizeRequests(req -> req.mvcMatchers("/api/greeting").hasRole("ADMIN")); // 又有ADMIN角色才可以访问
//        http.formLogin(Customizer.withDefaults())
//        .authorizeRequests(req -> req.antMatchers("/api/greeting").permitAll());  // 不需要认证就可以访问的接口
//
//    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)  // 给SpringSecurity添加自定义的过滤器到SpringSecurity过滤器链中
                .formLogin(form->form.loginPage("/login").permitAll().successHandler(jsonLoginSuccessHandler())
                        .failureHandler(jsonLoginFailureHandler()))
                .authorizeRequests(req->req
                        .antMatchers("/authorize/**").permitAll()
                        .antMatchers("/authorize/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
//                .csrf(csrf->csrf.disable());
                .csrf(csrf->csrf.ignoringAntMatchers("/authorize/**", "admin/**", "/api/**"));
//                .logout(logout->logout.logoutUrl("/perform_logout"))
//                .rememberMe(remeberMe->remeberMe.tokenValiditySeconds(30 * 24 *2600).rememberMeCookieName("someKeyRemeber")); // 对api路径下的所有请求进行认证

    }


    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(jsonLoginSuccessHandler());
        filter.setAuthenticationFailureHandler(jsonLoginFailureHandler());
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }




    private AuthenticationSuccessHandler jsonLoginSuccessHandler() {
        return (req, res, auth) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(auth));
            log.debug("认证成功");
        };
    }

    private AuthenticationFailureHandler jsonLoginFailureHandler() {
        return (req, res, exp) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.setCharacterEncoding("UTF-8");
            final Map<String, Object> errorData = new HashMap<>();
            errorData.put("title", "认证失败");
            errorData.put("detail",exp.getMessage());
            res.getWriter().println(objectMapper.writeValueAsString(errorData));
        };
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        // auth.inMemoryAuthentication
        // 表示是基于内存的配置(其实内部是维护了一个map集合) 他会把你配置的用户名角色和密码保存到org.springframework.security.provisioning.InMemoryUserDetailsManager.InMemoryUserDetailsManager(java.util.Collection<org.springframework.security.core.userdetails.UserDetails>)里边的this.createUser(user);这行
        auth.inMemoryAuthentication()
                .withUser("user")
//                .password(passwordEncoder().encode("123456"))
                  .password("{bcrypt}$2a$10$1WXog0BQPmbwcqSa.h.1ReeLg6goe3hlm6gLrhpo4zLaqVGw1li96")
                .roles("USER", "ADMIN")
                .and()
                .withUser("zhangsan")
//                .password(new MessageDigestPasswordEncoder("SHA-1").encode("abcd1234"))
                .password("{SHA-1}{5PkQTG0WllC+V9KiiunpQ6czz6MQCRkmAj9YJ+WzSpM=}95b7fd8b2ea2ba120ff4e74bcaf1da4f3875bf9a")
                .roles("USER");  // 此处必须要设置角色不然会报错
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        String idForDefault = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForDefault, new BCryptPasswordEncoder());
        encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
//        return new BCryptPasswordEncoder();
        return new DelegatingPasswordEncoder(idForDefault, encoders);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/public/**", "/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 这行代码的配置是如果访问public目录下的资源是不会走SpringSecurity的过滤器链的
    }
}
