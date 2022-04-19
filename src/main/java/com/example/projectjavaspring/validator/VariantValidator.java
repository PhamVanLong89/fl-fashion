package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.service.VariantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class VariantValidator implements Validator {

  private final VariantService variantService;
  private static final String FREE_SIZE = "Free size";
  private static final String QUANTITY = "quantity";

  @Override
  public boolean supports(Class<?> clazz) {
    return VariantForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    //override
  }

  public void validate(Object target, Errors errors, List<VariantForm> variants) {
    var variantForm = (VariantForm) target;
    String errorSKU = checkSKU(variantForm.getSKU(), variants);
    if (errorSKU != null) {
      errors.rejectValue("sKU", "sKU", errorSKU);
    }
    String errorColor = checkColor(variantForm.getColor());
    if (errorColor != null) {
      errors.rejectValue("color", "color", errorColor);
    }
    String errorSize = checkSize(variantForm.getSize(), variantForm.getColor(), variants);
    if (errorSize != null) {
      errors.rejectValue("size", "size", errorSize);
    }
    String errorQuantity = checkQuantity(variantForm.getQuantity());
    if (errorQuantity != null) {
      errors.rejectValue(QUANTITY, QUANTITY, errorQuantity);
    }

  }

  public void validateEdit(Object target, Errors errors) {
    var variantForm = (VariantForm) target;
    String errorQuantity = checkQuantity(variantForm.getQuantity());
    if (errorQuantity != null) {
      errors.rejectValue(QUANTITY, QUANTITY, errorQuantity);
    }

  }

  public String checkColor(String color) {
    if (color.trim().length() == 0) {
      return "Vui lòng chọn màu của sản phẩm";
    } else if (color.length() > 10) {
      return "Tên màu dài quá giới hạn (10 ký tự)";
    }
    return null;
  }

  public String checkSize(String size, String color, List<VariantForm> variants) {
    if (size.trim().length() == 0) {
      return "Vui lòng chọn kích cỡ của sản phẩm";
    } else if (size.length() > 10) {
      return "Tên kích cỡ dài quá giới hạn (10 ký tự)";
    }
    if (variants == null) {
      return null;
    }
    for (VariantForm variant : variants) {
      if (color.equals(variant.getColor()) && size.equals(variant.getSize())) {
        return "Sản phẩm màu: " + color + ", cỡ: " + size + " đã được chọn.";
      }
      if (color.equals(variant.getColor()) && size.equals(FREE_SIZE)) {
        return "Sản phẩm màu: " + color
            + " đã được chọn kích cỡ. Vui lòng chọn kích cỡ khác cho màu này.";
      }
      if (color.equals(variant.getColor()) && !size.equals(FREE_SIZE) && variant.getSize()
          .equals(FREE_SIZE)) {
        return "Vui lòng xóa sản phẩm màu: " + color
            + " cỡ: Free size trước khi thêm kích cỡ khác.";
      }
    }
    return null;
  }

  public String checkQuantity(String quantity) {
    if (quantity.trim().length() == 0) {
      return "Vui lòng nhập số lượng sản phẩm";
    } else {
      try {
        if (Integer.parseInt(quantity) < 0) {
          return "Số lượng sản phẩm không hợp lệ";
        }
      } catch (NumberFormatException e) {
        return "Số lượng sản phẩm không đúng định dạng";
      }
    }
    return null;
  }

  public String checkSKU(String sKU, List<VariantForm> variants) {
    if (sKU.trim().length() == 0) {
      return "Vui lòng nhập mã SKU";
    } else if (sKU.length() > 20) {
      return "Mã SKU dài quá giới hạn (20 ký tự)";
    } else if (variantService.findBySKU(sKU) != null) {
      return "Mã SKU đã tồn tại";
    }
    if (variants != null) {
      for (VariantForm variant : variants) {
        if (sKU.equals(variant.getSKU())) {
          return "Mã SKU đã tồn tại";
        }
      }
    }
    return null;
  }
}
