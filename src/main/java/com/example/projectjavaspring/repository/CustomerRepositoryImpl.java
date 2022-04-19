package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Customer> findByName(String customerName) {
    String[] keys = customerName.split(" ");
    var cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
    Root<Customer> customer = query.from(Customer.class);
    List<Predicate> predicates = new ArrayList<>();
    for (String key : keys) {
      predicates.add(cb.like(customer.get("customerName"), "%" + key + "%"));
    }
    query.select(customer)
        .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    return entityManager.createQuery(query)
        .getResultList();
  }

}
