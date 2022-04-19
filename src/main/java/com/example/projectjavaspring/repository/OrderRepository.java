package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Customer;
import com.example.projectjavaspring.model.entity.Order;
import java.sql.Date;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

  @Override
  Order save(Order order);

  Page<Order> findAll(Pageable pageable);

  ArrayList<Order> findAll();

  Page<Order> findByCustomer(Pageable pageable, Customer customer);

  Order findByOrderId(int orderId);

  Order findByOrderIdAndCustomer(int orderId, Customer customer);

  @Query(value = "SELECT * from orders  WHERE UserId = :userId ORDER BY OrderId DESC LIMIT 1", nativeQuery = true)
  Order findOrderNewByCustomerId(@Param("userId") int userId);

  @Query(value = "SELECT tinhTongSanPham(?1)", nativeQuery = true)
  int totalProduct(int orderId);

  @Query(value = "SELECT tinhTongTien(?1)", nativeQuery = true)
  int totalPrice(int orderId);

  @Query(value = "SELECT o FROM Order o WHERE year(o.orderDate) = year(:orderDate)\n"
      + "and month(o.orderDate) = month(:orderDate)")
  ArrayList<Order> findByOrderDate(@Param("orderDate") Date orderDate);

}
