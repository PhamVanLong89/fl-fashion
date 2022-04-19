package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "CartItems")
@Data
public class CartItem implements Serializable {

  @Id
  @Column(name = "CartItemId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "Quantity")
  private int quantity;

  @ManyToOne
  @EqualsAndHashCode.Exclude @ToString.Exclude
  @JoinColumn(name = "CartId")
  private Cart cart;

  @ManyToOne
  @EqualsAndHashCode.Exclude @ToString.Exclude
  @JoinColumn(name = "SKU")
  private Variant variant;


}
