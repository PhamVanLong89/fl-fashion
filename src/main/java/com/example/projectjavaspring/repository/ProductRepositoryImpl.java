package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Product;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl {

  private static final String PRODUCT_PRICE = "productPrice";
  @PersistenceContext
  private EntityManager entityManager;

  public Page<Product> findByName(String productName, Integer minPrice, Integer maxPrice,
      Pageable pageable) {
    String[] keys = productName.split(" ");
    var criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
    Root<Product> product = query.from(Product.class);
    List<Predicate> predicates = new ArrayList<>();
    for (String key : keys) {
      predicates.add(criteriaBuilder.like(product.get("productName"), "%" + key + "%"));
    }
    if (minPrice != null) {
      predicates.add(criteriaBuilder.greaterThanOrEqualTo(
          criteriaBuilder.diff(product.get(PRODUCT_PRICE), criteriaBuilder.prod(
              criteriaBuilder.prod(product.get(PRODUCT_PRICE), product.get("productSale")), 0.01)),
          Double.valueOf(minPrice)));
    }
    if (maxPrice != null) {
      predicates.add(criteriaBuilder.lessThanOrEqualTo(
          criteriaBuilder.diff(product.get(PRODUCT_PRICE), criteriaBuilder.prod(
              criteriaBuilder.prod(product.get(PRODUCT_PRICE),
                  product.get("productSale")), 0.01)), Double.valueOf(maxPrice)));
    }
    query.select(product)
        .where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
    query.orderBy(QueryUtils.toOrders(pageable.getSort(), product, criteriaBuilder));
    TypedQuery<Product> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult((int) pageable.getOffset());
    typedQuery.setMaxResults(pageable.getPageSize());
    List<Product> products = typedQuery.getResultList();
// Create Count Query
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Product> productCount = countQuery.from(Product.class);
    countQuery.select(criteriaBuilder.count(productCount))
        .where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));

    // Fetches the count of all Books as per given criteria
    Long count = entityManager.createQuery(countQuery).getSingleResult();
    return new PageImpl<>(products, pageable, count);
  }

}
