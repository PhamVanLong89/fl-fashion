package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Cart;
import com.example.projectjavaspring.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

  @Override
  CartItem save(CartItem item);

  CartItem findByCartAndId(Cart cart, int id);

  @Modifying
  @Transactional
  Integer deleteByIdAndCart(int id, Cart cart);

}
