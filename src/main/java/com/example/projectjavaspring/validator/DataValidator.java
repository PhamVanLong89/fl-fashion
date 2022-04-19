package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.entity.Variant;
import java.util.regex.Pattern;

public class DataValidator {

  private DataValidator() {
    throw new IllegalArgumentException("Utility class");
  }

  public static String checkName(String adminName) {
    if (adminName.trim().length() == 0) {
      return "Vui lòng nhập họ tên";
    } else if (adminName.length() > 50) {
      return "Họ tên dài quá giới hạn(50 ký tự)";
    } else {
      return null;
    }
  }

  public static String checkGender(String gender) {
    if (gender.trim().length() == 0) {
      return "Vui lòng chọn giới tính";
    } else if (!gender.equals("Nam") && !gender.equals("Nữ")) {
      return "Giới tính không hợp lệ";
    } else {
      return null;
    }
  }

  public static String checkAddress(String address) {
    if (address.trim().length() == 0) {
      return "Vui lòng nhập địa chỉ";
    } else if (address.length() > 100) {
      return "Địa chỉ dài quá giới hạn(100 ký tự)";
    } else {
      return null;
    }
  }

  public static String checkNumberPhone(String numberPhone) {
    var numberPhonePattern1 = "^(([0]{1}[0-9]{9})|([+]{1}[8]{1}[4]{1}[0-9]{9}))$";
    if (numberPhone.trim().length() == 0) {
      return "Vui lòng nhập số điện thoại";
    } else if (!Pattern.matches(numberPhonePattern1, numberPhone)) {
      return "Số điện thoại không hợp lệ";
    } else {
      return null;
    }
  }

  public static String checkPassword(String password) {
    var passwordPattern = "^[a-z0-9_-]{6,20}$";
    if (password.trim().length() == 0) {
      return "Vui lòng nhập mật khẩu";
    } else if (!Pattern.matches(passwordPattern, password)) {
      return "Mật khẩu phải dài từ 6 đến 20 ký tự";
    } else {
      return null;
    }
  }

  public static String checkConfirmPassword(String password, String confirmPassword) {
    if (confirmPassword.trim().length() == 0) {
      return "Vui lòng nhập mật khẩu xác thực";
    } else if (confirmPassword.trim().compareTo(password.trim()) != 0) {
      return "Mật khẩu không khớp";
    } else {
      return null;
    }
  }

  public static String checkStatus(String status) {
    if (!status.equals("1") && !status.equals("0")) {
      return "Trạng thái tài khoản không hợp lệ";
    }
    return null;
  }

  public static String checkRemainingProductQuantity(String quantity, Variant variant) {
    if (variant == null) {
      return null;
    }
    if (variant.getQuantity() == 0) {
      return "Hết hàng.";
    }
    try {
      if (Integer.parseInt(quantity) < 1) {
        return "Số lượng không hợp lệ";
      }
    } catch (NumberFormatException e) {
      return "Số lượng không đúng định dạng";
    }
    if (Integer.parseInt(quantity) > variant.getQuantity()) {
      return "Số lượng sản phẩm không thể đáp ứng. Vui lòng chọn lại số lượng ";
    }
    return null;
  }

}
