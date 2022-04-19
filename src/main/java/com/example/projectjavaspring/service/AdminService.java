package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Admin;
import com.example.projectjavaspring.repository.AdminRepository;
import com.example.projectjavaspring.security.AdminDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

  private final AdminRepository adminRepository;

  public Admin save(Admin admin) {
    return adminRepository.save(admin);
  }

  // findByEmail
  public Admin findByEmail(String email) {
    return adminRepository.findByEmail(email);
  }

  public List<Admin> findAll() {
    return adminRepository.findAll();
  }

  // findById
  public Admin findById(int adminId) {
    return adminRepository.findByAdminId(adminId);
  }

  // findByName
  public List<Admin> findByName(String adminName) {
    return adminRepository.findByName(adminName);
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var admin = adminRepository.findByEmail(email);
    if (admin == null) {
      throw new UsernameNotFoundException("Email không tồn tại!");
    }
    return new AdminDetails(admin);
  }
}
