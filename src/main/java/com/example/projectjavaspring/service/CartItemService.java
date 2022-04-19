package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Cart;
import com.example.projectjavaspring.model.entity.CartItem;
import com.example.projectjavaspring.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {

  private final CartItemRepository cartItemRepository;

  public CartItem save(CartItem item) {
    return cartItemRepository.save(item);
  }

  public CartItem findByCartAndId(Cart cart, int id) {
    return cartItemRepository.findByCartAndId(cart, id);
  }

  public int deleteByIdAndCart(int id, Cart cart) {
    return cartItemRepository.deleteByIdAndCart(id, cart);
  }
}
