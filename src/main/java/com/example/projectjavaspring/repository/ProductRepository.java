package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

  @Override
  Product save(Product product);

  Page<Product> findAll(Pageable pageable);

  Page<Product> findByDisplayHome(Pageable pageable, int displayHome);

  Product findByProductId(int productId);

  @Modifying
  @Transactional
  int deleteByProductId(int productId);

  Page<Product> findByName(String productName, Integer minPrice, Integer maxPrice,
      Pageable pageable);

  @Query("Select p from Product p Where (:category is null or p.categoryName like %:category%) And (:minPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) >= :minPrice) And (:maxPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) <= :maxPrice)")
  Page<Product> findByCategoryAndProductPriceBetween(@Param("category") String category,
      @Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

  @Query("Select p from Product p Where p.categoryName like %:category% AND (ADDDATE(p.saleDate, 60) > CURDATE()) And (:minPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) >= :minPrice) And (:maxPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) <= :maxPrice)")
  Page<Product> findNewProductsByCategoryAndProductPriceBetween(@Param("category") String category,
      @Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

  @Query("Select p from Product p Where p.categoryName like %:category% AND p.productSale > 0 And (:minPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) >= :minPrice) And (:maxPrice is null Or (p.productPrice - ROUND((p.productPrice * p.productSale / 100), -3)) <= :maxPrice)")
  Page<Product> findDiscountProductByCategoryAndProductPriceBetween(
      @Param("category") String category,
      @Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

  List<Product> findByCategoryName(String category);

}
