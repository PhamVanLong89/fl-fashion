package com.example.projectjavaspring.security;

import com.example.projectjavaspring.service.CustomerService;
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
@Order(1)
public class CustomerSecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomerService customerService;
  private final CustomAuthenticationSuccessHandler successHandler;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider1() {
    var authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customerService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider1());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.requestMatchers()
        .antMatchers("/*", "/user/**", "/product/**", "/products/**", "/cart/**")
        .and().authorizeRequests().antMatchers("/user/**").hasRole("USER")
        .anyRequest().permitAll()
        // log in
        .and().formLogin().loginPage("/login").loginProcessingUrl("/loginUser")
        .usernameParameter("email")
        .passwordParameter("password")
        .failureUrl("/login?error=loginError").defaultSuccessUrl("/")
        .successHandler(successHandler)
        // logout
        .and().logout().logoutUrl("/user/logout").logoutSuccessUrl("/")
        .deleteCookies("JSESSIONID").and().exceptionHandling().accessDeniedPage("/403").and()
        .csrf().disable();
  }

}
