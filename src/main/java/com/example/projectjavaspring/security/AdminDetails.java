package com.example.projectjavaspring.security;

import com.example.projectjavaspring.model.entity.Admin;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
public class AdminDetails implements UserDetails {

  private Admin admin;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  @Override
  public String getPassword() {
    return admin.getPassword();
  }

  @Override
  public String getUsername() {
    return admin.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public int getAdminId() {
    return admin.getAdminId();
  }

  public String getAdminName() {
    return admin.getAdminName();
  }

  public void setAdminName(String adminName) {
    this.admin.setAdminName(adminName);
  }

  public void setPhoneNumber(String phoneNumber) {
    this.admin.setNumberPhone(phoneNumber);
  }

  public void setAddress(String address) {
    this.admin.setAddress(address);
  }

  public void setGender(String gender) {
    this.admin.setSex(gender);
  }

  public void setImage(String image) {
    this.admin.setImage(image);
  }

  public String getImage() {
    return admin.getImage();
  }
}
