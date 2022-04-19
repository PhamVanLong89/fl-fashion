package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Revenue;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {

  @Query(value = "SELECT o.OrderDate as date, SUM(tinhTongTien(o.OrderId)) as revenue FROM orders o WHERE month(o.orderDate) = month(:orderDate) and year(o.orderDate) = year(:orderDate) GROUP BY o.orderDate ORDER BY o.orderDate", nativeQuery = true)
  List<Revenue> getRevenueByDate(@Param("orderDate") Date orderDate);

}
