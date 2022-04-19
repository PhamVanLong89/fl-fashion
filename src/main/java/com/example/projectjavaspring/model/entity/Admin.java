package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Admins")
@Data
public class Admin implements Serializable {

  @Id
  @Column(name = "AdminId")
  private int adminId;

  @Column(name = "AdminName")
  private String adminName;

  @Column(name = "NumberPhone")
  private String numberPhone;

  @Column(name = "Sex")
  private String sex;

  @Column(name = "Address")
  private String address;

  @Column(name = "Email")
  private String email;

  @Column(name = "PassWord")
  private String password;

  @Column(name = "Image")
  private String image;
}
