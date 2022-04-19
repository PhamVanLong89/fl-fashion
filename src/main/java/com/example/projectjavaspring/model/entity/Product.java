package com.example.projectjavaspring.model.entity;

import java.io.Serializable;
import java.sql.Date;
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
@Table(name = "Products")
@Data
public class Product implements Serializable {

  @Id
  @Column(name = "ProductId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int productId;

  @Column(name = "ProductName")
  private String productName;

  @Column(name = "ProductPrice")
  private int productPrice;

  @Column(name = "Sale")
  private int productSale;

  @Column(name = "CategoryName")
  private String categoryName;

  @Column(name = "SaleDate")
  private Date saleDate;

  @Column(name = "Description")
  private String description;

  @Column(name = "AdminId")
  private int adminId;

  @Column(name = "DisplayHome")
  private int displayHome;

  @OneToMany(mappedBy = "product")
  private List<Variant> variants;
}
