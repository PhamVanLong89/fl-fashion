package com.example.projectjavaspring.model.form;

import java.util.List;
import lombok.Data;

@Data
public class CartForm {

  private String cartId;
  private String createDate;
  private String userId;
  private String status;
  private List<CartItemForm> cartItemsForms;
}
