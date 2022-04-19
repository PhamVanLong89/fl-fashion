package com.example.projectjavaspring.model.form;

import lombok.Data;

@Data
public class OrderDetailForm {

  private String orderDetailId;
  private OrderForm orderForm;
  private VariantForm variantForm;
  private String sale;
  private String price;
  private String quantity;
  private String amount;
}
