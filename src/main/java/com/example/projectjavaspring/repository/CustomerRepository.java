package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

  Customer findByEmail(String email);

  @Override
  Customer save(Customer customer);

  @Override
  List<Customer> findAll();

  Customer findByCustomerId(int id);

  @Modifying
  @Transactional
  @Query(value = "UPDATE Users SET StatusActive = :status WHERE UserID = :customerId", nativeQuery = true)
  void changeStatus(@Param("status") String status,
      @Param("customerId") int customerId);

  List<Customer> findByName(String customerName);

}
