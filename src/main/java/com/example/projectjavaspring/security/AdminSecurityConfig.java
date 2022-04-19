package com.example.projectjavaspring.security;

import com.example.projectjavaspring.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(2)
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

  private final AdminService adminService;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider2() {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(adminService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider2());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.requestMatchers ()
        .antMatchers("/manager/**")
        .and().authorizeRequests().antMatchers("/manager", "/manager/**").hasRole("ADMIN")
        // log in
        .and().formLogin().loginPage("/manager/login").permitAll().loginProcessingUrl("/manager/login")
        .usernameParameter("email")
        .passwordParameter("password")
        .failureUrl("/manager/login?error=loginError").defaultSuccessUrl("/manager")
        // logout
        .and().logout().logoutUrl("/manager/logout").logoutSuccessUrl("/")
        .deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and()
        .csrf().disable();
  }

}
