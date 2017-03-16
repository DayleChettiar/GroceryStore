package com.dayle.shoppingcart.web.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Order(Ordered.LOWEST_PRECEDENCE - 8)
public class CartItemSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/rest/cartItems*/**").permitAll()
            .antMatchers(HttpMethod.GET, "/rest/photos*/**").permitAll()
            .antMatchers("/rest/**").authenticated()
            .anyRequest().permitAll();
    }
}
