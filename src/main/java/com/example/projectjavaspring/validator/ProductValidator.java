package com.example.projectjavaspring.validator;

import com.example.projectjavaspring.model.form.ProductForm;
import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@RequiredArgsConstructor
public class ProductValidator implements Validator {

  private final ProductService productService;
  private static final String DISPLAY_HOME = "displayHome";
  private static final String PRODUCT_SALE = "productSale";
  private static final String PRODUCT_PRICE = "productPrice";
  private static final String CATEGORY_NAME = "categoryName";
  private static final String PRODUCT_NAME = "productName";

  @Override
  public boolean supports(Class<?> clazz) {
    return ProductForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    var productForm = (ProductForm) target;
    String errorName = checkProductName(productForm.getProductName());
    if (errorName != null) {
      errors.rejectValue(PRODUCT_NAME, PRODUCT_NAME, errorName);
    }
    String errorCategory = checkCategory(productForm.getCategoryName());
    if (errorCategory != null) {
      errors.rejectValue(CATEGORY_NAME, CATEGORY_NAME, errorCategory);
    }
    String errorPrice = checkProductPrice(productForm.getProductPrice());
    if (errorPrice != null) {
      errors.rejectValue(PRODUCT_PRICE, PRODUCT_PRICE, errorPrice);
    }
    String errorSale = checkProductSale(productForm.getProductSale());
    if (errorSale != null) {
      errors.rejectValue(PRODUCT_SALE, PRODUCT_SALE, errorSale);
    }
    String errorDisplay = checkDisplayHome(productForm.getDisplayHome());
    if (errorDisplay != null) {
      errors.rejectValue(DISPLAY_HOME, DISPLAY_HOME, errorDisplay);
    }

    String errorVariant = checkVariant(productForm.getVariants());
    if (errorVariant != null) {
      errors.rejectValue("variants", "variants", errorVariant);
    }

  }

  public void validateEdit(Object target, Errors errors) {
    var productForm = (ProductForm) target;
    String errorName = checkProductName(productForm.getProductName());
    if (errorName != null) {
      errors.rejectValue(PRODUCT_NAME, PRODUCT_NAME, errorName);
    }
    String errorCategory = checkCategory(productForm.getCategoryName());
    if (errorCategory != null) {
      errors.rejectValue(CATEGORY_NAME, CATEGORY_NAME, errorCategory);
    }
    String errorPrice = checkProductPrice(productForm.getProductPrice());
    if (errorPrice != null) {
      errors.rejectValue(PRODUCT_PRICE, PRODUCT_PRICE, errorPrice);
    }
    String errorSale = checkProductSale(productForm.getProductSale());
    if (errorSale != null) {
      errors.rejectValue(PRODUCT_SALE, PRODUCT_SALE, errorSale);
    }
    String errorDisplay = checkDisplayHome(productForm.getDisplayHome());
    if (errorDisplay != null) {
      errors.rejectValue(DISPLAY_HOME, DISPLAY_HOME, errorDisplay);
    }

  }

  public String checkVariant(List<VariantForm> variants) {
    if (variants == null || variants.isEmpty()) {
      return "Vui lòng chọn ít nhất 1 màu và kích cỡ của sản phẩm";
    }
    return null;
  }

  public String checkProductName(String productName) {
    if (productName.trim().length() == 0) {
      return "Vui lòng nhập tên sản phẩm";
    } else if (productName.length() > 50) {
      return "Tên sản phẩm dài quá giới hạn(50 ký tự)";
    } else {
      return null;
    }
  }

  public String checkCategory(String category) {
    if (category.trim().length() == 0) {
      return "Vui lòng chọn loại sản phẩm";
    } else {
      return null;
    }
  }

  public String checkProductPrice(String productPrice) {
    if (productPrice.trim().length() == 0) {
      return "Vui lòng nhập giá bán sản phẩm";
    } else {
      try {
        if (Integer.parseInt(productPrice) <= 0) {
          return "Giá bán sản phẩm không hợp lệ";
        } else {
          return null;
        }
      } catch (NumberFormatException e) {
        return "Giá bán sản phẩm không đúng định dạng";
      }
    }
  }

  public String checkProductSale(String productSale) {
    if (productSale.trim().length() > 0) {
      try {
        if (Integer.parseInt(productSale) < 0 || Integer.parseInt(productSale) > 100) {
          return "Giảm giá sản phẩm không hợp lệ";
        } else {
          return null;
        }
      } catch (NumberFormatException e) {
        return "Giảm giá sản phẩm không đúng định dạng";
      }
    } else {
      return null;
    }
  }

  public String checkDisplayHome(String displayHome) {
    if (displayHome.compareTo("1") != 0 && displayHome.compareTo("0") != 0) {
      return "Vui lòng chọn sản phẩm có được hiển thị lên trang chủ hay không";
    } else {
      return null;
    }
  }

}
