package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.entity.Revenue;
import com.example.projectjavaspring.model.form.OrderForm;
import com.example.projectjavaspring.repository.RevenueRepository;
import com.example.projectjavaspring.service.OrderService;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RevenueController {

  private final OrderService orderService;
  private final RevenueRepository revenueRepository;

  @GetMapping(value = {"/manager/order/doanhthu"})
  public String revenue(@RequestParam(name = "date", required = false) String dateParam,
      Model model) throws NumberFormatException {
    //handle date time
//    LocalDate now = LocalDate.now();
    LocalDate dateKey = LocalDate.now();
    if (dateParam != null && !dateParam.isEmpty()) {
      dateKey = LocalDate.parse(dateParam + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      if (dateKey.compareTo(LocalDate.now()) > 0) {
        dateKey = LocalDate.now();
      }
    }

//    List<Revenue> a = revenueRepository.getRevenueByDate(Date.valueOf(dateKey));

    List<Order> orders = orderService.findByOrderDate(Date.valueOf(dateKey));
    List<OrderForm> orderForms = new ArrayList<>();
    for (Order order : orders) {
      orderForms.add(orderService.convertOrderToOrderForm(order));
    }
    List<Revenue> revenueList = new ArrayList<>();
    LocalDate start = dateKey.withDayOfMonth(1);
    LocalDate end = dateKey.withDayOfMonth(dateKey.lengthOfMonth());
    if (end.compareTo(LocalDate.now()) > 0) {
      end = LocalDate.now();
    }
    for (LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
      int revenueInDay = 0;
      for (OrderForm order : orderForms) {
        if (order.getOrderDate().compareTo(Date.valueOf(date)) == 0) {
          revenueInDay = revenueInDay + order.getTotalPrice();
        }
      }
      Revenue revenue = new Revenue();
      revenue.setDate(date);
      revenue.setRevenue(Long.valueOf(revenueInDay));
      revenueList.add(revenue);
    }
    model.addAttribute("revenueList", revenueList);
    model.addAttribute("date", dateKey.format(DateTimeFormatter.ofPattern("yyyy-MM")));
    return "Admin/doanhThu";
  }
}
