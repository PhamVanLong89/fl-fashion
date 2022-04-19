package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.form.AdminForm;
import com.example.projectjavaspring.service.AdminService;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AdminValidator implements Validator {

  private final AdminService adminService;
  private static final String PHONE_NUMBER = "numberPhone";
  private static final String ADMIN_NAME = "adminName";
  private static final String ADDRESS = "address";
  private static final String PASSWORD = "password";
  private static final String CONFIRM_PASSWORD = "confirmPassword";

  public boolean supports(Class<?> clazz) {
    return AdminForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var adminForm = (AdminForm) target;
    String errorName = DataValidator.checkName(adminForm.getAdminName());
    if (errorName != null) {
      errors.rejectValue(ADMIN_NAME, ADMIN_NAME, errorName);
    }
    String errorNumberPhone = DataValidator.checkNumberPhone(adminForm.getNumberPhone());
    if (errorNumberPhone != null) {
      errors.rejectValue(PHONE_NUMBER, PHONE_NUMBER, errorNumberPhone);
    }
    String errorSex = DataValidator.checkGender(adminForm.getSex());
    if (errorSex != null) {
      errors.rejectValue("sex", "sex", errorSex);
    }
    String errorAddress = DataValidator.checkAddress(adminForm.getAddress());
    if (errorAddress != null) {
      errors.rejectValue(ADDRESS, ADDRESS, errorAddress);
    }
    String errorEmail = checkEmailAdmin(adminForm.getEmail());
    if (errorEmail != null) {
      errors.rejectValue("email", "email", errorEmail);
    }
    String errorPassword = DataValidator.checkPassword(adminForm.getPassword());
    if (errorPassword != null) {
      errors.rejectValue(PASSWORD, PASSWORD, errorPassword);
    }
    String errorConfirmPassword = DataValidator
        .checkConfirmPassword(adminForm.getPassword(), adminForm.getConfirmPassword());
    if (errorConfirmPassword != null) {
      errors.rejectValue(CONFIRM_PASSWORD, CONFIRM_PASSWORD, errorConfirmPassword);
    }
  }

  public void validateAdminUpdate(Object target, Errors errors) {
    var adminStr = (AdminForm) target;
    String errorName = DataValidator.checkName(adminStr.getAdminName());
    if (errorName != null) {
      errors.rejectValue(ADMIN_NAME, ADMIN_NAME, errorName);
    }
    String errorNumberPhone = DataValidator.checkNumberPhone(adminStr.getNumberPhone());
    if (errorNumberPhone != null) {
      errors.rejectValue(PHONE_NUMBER, PHONE_NUMBER, errorNumberPhone);
    }
    String errorSex = DataValidator.checkGender(adminStr.getSex());
    if (errorSex != null) {
      errors.rejectValue("sex", "sex", errorSex);
    }
    String errorAddress = DataValidator.checkAddress(adminStr.getAddress());
    if (errorAddress != null) {
      errors.rejectValue(ADDRESS, ADDRESS, errorAddress);
    }
  }

  public void validateChangePassword(Object target, String currentPassword, Errors errors) {
    var adminForm = (AdminForm) target;
    var passwordEncoder = new BCryptPasswordEncoder();
    if (!passwordEncoder.matches(adminForm.getPassword(), currentPassword)) {
      errors.rejectValue(PASSWORD, PASSWORD, "Mật khẩu hiện tại không chính xác");
    }
    String errorPassword = DataValidator.checkPassword(adminForm.getNewPassword());
    if (errorPassword != null) {
      errors.rejectValue("newPassword", "newPassword", errorPassword);
    }
    String errorConfirmPassword = DataValidator
        .checkConfirmPassword(adminForm.getNewPassword(), adminForm.getConfirmPassword());
    if (errorConfirmPassword != null) {
      errors.rejectValue(CONFIRM_PASSWORD, CONFIRM_PASSWORD, errorConfirmPassword);
    }
  }

  public String checkEmailAdmin(String email) {
    var emailPattern = "^([a-z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$";
    if (email.trim().length() == 0) {
      return "Vui lòng nhập email";
    } else if (!Pattern.matches(emailPattern, email)) {
      return "Email không hợp lệ";
    } else if (adminService.findByEmail(email) != null) {
      return "Email đã được sử dụng";
    } else {
      return null;
    }
  }
}
