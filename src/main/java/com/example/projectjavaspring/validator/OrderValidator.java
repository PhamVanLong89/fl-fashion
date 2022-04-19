package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.entity.CartItem;
import com.example.projectjavaspring.model.form.OrderForm;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class OrderValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OrderForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var orderForm = (OrderForm) target;
    String errorNumberPhone = DataValidator.checkNumberPhone(orderForm.getOrderNumberPhone());
    if (errorNumberPhone != null) {
      errors.rejectValue("orderNumberPhone", "orderNumberPhone", errorNumberPhone);
    }
    String errorAddress = DataValidator.checkAddress(orderForm.getOrderAddress());
    if (errorAddress != null) {
      errors.rejectValue("orderAddress", "orderAddress", errorAddress);
    }
    String errorPaymentMethod = checkPaymentMethod(orderForm.getPaymentsMethod());
    if (errorPaymentMethod != null) {
      errors.rejectValue("paymentsMethod", "paymentsMethod", errorPaymentMethod);
    }
  }

  public void validateQuantity(List<CartItem> items, Errors errors) {
    for (CartItem item : items) {
      String errorQuantity = DataValidator.checkRemainingProductQuantity(
          String.valueOf(item.getQuantity()), item.getVariant());
      if (errorQuantity != null) {
        errors.rejectValue("orderDetailForms", String.valueOf(item.getId()), errorQuantity);
      }
    }
  }

  public void validateOrderStatus(Object target, Errors errors) {
    var orderForm = (OrderForm) target;
    if (!orderForm.getOrderStatus().equals("Chờ xác nhận") && !orderForm.getOrderStatus()
        .equals("Đang giao") && !orderForm.getOrderStatus().equals("Đã giao")) {
      errors.rejectValue("orderStatus", "orderStatus", "Trạng thái đơn hàng không hợp lệ");
    }
  }

  public String checkPaymentMethod(String paymentMethod) {
    if (paymentMethod == null || (!paymentMethod.equals("COD") && !paymentMethod.equals("MoMo")
        && !paymentMethod.equals("ZaloPay"))) {
      return "Vui lòng chọn phương thức thanh toán";
    }
    return null;
  }
}
