package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.security.CustomerDetails;
import com.example.projectjavaspring.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @ResponseBody
  @GetMapping(value = {"/cart/products/total"})
  public int getTotalProductsInCart(@AuthenticationPrincipal CustomerDetails customerLogged,
      @CookieValue(value = "userId", defaultValue = "") String userId) {
    if (customerLogged != null) {
      userId = String.valueOf(customerLogged.getCustomerId());
    }
    var cart = cartService.findCartUnpaidByUserId(userId);
    return cartService.totalProduct(cart);
  }

  @GetMapping(value = {"/cart"})
  public String selectCart(@AuthenticationPrincipal CustomerDetails customerLogged,
      @CookieValue(value = "userId", defaultValue = "") String userId, Model model) {
    if (customerLogged != null) {
      userId = String.valueOf(customerLogged.getCustomerId());
    }
    var cart = cartService.findCartUnpaidByUserId(userId);
    model.addAttribute("cart", cart);
    model.addAttribute("totalProduct", cartService.totalProduct(cart));
    model.addAttribute("totalPrice", cartService.totalPrice(cart));
    model.addAttribute("totalDiscount", cartService.totalDiscount(cart));
    return "Customer/CartView";
  }

}