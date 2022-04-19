package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Order;
import com.example.projectjavaspring.model.form.OrderForm;
import com.example.projectjavaspring.security.CustomerDetails;
import com.example.projectjavaspring.service.OrderService;
import com.example.projectjavaspring.service.mailservice.AbstractEmailContext;
import com.example.projectjavaspring.service.mailservice.EmailService;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

  private final OrderService orderService;

  @Autowired
  EmailService emailService;

  @PostMapping(value = "/user/order/email")
  public @ResponseBody
  ResponseEntity sendEmailWithTemplate(@AuthenticationPrincipal CustomerDetails customerLogged) {
    if (customerLogged == null) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    Order order = orderService.findOrderNewByCustomerId(customerLogged.getCustomerId());
    OrderForm orderForm = orderService.convertOrderToOrderForm(order);
    AbstractEmailContext mail = new AbstractEmailContext();
    mail.setFrom("longcuto892000@gmail.com");
    mail.setTo("phamvanlong892@gmail.com");
    mail.setSubject("Xác nhận đơn hàng");
    Map<String, Object> model2 = new HashMap<>();
    model2.put("order", orderForm);
    mail.setContext(model2);
    try {
      emailService.sendMailTemplate(mail, orderForm);
      return new ResponseEntity<>(HttpStatus.OK);

    } catch (MessagingException e) {
      return new ResponseEntity<>("Unable to send email", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
