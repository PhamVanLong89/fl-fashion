package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Customer;
import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.entity.OrderDetail;
import com.example.projectjavaspring.model.form.OrderDetailForm;
import com.example.projectjavaspring.model.form.OrderForm;
import com.example.projectjavaspring.repository.OrderRepository;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final CustomerService customerService;
  private final OrderDetailService orderDetailService;

  public Order save(Order order) {
    return orderRepository.save(order);
  }

  public Page<Order> findAll(Pageable pageable) {
    return orderRepository.findAll(pageable);
  }

  public Order findByOrderId(int orderId) {
    return orderRepository.findByOrderId(orderId);
  }

  public Order findOrderNewByCustomerId(int customerId) {
    return orderRepository.findOrderNewByCustomerId(customerId);
  }

  public Order findByOrderIdAndCustomer(int orderId, Customer customer) {
    return orderRepository.findByOrderIdAndCustomer(orderId, customer);
  }

  public Page<Order> findByCustomer(Pageable pageable, Customer customer) {
    return orderRepository.findByCustomer(pageable, customer);
  }

  public List<Order> findByOrderDate(Date orderDate) {
    return orderRepository.findByOrderDate(orderDate);
  }

  public OrderForm convertOrderToOrderForm(Order order) {
    var orderForm = new OrderForm();
    orderForm.setOrderId(String.valueOf(order.getOrderId()));
    orderForm.setCustomerForm(customerService.convertToCustomerForm(order.getCustomer()));
    orderForm.setOrderDate(order.getOrderDate());
    orderForm.setPaymentsMethod(order.getPaymentsMethod());
    orderForm.setOrderAddress(order.getOrderAddress());
    orderForm.setOrderNumberPhone(order.getOrderNumberPhone());
    orderForm.setOrderStatus(order.getOrderStatus());
    orderForm.setTotalProduct(orderRepository.totalProduct(order.getOrderId()));
    orderForm.setTotalPrice(orderRepository.totalPrice(order.getOrderId()));
    List<OrderDetailForm> orderDetailForms = new ArrayList<>();
    for (OrderDetail detail : order.getOrderDetails()) {
      orderDetailForms.add(orderDetailService.convertToOrderDetailForm(detail));
    }
    orderForm.setOrderDetailForms(orderDetailForms);
    return orderForm;
  }

  public int totalPrice(int id) {
    return orderRepository.totalPrice(id);
  }

  public List<Order> findAll() {
    return orderRepository.findAll();
  }
}
