package com.example.projectjavaspring.service.mailservice;

import java.util.Map;
import lombok.Data;

@Data
public class AbstractEmailContext {

  private String from;
  private String to;
  private String subject;
  private String email;
  private String attachment;
  private String fromDisplayName;
  private String emailLanguage;
  private String displayName;
  private String templateLocation;
  private Map<String, Object> context;

}
