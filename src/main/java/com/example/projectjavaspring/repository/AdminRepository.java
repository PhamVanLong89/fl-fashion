package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Admin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

  @Override
  Admin save(Admin admin);

  Admin findByEmail(String email);

  @Override
  List<Admin> findAll();

  Admin findByAdminId(int adminId);

  List<Admin> findByName(String adminName);

}
