package com.example.projectjavaspring.model.form;

import java.sql.Date;
import java.util.List;
import lombok.Data;

@Data
public class OrderForm {

  private String orderId;
  private Date orderDate;
  private CustomerForm customerForm;
  private String paymentsMethod;
  private String orderStatus;
  private String orderAddress;
  private String orderNumberPhone;
  private int totalPrice;
  private int totalProduct;
  private List<OrderDetailForm> orderDetailForms;
}
