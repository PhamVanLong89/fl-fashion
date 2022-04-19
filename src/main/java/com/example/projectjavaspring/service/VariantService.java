package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Variant;
import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.repository.ProductRepository;
import com.example.projectjavaspring.repository.VariantRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VariantService {

  private final VariantRepository variantRepository;
  private final ProductRepository productRepository;
  private final ProductService productService;

  public Variant findBySKU(String sKU) {
    return variantRepository.findBysKU(sKU);
  }

  public Variant save(Variant variant) {
    return variantRepository.save(variant);
  }

  public List<Variant> saveAll(List<Variant> variants) {
    List<Variant> result = new ArrayList<>();
    for (Variant variant : variants) {
      result.add(variantRepository.save(variant));
    }
    return result;
  }

  public int deleteBySKU(String sKU) {
    return variantRepository.deleteBysKU(sKU);
  }

  public VariantForm convertToVariantForm(Variant variant) {
    var variantForm = new VariantForm();
    variantForm.setProductForm(productService.convertProductToProductForm(variant.getProduct()));
    variantForm.setColor(variant.getColor());
    variantForm.setSize(variant.getSize());
    variantForm.setSKU(variant.getSKU());
    variantForm.setQuantity(String.valueOf(variant.getQuantity()));
    variantForm.setImage1(variant.getImage1());
    variantForm.setImage2(variant.getImage2());
    return variantForm;
  }

  public Variant findByProductIdAndColorAndSize(int productId, String color, String size) {
    var product = productRepository.findByProductId(productId);
    if (product == null) {
      return null;
    }
    return variantRepository.findByProductAndColorAndSize(product, color, size);
  }

  public List<Variant> findByProductIdAndColor(int productId, String color) {
    var product = productRepository.findByProductId(productId);
    if (product == null) {
      return Collections.emptyList();
    }
    return variantRepository.findByProductAndColor(product, color);
  }
}
