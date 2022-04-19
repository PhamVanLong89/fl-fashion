package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "Users")
@Data
public class Customer implements Serializable {

  @Id
  @Column(name = "UserID")
  private int customerId;

  @Column(name = "UserName")
  private String customerName;

  @Column(name = "NumberPhone")
  private String numberPhone;

  @Column(name = "Address")
  private String address;

  @Column(name = "Email")
  private String email;

  @Column(name = "PassWord")
  private String password;

  @Column(name = "StatusActive")
  private int statusActive;

  @OneToMany(mappedBy = "customer")
  private List<Order> orders;
}
