package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.entity.OrderDetail;
import com.example.projectjavaspring.model.form.OrderDetailForm;
import com.example.projectjavaspring.repository.OrderDetailRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

  private final OrderDetailRepository orderDetailRepository;
  private final VariantService variantService;

  public OrderDetail save(OrderDetail orderDetail) {
    return orderDetailRepository.save(orderDetail);
  }

  public List<OrderDetail> findByOrder(Order order) {
    return orderDetailRepository.findByOrder(order);
  }

  public OrderDetailForm convertToOrderDetailForm(OrderDetail orderDetail) {
    var orderDetailForm = new OrderDetailForm();
    orderDetailForm.setOrderDetailId(String.valueOf(orderDetail.getOrderDetailId()));
    orderDetailForm.setVariantForm(variantService.convertToVariantForm(orderDetail.getVariant()));
    orderDetailForm.setSale(String.valueOf(orderDetail.getSale()));
    orderDetailForm.setPrice(String.valueOf(orderDetail.getPrice()));
    orderDetailForm.setQuantity(String.valueOf(orderDetail.getQuantity()));
    orderDetailForm.setAmount(
        String.valueOf(orderDetailRepository.amount(orderDetail.getOrderDetailId())));
    return orderDetailForm;
  }
}
