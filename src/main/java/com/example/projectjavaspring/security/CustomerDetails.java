package com.example.projectjavaspring.security;

import com.example.projectjavaspring.model.entity.Customer;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@AllArgsConstructor
public class CustomerDetails implements UserDetails, Serializable {

  private Customer customer;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return customer.getPassword();
  }

  @Override
  public String getUsername() {
    return customer.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.customer.getStatusActive() == 1;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public int getCustomerId() {
    return this.customer.getCustomerId();
  }

  public String getCustomerName() {
    return this.customer.getCustomerName();
  }

  public String getAddress() {
    return this.customer.getAddress();
  }

  public String getPhoneNumber() {
    return this.customer.getNumberPhone();
  }

  public void setCustomerName(String customerName) {
    this.customer.setCustomerName(customerName);
  }

  public void setPhoneNumber(String phoneNumber) {
    this.customer.setNumberPhone(phoneNumber);
  }

  public void setAddress(String address) {
    this.customer.setAddress(address);
  }

}
