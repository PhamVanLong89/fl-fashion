package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.CartItem;
import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.entity.OrderDetail;
import com.example.projectjavaspring.model.form.OrderForm;
import com.example.projectjavaspring.security.CustomerDetails;
import com.example.projectjavaspring.service.CartService;
import com.example.projectjavaspring.service.CustomerService;
import com.example.projectjavaspring.service.OrderDetailService;
import com.example.projectjavaspring.service.OrderService;
import com.example.projectjavaspring.validator.OrderValidator;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class OrderController {

  private final CartService cartService;
  private final OrderValidator orderValidator;
  private final CustomerService customerService;
  private final OrderService orderService;
  private final OrderDetailService orderDetailService;


  @ResponseBody
  @PostMapping(value = {"/order"})
  public ResponseEntity<List<FieldError>> order(
      @AuthenticationPrincipal CustomerDetails customerLogged,
      @RequestBody @Validated OrderForm orderForm, BindingResult result) {
    if (customerLogged == null) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    var cart = cartService.findCartUnpaidByUserId(String.valueOf(customerLogged.getCustomerId()));
    if (cart == null || cart.getCartItems() == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    orderValidator.validate(orderForm, result);
    orderValidator.validateQuantity(cart.getCartItems(), result);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors(), HttpStatus.BAD_REQUEST);
    }
    var order = new Order();
    var customer = customerService.findByEmail(customerLogged.getUsername());
    order.setCustomer(customer);
    order.setPaymentsMethod(orderForm.getPaymentsMethod());
    order.setOrderDate(Date.valueOf(LocalDate.now()));
    order.setOrderAddress(orderForm.getOrderAddress());
    order.setOrderNumberPhone(orderForm.getOrderNumberPhone());
    order.setOrderStatus("Chờ xác nhận");
    order = orderService.save(order);
    for (CartItem item : cart.getCartItems()) {
      var orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setVariant(item.getVariant());
      orderDetail.setPrice(item.getVariant().getProduct().getProductPrice());
      orderDetail.setSale(item.getVariant().getProduct().getProductSale());
      orderDetail.setQuantity(item.getQuantity());
      orderDetailService.save(orderDetail);
    }
    cartService.deleteByCartId(cart.getCartId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = {"/manager/orders"})
  public String showOrdersToAdmins(
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      Model model) throws NumberFormatException {
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Order> pages = orderService.findAll(PageRequest.of(currentPage - 1, 3));
    List<OrderForm> orderForms = new ArrayList<>();
    for (Order order : pages.getContent()) {
      orderForms.add(orderService.convertOrderToOrderForm(order));
    }
    model.addAttribute("orders", orderForms);
    model.addAttribute("totalPage", pages.getTotalPages());
    model.addAttribute("currentPage", currentPage);
    return "Admin/OrdersView";
  }

  @GetMapping(value = {"/user/orders"})
  public String showOrdersToCustomer(Model model,
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      @AuthenticationPrincipal CustomerDetails customerLogged) throws NumberFormatException {
    var customer = customerService.findByEmail(customerLogged.getUsername());
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Order> pages = orderService.findByCustomer(PageRequest.of(currentPage - 1, 3), customer);
    List<OrderForm> orderForms = new ArrayList<>();
    for (Order order : pages.getContent()) {
      orderForms.add(orderService.convertOrderToOrderForm(order));
    }
    model.addAttribute("orders", orderForms);
    model.addAttribute("totalPage", pages.getTotalPages());
    model.addAttribute("currentPage", currentPage);
    return "Customer/OrdersView";
  }

  @GetMapping(value = {"/manager/order/{orderId}"})
  public String showOrderDetailsToAdmin(@PathVariable(name = "orderId") int orderId, Model model)
      throws NumberFormatException {
    var order = orderService.findByOrderId(orderId);
    if (order == null) {
      return "redirect:/manager/orders";
    }
    var orderForm = orderService.convertOrderToOrderForm(order);
    model.addAttribute("order", orderForm);
    return "Admin/OrderDetailView";
  }

  @GetMapping(value = {"/user/order/{orderId}"})
  public String showOrderDetailsToCustomer(@PathVariable(name = "orderId") int orderId, Model model,
      @AuthenticationPrincipal CustomerDetails customerLogged) throws NumberFormatException {
    var customer = customerService.findByEmail(customerLogged.getUsername());
    var order = orderService.findByOrderIdAndCustomer(orderId, customer);
    if (order == null) {
      return "redirect:/user/orders";
    }
    var orderForm = orderService.convertOrderToOrderForm(order);
    model.addAttribute("order", orderForm);
    return "Customer/OrderDetailView";
  }

  @PutMapping(value = {"/user/order/{orderId}"})
  public ResponseEntity<String> cancelOrder(@PathVariable(name = "orderId") int orderId,
      @AuthenticationPrincipal CustomerDetails customerLogged) throws NumberFormatException {
    var customer = customerService.findByEmail(customerLogged.getUsername());
    var order = orderService.findByOrderIdAndCustomer(orderId, customer);
    if (order == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    order.setOrderStatus("Đã hủy");
    orderService.save(order);
    return new ResponseEntity<>("Hủy đơn đặt hàng thành công", HttpStatus.OK);
  }

  @PutMapping(value = {"/manager/order/{orderId}"})
  public ResponseEntity<List<FieldError>> changeOrderStatus(
      @RequestBody @Validated OrderForm orderForm, BindingResult result,
      @PathVariable(name = "orderId") int orderId) throws NumberFormatException {
    orderValidator.validateOrderStatus(orderForm, result);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors(), HttpStatus.BAD_REQUEST);
    }
    var order = orderService.findByOrderId(orderId);
    if (order == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    order.setOrderStatus(orderForm.getOrderStatus());
    orderService.save(order);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ExceptionHandler({NumberFormatException.class})
  public String ex(NumberFormatException e) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    Collection<SimpleGrantedAuthority> authority = (Collection<SimpleGrantedAuthority>) auth
        .getAuthorities();
    if (authority.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
      return "redirect:/manager/orders";
    } else {
      return "redirect:/";
    }
  }

}
