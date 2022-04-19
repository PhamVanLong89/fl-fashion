package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.form.CustomerForm;
import com.example.projectjavaspring.service.CustomerService;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class CustomerValidator implements Validator {

  private final CustomerService customerService;
  private static final String PHONE_NUMBER = "numberPhone";
  private static final String CUSTOMER_NAME = "customerName";
  private static final String ADDRESS = "address";
  private static final String PASSWORD = "password";
  private static final String CONFIRM_PASSWORD = "confirmPassword";


  @Override
  public boolean supports(Class<?> clazz) {
    return CustomerForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var customerForm = (CustomerForm) target;
    String errorName = DataValidator.checkName(customerForm.getCustomerName());
    if (errorName != null) {
      errors.rejectValue(CUSTOMER_NAME, CUSTOMER_NAME, errorName);
    }
    String errorNumberPhone = DataValidator.checkNumberPhone(customerForm.getNumberPhone());
    if (errorNumberPhone != null) {
      errors.rejectValue(PHONE_NUMBER, PHONE_NUMBER, errorNumberPhone);
    }
    String errorAddress = DataValidator.checkAddress(customerForm.getAddress());
    if (errorAddress != null) {
      errors.rejectValue(ADDRESS, ADDRESS, errorAddress);
    }
    String errorEmail = checkEmail(customerForm.getEmail());
    if (errorEmail != null) {
      errors.rejectValue("email", "email", errorEmail);
    }
    String errorPassword = DataValidator.checkPassword(customerForm.getPassword());
    if (errorPassword != null) {
      errors.rejectValue(PASSWORD, PASSWORD, errorPassword);
    }
    String errorConfirmPassword = DataValidator.checkConfirmPassword(customerForm.getPassword(),
        customerForm.getConfirmPassword());
    if (errorConfirmPassword != null) {
      errors.rejectValue(CONFIRM_PASSWORD, CONFIRM_PASSWORD, errorConfirmPassword);
    }
  }

  public void validateUpdate(Object target, Errors errors) {
    var customerForm = (CustomerForm) target;
    String errorName = DataValidator.checkName(customerForm.getCustomerName());
    if (errorName != null) {
      errors.rejectValue(CUSTOMER_NAME, CUSTOMER_NAME, errorName);
    }
    String errorNumberPhone = DataValidator.checkNumberPhone(customerForm.getNumberPhone());
    if (errorNumberPhone != null) {
      errors.rejectValue(PHONE_NUMBER, PHONE_NUMBER, errorNumberPhone);
    }
    String errorAddress = DataValidator.checkAddress(customerForm.getAddress());
    if (errorAddress != null) {
      errors.rejectValue(ADDRESS, ADDRESS, errorAddress);
    }
  }

  public void validateChangePassword(Object target, String currentPassword, Errors errors) {
    var customerForm = (CustomerForm) target;
    var passwordEncoder = new BCryptPasswordEncoder();
    if (!passwordEncoder.matches(customerForm.getPassword(), currentPassword)) {
      errors.rejectValue(PASSWORD, PASSWORD, "Mật khẩu hiện tại không chính xác");
    }
    String errorPassword = DataValidator.checkPassword(customerForm.getNewPassword());
    if (errorPassword != null) {
      errors.rejectValue("newPassword", "newPassword", errorPassword);
    }
    String errorConfirmPassword = DataValidator
        .checkConfirmPassword(customerForm.getNewPassword(), customerForm.getConfirmPassword());
    if (errorConfirmPassword != null) {
      errors.rejectValue(CONFIRM_PASSWORD, CONFIRM_PASSWORD, errorConfirmPassword);
    }
  }

  public void validateStatus(Object target, Errors errors) {
    var customerForm = (CustomerForm) target;
    String error = DataValidator.checkStatus(customerForm.getStatusActive());
    if (error != null) {
      errors.rejectValue("statusActive", "statusActive", error);
    }
  }

  public String checkEmail(String email) {
    var emailPattern = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
    if (email.trim().length() == 0) {
      return "Vui lòng nhập email";
    } else if (!Pattern.matches(emailPattern, email)) {
      return "Email không hợp lệ";
    } else if (customerService.findByEmail(email) != null) {
      return "Email đã được sử dụng";
    } else {
      return null;
    }
  }

}
