package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Variant;
import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.service.ProductService;
import com.example.projectjavaspring.service.VariantService;
import com.example.projectjavaspring.util.ImageUtil;
import com.example.projectjavaspring.validator.VariantValidator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class VariantController {

  private final VariantValidator variantValidator;
  private final VariantService variantService;
  private final ProductService productService;
  private static final String VARIANTS = "variants";
  private static final String SUCCESS = "Success";

  @PostMapping(value = {"/manager/product/variant"})
  @ResponseBody
  public ResponseEntity<String> addVariant(
      @RequestParam(name = "productId", required = false, defaultValue = "") String productId,
      @ModelAttribute("variant") @Validated VariantForm variantForm, BindingResult result,
      HttpSession session) throws NumberFormatException, IOException {
    if (productId == null || productId.equals("")) {
      List<VariantForm> variantForms = (List<VariantForm>) session.getAttribute(VARIANTS);
      variantValidator.validate(variantForm, result, variantForms);
      if (result.hasErrors()) {
        return new ResponseEntity<>(result.getFieldErrors().get(0).getDefaultMessage(),
            HttpStatus.BAD_REQUEST);
      }
      variantForm.setImage1(ImageUtil.uploadImage(variantForm.getFile1()));
      variantForm.setImage2(ImageUtil.uploadImage(variantForm.getFile2()));
      variantForm.setFile1(null);
      variantForm.setFile2(null);
      if (variantForms == null) {
        variantForms = new ArrayList<>();
      }
      variantForms.add(variantForm);
      session.setAttribute(VARIANTS, variantForms);
      return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }

    var product = productService.findByProductId(Integer.parseInt(productId));
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<VariantForm> variantForms = new ArrayList<>();
    for (Variant variant : product.getVariants()) {
      variantForms.add(variantService.convertToVariantForm(variant));
    }
    variantValidator.validate(variantForm, result, variantForms);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors().get(0).getDefaultMessage(),
          HttpStatus.BAD_REQUEST);
    }
    var variant = new Variant();
    variant.setProduct(product);
    variant.setColor(variantForm.getColor());
    variant.setSize(variantForm.getSize());
    variant.setSKU(variantForm.getSKU());
    variant.setQuantity(Integer.parseInt(variantForm.getQuantity()));
    variant.setImage1(ImageUtil.uploadImage(variantForm.getFile1()));
    variant.setImage2(ImageUtil.uploadImage(variantForm.getFile2()));
    variantService.save(variant);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }

  @DeleteMapping(value = {"/manager/product/variant/{sKU}"})
  @ResponseBody
  public ResponseEntity<String> deleteVariant(@PathVariable(name = "sKU") String sKU,
      @RequestParam(name = "productId", required = false, defaultValue = "") String productId,
      HttpSession session) throws NumberFormatException {

    if (productId == null || productId.equals("")) {
      List<VariantForm> variantForms = (List<VariantForm>) session.getAttribute(VARIANTS);
      for (VariantForm variant : variantForms) {
        if (sKU.equals(variant.getSKU())) {
          variantForms.remove(variant);
          break;
        }
      }
      return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }
    var product = productService.findByProductId(Integer.parseInt(productId));
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    variantService.deleteBySKU(sKU);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }

  @PutMapping(value = {"/manager/product/variant/{sKU}"})
  @ResponseBody
  public ResponseEntity<String> editVariant(
      @PathVariable(name = "sKU") String sKU,
      @RequestParam(name = "productId", required = false, defaultValue = "") String productId,
      @ModelAttribute("variant") @Validated VariantForm variantForm, BindingResult result,
      HttpSession session) throws IOException, NumberFormatException {
    variantForm.setSKU(sKU);
    variantValidator.validateEdit(variantForm, result);
    if (result.hasErrors()) {
      return new ResponseEntity<>(result.getFieldErrors().get(0).getDefaultMessage(),
          HttpStatus.BAD_REQUEST);
    }
    if (productId == null || productId.equals("")) {
      editVariantInSession(variantForm, session);
      return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
    }
    var product = productService.findByProductId(Integer.parseInt(productId));
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var variant = variantService.findBySKU(sKU);
    if (variant == null) {
      return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
    String fileName1 = ImageUtil.uploadImage(variantForm.getFile1());
    String fileName2 = ImageUtil.uploadImage(variantForm.getFile2());
    variant.setQuantity(Integer.parseInt(variantForm.getQuantity()));
    if (fileName1 != null) {
      variant.setImage1(fileName1);
    }
    if (fileName2 != null) {
      variant.setImage2(fileName2);
    }
    variantService.save(variant);
    return new ResponseEntity<>(SUCCESS, HttpStatus.OK);
  }

  public void editVariantInSession(VariantForm variantForm, HttpSession session)
      throws IOException {
    String fileName1 = ImageUtil.uploadImage(variantForm.getFile1());
    String fileName2 = ImageUtil.uploadImage(variantForm.getFile2());
    List<VariantForm> variantForms = (List<VariantForm>) session.getAttribute(VARIANTS);
    for (VariantForm vF : variantForms) {
      if (variantForm.getSKU().equals(vF.getSKU())) {
        vF.setQuantity(variantForm.getQuantity());
        if (fileName1 != null) {
          vF.setImage1(fileName1);
        }
        if (fileName2 != null) {
          vF.setImage2(fileName2);
        }
        variantForms.set(variantForms.indexOf(vF), vF);
        break;
      }
    }
  }

  @GetMapping(value = {"/manager/product/variants"})
  @ResponseBody
  public ResponseEntity<List<VariantForm>> getVariantsToAdmin(
      @RequestParam(name = "productId", required = false, defaultValue = "") String productId,
      HttpSession session) throws NumberFormatException {
    if (productId == null || productId.equals("")) {
      List<VariantForm> variantForms = (List<VariantForm>) session.getAttribute(VARIANTS);
      return new ResponseEntity<>(variantForms, HttpStatus.OK);
    }
    var product = productService.findByProductId(Integer.parseInt(productId));
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<VariantForm> variantForms = new ArrayList<>();
    for (Variant variant : product.getVariants()) {
      variantForms.add(variantService.convertToVariantForm(variant));
    }
    return new ResponseEntity<>(variantForms, HttpStatus.OK);
  }

  @GetMapping(value = {"/product/{productId}/variants"})
  @ResponseBody
  public ResponseEntity<List<VariantForm>> findVariantsByProductAndColor(
      @RequestParam(name = "color", required = false, defaultValue = "") String color,
      @PathVariable(name = "productId") int productId) throws NumberFormatException {
    var product = productService.findByProductId(productId);
    if (product == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<VariantForm> variants = new ArrayList<>();
    for (Variant variant : product.getVariants()) {
      if (variant.getColor().equals(color)) {
        variants.add(variantService.convertToVariantForm(variant));
      }
    }
    return new ResponseEntity<>(variants, HttpStatus.OK);
  }

  @ExceptionHandler({NumberFormatException.class})
  public ResponseEntity<String> ex(NumberFormatException e) {
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }
}
