package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.CartItem;
import com.example.projectjavaspring.model.form.CartItemForm;
import com.example.projectjavaspring.security.CustomerDetails;
import com.example.projectjavaspring.service.CartItemService;
import com.example.projectjavaspring.service.CartService;
import com.example.projectjavaspring.service.VariantService;
import com.example.projectjavaspring.validator.CartItemValidator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CartItemController {

  private final CartService cartService;
  private final CartItemService cartItemService;
  private final CartItemValidator cartItemValidator;
  private final VariantService variantService;

  @ResponseBody
  @PostMapping(value = {"/cart/item"})
  public ResponseEntity<List<FieldError>> addItem(
      @AuthenticationPrincipal CustomerDetails customerLogged, HttpServletResponse response,
      @CookieValue(value = "userId", defaultValue = "") String userId,
      @RequestBody @Validated CartItemForm cartItemForm, BindingResult result) {
    cartItemValidator.validate(cartItemForm, result);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors(), HttpStatus.BAD_REQUEST);
    }

    var cart = cartService.addCart(userId, customerLogged, response);
    var variant = variantService.findByProductIdAndColorAndSize(
        Integer.parseInt(cartItemForm.getVariantForm().getProductForm().getProductId()),
        cartItemForm.getVariantForm().getColor(), cartItemForm.getVariantForm().getSize());
    var itemNew = new CartItem();
    itemNew.setCart(cart);
    itemNew.setQuantity(Integer.parseInt(cartItemForm.getQuantity()));
    itemNew.setVariant(variant);

    CartItem item = cart.getCartItems().stream()
        .filter(i -> i.getVariant().getSKU().equals(itemNew.getVariant().getSKU())).findAny()
        .orElse(null);
    //Nếu đã có sản phẩm trong giỏ hàng thì tăng số lượng
    if (item != null) {
      item.setQuantity(itemNew.getQuantity() + item.getQuantity());
      cartItemValidator
          .validateQuantity(String.valueOf(item.getQuantity()), item.getVariant(), result);
      if (result.hasErrors()) {
        return new ResponseEntity<>(result.getFieldErrors(), HttpStatus.BAD_REQUEST);
      }
      cartItemService.save(item);
      return new ResponseEntity<>(HttpStatus.OK);
    }

    //nếu chưa có sản phẩm trong giỏ hàng thì lưu item mới
    cartItemService.save(itemNew);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ResponseBody
  @DeleteMapping(value = {"/cart/item/{id}"})
  public ResponseEntity<String> deleteItem(@AuthenticationPrincipal CustomerDetails customerLogged,
      @CookieValue(value = "userId", defaultValue = "") String userId,
      @PathVariable(name = "id") int id) throws NumberFormatException {
    if (customerLogged != null) {
      userId = String.valueOf(customerLogged.getCustomerId());
    }
    var cart = cartService.findCartUnpaidByUserId(userId);
    if (cartItemService.deleteByIdAndCart(id, cart) > 0) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @ResponseBody
  @PutMapping(value = {"/cart/item/{id}"})
  public ResponseEntity<List<FieldError>> updateItem(@PathVariable(name = "id") int id,
      @RequestBody @Validated CartItemForm itemForm, BindingResult result,
      @CookieValue(value = "userId", defaultValue = "") String userId,
      @AuthenticationPrincipal CustomerDetails customerLogged) throws NumberFormatException {
    if (customerLogged != null) {
      userId = String.valueOf(customerLogged.getCustomerId());
    }
    var cart = cartService.findCartUnpaidByUserId(userId);
    var item = cartItemService.findByCartAndId(cart, id);
    if (item == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    cartItemValidator.validateQuantity(itemForm.getQuantity(), item.getVariant(), result);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors(), HttpStatus.BAD_REQUEST);
    }
    item.setQuantity(Integer.parseInt(itemForm.getQuantity()));
    cartItemService.save(item);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
