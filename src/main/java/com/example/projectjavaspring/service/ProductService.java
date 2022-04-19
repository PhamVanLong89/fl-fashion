package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Product;
import com.example.projectjavaspring.model.entity.Variant;
import com.example.projectjavaspring.model.form.ProductForm;
import com.example.projectjavaspring.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public Page<Product> findByName(String productName, Integer minPrice, Integer maxPrice, Pageable pageable) {
    return productRepository.findByName(productName, minPrice, maxPrice, pageable);
  }

  public Product save(Product product) {
    return productRepository.save(product);
  }

  public Product findByProductId(int productId) {
    return productRepository.findByProductId(productId);
  }

  public int deleteByProductId(int productId) {
    return productRepository.deleteByProductId(productId);
  }

  public Page<Product> findByDisplayHome(Pageable pageable) {
    return productRepository.findByDisplayHome(pageable, 1);
  }

  public Page<Product> findByCategoryAndProductPriceBetween(String category, Integer minPrice,
      Integer maxPrice, Pageable pageable) {
    return productRepository
        .findByCategoryAndProductPriceBetween(category, minPrice, maxPrice, pageable);
  }

  public Page<Product> findNewProductsByCategoryAndProductPriceBetween(String category,
      Integer minPrice, Integer maxPrice, Pageable pageable) {
    return productRepository.findNewProductsByCategoryAndProductPriceBetween(category, minPrice,
        maxPrice, pageable);
  }

  public Page<Product> findDiscountProductByCategoryAndProductPriceBetween(String category,
      Integer minPrice, Integer maxPrice, Pageable pageable) {
    return productRepository.findDiscountProductByCategoryAndProductPriceBetween(category, minPrice,
        maxPrice, pageable);
  }

  public List<Product> findByCategoryName(String category) {
    return productRepository.findByCategoryName(category);
  }

  public ProductForm convertProductToProductForm(Product product) {
    var productForm = new ProductForm();
    productForm.setProductId(String.valueOf(product.getProductId()));
    productForm.setProductName(product.getProductName());
    productForm.setCategoryName(product.getCategoryName());
    productForm.setProductPrice(String.valueOf(product.getProductPrice()));
    productForm.setProductSale(String.valueOf(product.getProductSale()));
    productForm.setDisplayHome(String.valueOf(product.getDisplayHome()));
    productForm.setSaleDate(String.valueOf(product.getSaleDate()));
    productForm.setAdminId(String.valueOf(product.getAdminId()));
    for (Variant variant : product.getVariants()) {
      var imageExist = false;
      if (variant.getImage1() != null) {
        productForm.setImage(variant.getImage1());
        imageExist = true;
      } else if (variant.getImage2() != null) {
        productForm.setImage(variant.getImage2());
        imageExist = true;
      }
      if (imageExist) {
        break;
      }
    }
    return productForm;
  }
}
