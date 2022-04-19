package com.example.projectjavaspring.service.mailservice;

import com.example.projectjavaspring.model.form.OrderDetailForm;
import com.example.projectjavaspring.model.form.OrderForm;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailService {

  @Autowired
  public JavaMailSender emailSender;
  @Autowired
  private SpringTemplateEngine templateEngine;

  public void sendSimpleEmail(String toAddress, String subject, String message) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(toAddress);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setText(message);
    emailSender.send(simpleMailMessage);
  }

  public void sendEmailWithAttachment(String toAddress, String subject, String message,
      File[] listAttachments) throws MessagingException {
    MimeMessage mimeMessage = emailSender.createMimeMessage();
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    messageHelper.setTo(toAddress);
    messageHelper.setSubject(subject);
    messageHelper.setText(message);
    FileSystemResource file;
    for (File attachment : listAttachments) {
      file = new FileSystemResource(attachment);
      messageHelper.addAttachment(attachment.getName(), file);
    }
    emailSender.send(mimeMessage);
  }

  public void sendMailTemplate(AbstractEmailContext email, OrderForm orderForm)
      throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
    Context context = new Context();
    context.setVariables(email.getContext());
    String emailContent = templateEngine.process("Customer/OrderEmailTemplate", context);
    mimeMessageHelper.setTo(email.getTo());
    mimeMessageHelper.setSubject(email.getSubject());
    mimeMessageHelper.setFrom(email.getFrom());
    mimeMessageHelper.setText(emailContent, true);
    for (OrderDetailForm orderDetailForm : orderForm.getOrderDetailForms()) {
      FileSystemResource resource = new FileSystemResource(new File(
          "C:\\Users\\Admin\\Documents\\Java\\projectjavaspring\\src\\main\\resources\\static\\img\\"
              + orderDetailForm.getVariantForm().getProductForm().getImage()));
      mimeMessageHelper.addInline(orderDetailForm.getOrderDetailId(), resource);
    }
    emailSender.send(message);
  }
}
