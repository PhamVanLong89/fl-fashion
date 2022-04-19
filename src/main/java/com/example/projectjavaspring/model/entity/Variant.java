package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "Variants")
@Data
public class Variant implements Serializable {

  @ManyToOne
  @JoinColumn(name = "ProductId")
  private Product product;

  @Id
  @Column(name = "SKU")
  private String sKU;

  @Column(name = "Color")
  private String color;

  @Column(name = "Size")
  private String size;

  @Column(name = "Quantity")
  private int quantity;

  @Column(name = "Image1")
  private String image1;

  @Column(name = "Image2")
  private String image2;

  @OneToMany(mappedBy = "variant")
  private List<CartItem> cartItems;

  @OneToMany(mappedBy = "variant")
  private List<OrderDetail> orderDetails;

}
