package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.entity.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

  @Override
  OrderDetail save(OrderDetail orderDetail);

  List<OrderDetail> findByOrder(Order order);

  @Query(value = "SELECT tinhThanhTien(?1)", nativeQuery = true)
  int amount(int orderDetailId);
}
