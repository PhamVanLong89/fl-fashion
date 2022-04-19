package com.example.projectjavaspring.model.form;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ProductForm implements Serializable {

  private String productId;
  private String productName;
  private String productPrice;
  private String productSale;
  private String categoryName;
  private String saleDate;
  private String description;
  private String adminId;
  private String displayHome;
  private String image;
  private List<VariantForm> variants;
}
