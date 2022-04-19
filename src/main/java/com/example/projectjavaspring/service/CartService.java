package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Cart;
import com.example.projectjavaspring.model.entity.CartItem;
import com.example.projectjavaspring.repository.CartItemRepository;
import com.example.projectjavaspring.repository.CartRepository;
import com.example.projectjavaspring.security.CustomerDetails;
import java.sql.Date;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;

  public Cart findCartUnpaidByUserId(String userId) {
    return cartRepository.findCartUnpaidByUserId(userId);
  }

  public Cart save(Cart cart) {
    return cartRepository.save(cart);
  }

  public int deleteByCartId(int cartId) {
    return cartRepository.deleteByCartId(cartId);
  }

  public Cart addCart(String userId, CustomerDetails customerLogged, HttpServletResponse response) {
    Cart cart;
    if (customerLogged == null) {
      cart = cartRepository.findCartUnpaidByUserId(userId);
      if (cart == null) {
        UUID uuid = null;
        do {
          uuid = UUID.randomUUID();
        } while (cartRepository.findByUserId(String.valueOf(uuid)) != null);
        cart = new Cart();
        cart.setCreateDate(Date.valueOf(java.time.LocalDate.now()));
        cart.setUserId(String.valueOf(uuid));
        cart.setStatus(0);
        cart = cartRepository.save(cart);
        var cookie = new Cookie("userId", String.valueOf(uuid));
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
      }
    } else {
      cart = cartRepository.findCartUnpaidByUserId(String.valueOf(customerLogged.getCustomerId()));
      if (cart == null) {
        cart = new Cart();
        cart.setCreateDate(Date.valueOf(java.time.LocalDate.now()));
        cart.setUserId(String.valueOf(customerLogged.getCustomerId()));
        cart.setStatus(0);
        cartRepository.save(cart);
      }
    }
    return cart;
  }

  @Transactional
  public void synchronizeCart(String userId, CustomerDetails customerLogged) {
    if (customerLogged == null) {
      return;
    }
    var anonymousCart = cartRepository.findCartUnpaidByUserId(userId);
    if (anonymousCart == null) {
      return;
    }
    var customerCart = cartRepository
        .findCartUnpaidByUserId(String.valueOf(customerLogged.getCustomerId()));
    if (customerCart == null) {
      anonymousCart.setUserId(String.valueOf(customerLogged.getCustomerId()));
      cartRepository.save(anonymousCart);
      return;
    }
    //nếu tài khoản đã có giỏ hàng thì đồng bộ giỏ hàng và xóa giỏ hàng ẩn danh
    for (CartItem anonymousItem : anonymousCart.getCartItems()) {
      CartItem customerItem = customerCart.getCartItems().stream()
          .filter(i -> i.getVariant().getSKU().equals(anonymousItem.getVariant().getSKU()))
          .findAny().orElse(null);
      //Nếu đã có sản phẩm trong giỏ hàng thì tăng số lượng
      if (customerItem != null) {
        customerItem.setQuantity(customerItem.getQuantity() + anonymousItem.getQuantity());
        cartItemRepository.save(customerItem);
        continue;
      }
      anonymousItem.setCart(customerCart);
      cartItemRepository.save(anonymousItem);
    }
    cartRepository.deleteByCartId(anonymousCart.getCartId());
  }

  public int totalProduct(Cart cart) {
    if (cart == null || cart.getCartItems() == null) {
      return 0;
    }
    var totalProduct = 0;
    for (CartItem item : cart.getCartItems()) {
      totalProduct += item.getQuantity();
    }
    return totalProduct;
  }

  public int totalPrice(Cart cart) {
    if (cart == null || cart.getCartItems() == null) {
      return 0;
    }
    var totalPrice = 0;
    for (CartItem item : cart.getCartItems()) {
      totalPrice += (item.getQuantity() * item.getVariant().getProduct().getProductPrice());
    }
    return totalPrice;
  }

  public int totalDiscount(Cart cart) {
    if (cart == null || cart.getCartItems() == null) {
      return 0;
    }
    var totalDiscount = 0;
    for (CartItem item : cart.getCartItems()) {
      totalDiscount += (item.getQuantity() * Precision.round(
          (double) item.getVariant().getProduct().getProductPrice() * (double) item.getVariant()
              .getProduct().getProductSale() / 100, -3));
    }
    return totalDiscount;
  }

}
