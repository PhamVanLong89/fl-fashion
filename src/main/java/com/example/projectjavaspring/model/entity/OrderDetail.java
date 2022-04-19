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
@Table(name = "OrderDetails")
@Data
public class OrderDetail implements Serializable {

  @Id
  @Column(name = "OrderDetailId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int orderDetailId;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JoinColumn(name = "OrderId")
  private Order order;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JoinColumn(name = "SKU")
  private Variant variant;

  @Column(name = "Sale")
  private int sale;

  @Column(name = "Price")
  private int price;

  @Column(name = "Quantity")
  private int quantity;
}
