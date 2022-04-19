package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Product;
import com.example.projectjavaspring.model.entity.Variant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, String> {

  Variant findBysKU(String sKU);

  @Override
  Variant save(Variant variant);

  @Modifying
  @Transactional
  int deleteBysKU(String sKU);

  List<Variant> findByProductAndColor(Product product, String color);

  Variant findByProductAndColorAndSize(Product product, String color, String size);

}
