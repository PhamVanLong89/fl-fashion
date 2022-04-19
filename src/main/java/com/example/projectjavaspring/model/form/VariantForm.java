package com.example.projectjavaspring.model.form;

import java.io.Serializable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VariantForm implements Serializable {

  private ProductForm productForm;
  private String sKU;
  private String color;
  private String size;
  private String quantity;
  private transient MultipartFile file1;
  private transient MultipartFile file2;
  private String image1;
  private String image2;
}
