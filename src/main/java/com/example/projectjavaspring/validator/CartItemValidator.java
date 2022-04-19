package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.entity.Variant;
import com.example.projectjavaspring.model.form.CartItemForm;
import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CartItemValidator implements Validator {

  private final VariantService variantService;
  private static final String QUANTITY = "quantity";

  @Override
  public boolean supports(Class<?> clazz) {
    return CartItemForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var cartItem = (CartItemForm) target;
    String errorColor = checkColor(cartItem.getVariantForm());
    if (errorColor != null) {
      errors.rejectValue("variantForm.color", "color", errorColor);
    }
    String errorSize = checkSize(cartItem.getVariantForm());
    if (errorSize != null) {
      errors.rejectValue("variantForm.size", "size", errorSize);
    }
    String errorQuantity = DataValidator
        .checkRemainingProductQuantity(cartItem.getQuantity(), variantService
            .findByProductIdAndColorAndSize(
                Integer.parseInt(cartItem.getVariantForm().getProductForm().getProductId()),
                cartItem.getVariantForm().getColor(), cartItem.getVariantForm().getSize()));
    if (errorQuantity != null) {
      errors.rejectValue(QUANTITY, QUANTITY, errorQuantity);
    }
  }

  public void validateQuantity(String quantity, Variant variant, Errors errors) {
    String errorQuantity = DataValidator.checkRemainingProductQuantity(quantity, variant);
    if (errorQuantity != null) {
      errors.rejectValue(QUANTITY, QUANTITY, errorQuantity);
    }
  }

  public String checkColor(VariantForm variantForm) {
    if (variantForm.getColor().trim().length() == 0) {
      return "Vui lòng chọn màu sản phẩm.";
    }
    if (variantService.findByProductIdAndColor(Integer.parseInt(variantForm.getProductForm().getProductId()),
        variantForm.getColor()) == null) {
      return "Sản phẩm không có màu cần chọn. Vui lòng chọn lại.";
    }
    return null;
  }

  public String checkSize(VariantForm variantForm) {
    if (variantForm.getSize().trim().length() == 0) {
      return "Vui lòng chọn kích cỡ sản phẩm.";
    }
    if (variantService.findByProductIdAndColorAndSize(Integer.parseInt(variantForm.getProductForm().getProductId()),
        variantForm.getColor(), variantForm.getSize()) == null) {
      return "Sản phẩm không có kích cỡ cần chọn. Vui lòng chọn lại";
    }
    return null;
  }


}
