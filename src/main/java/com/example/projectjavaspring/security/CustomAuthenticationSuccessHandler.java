package com.example.projectjavaspring.security;

import com.example.projectjavaspring.service.CartService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final CartService cartService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    CustomerDetails userDetails = (CustomerDetails) authentication.getPrincipal();
    Cookie[] cookies = request.getCookies();
    var userId = "";
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals("userId")) {
          userId = cookie.getValue();
          cookie.setMaxAge(0);
          cookie.setPath("/");
          response.addCookie(cookie);
        }
      }
    }
    cartService.synchronizeCart(userId, userDetails);
    var helper = new UrlPathHelper();
    String contextPath = helper.getContextPath(request);
    response.sendRedirect(contextPath);
  }
}
