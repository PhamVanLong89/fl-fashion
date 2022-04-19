package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "Orders")
@Data
public class Order implements Serializable {

  @Id
  @Column(name = "OrderId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int orderId;

  @Column(name = "OrderDate")
  private Date orderDate;

  @ManyToOne
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JoinColumn(name = "UserId")
  private Customer customer;

  @Column(name = "PaymentsMethod")
  private String paymentsMethod;

  @Column(name = "OrderStatus")
  private String orderStatus;

  @Column(name = "OrderAddress")
  private String orderAddress;

  @Column(name = "OrderNumberPhone")
  private String orderNumberPhone;

  @OneToMany(mappedBy = "order")
  private List<OrderDetail> orderDetails;

}
