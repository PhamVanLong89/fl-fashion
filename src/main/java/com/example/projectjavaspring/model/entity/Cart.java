package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Carts")
@Data
public class Cart implements Serializable {

  @Id
  @Column(name = "CartId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int cartId;

  @Column(name = "CreateDate")
  private Date createDate;

  @Column(name = "UserId")
  private String userId;

  @Column(name = "CartStatus")
  private int status;

  @OneToMany(mappedBy = "cart")
  private List<CartItem> cartItems;
}
