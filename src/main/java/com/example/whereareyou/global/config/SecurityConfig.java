package com.example.whereareyou.global.config;

import com.example.whereareyou.global.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/member/**").permitAll()
                .antMatchers("/friend/**").permitAll()
                .antMatchers("/actuator/health").permitAll()
                .antMatchers("/schedule/**").permitAll()
                .antMatchers("/memberschedule/**").permitAll()
                .anyRequest().authenticated();

        http.csrf().disable();

        http.headers().frameOptions().disable();
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        List<String> permitAllEndpoints = Arrays.asList(
                "/member/**",
                "/actuator/health",
                "/friend/**",
                "/schedule/**",
                "/memberschedule/**"
        );
        return new JwtTokenFilter(jwtSecret, permitAllEndpoints);
    }
}
