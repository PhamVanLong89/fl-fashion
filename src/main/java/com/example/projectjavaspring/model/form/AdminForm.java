package com.example.projectjavaspring.model.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AdminForm {

  private String adminId;
  private String adminName;
  private String numberPhone;
  private String sex;
  private String address;
  private String email;
  private String password;
  private MultipartFile file;
  private String image;
  private String confirmPassword;
  private String newPassword;
}
