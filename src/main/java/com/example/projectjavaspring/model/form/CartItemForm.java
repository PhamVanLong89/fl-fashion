package com.example.projectjavaspring.model.form;

import lombok.Data;

@Data
public class CartItemForm {

  private String id;
  private String quantity;
  private CartForm cartForm;
  private VariantForm variantForm;
}
