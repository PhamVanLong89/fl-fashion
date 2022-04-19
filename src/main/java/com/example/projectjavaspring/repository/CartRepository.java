package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

  @Query("Select c from Cart c Where c.userId = ?1 And c.status = 0")
  Cart findCartUnpaidByUserId(String userId);

  Cart findByUserId(String userId);

  @Override
  Cart save(Cart cart);

  @Modifying
  @Transactional
  int deleteByCartId(int cartId);
}
