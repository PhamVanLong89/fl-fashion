package com.example.projectjavaspring.model.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Revenue {

  @Id
  private LocalDate date;
  private Long revenue;
}
