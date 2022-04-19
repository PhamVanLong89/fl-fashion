package com.example.projectjavaspring.repository;

import com.example.projectjavaspring.model.entity.Admin;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

@Repository
public class AdminRepositoryImpl {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Admin> findByName(String adminName) {
    String[] keys = adminName.split(" ");
    var cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Admin> query = cb.createQuery(Admin.class);
    Root<Admin> admin = query.from(Admin.class);
    List<Predicate> predicates = new ArrayList<>();
    for (String key : keys) {
      predicates.add(cb.like(admin.get("adminName"), "%" + key + "%"));
    }
    query.select(admin)
        .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    return entityManager.createQuery(query)
        .getResultList();
  }

}
