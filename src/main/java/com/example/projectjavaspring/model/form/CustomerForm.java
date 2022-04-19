package com.example.projectjavaspring.model.form;

import lombok.Data;

@Data
public class CustomerForm {

  private String customerId;
  private String customerName;
  private String numberPhone;
  private String address;
  private String email;
  private String password;
  private String confirmPassword;
  private String newPassword;
  private String statusActive;
}
