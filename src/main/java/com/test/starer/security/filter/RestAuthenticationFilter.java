package com.test.starer.security.filter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@RequiredArgsConstructor
public class RestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final ObjectMapper objectMapper;

    /**
     *  {
     *      "username":"zhangsan",
     *      "password":"123456"
     *  }
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)  {
        UsernamePasswordAuthenticationToken authRequest;
        try {
            InputStream is = request.getInputStream();
            JsonNode jsonNode = objectMapper.readTree(is);
            String username = jsonNode.get("username").textValue();
            String password = jsonNode.get("password").textValue();
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
        } catch (Exception e) {
            throw new BadCredentialsException("没有找到用户名和密码");
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
