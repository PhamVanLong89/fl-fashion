package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Customer;
import com.example.projectjavaspring.model.form.CustomerForm;
import com.example.projectjavaspring.security.CustomerDetails;
import com.example.projectjavaspring.service.CustomerService;
import com.example.projectjavaspring.validator.CustomerValidator;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerValidator customerValidator;
  private final CustomerService customerService;
  private static final String MESSAGE = "message";
  private static final String CUSTOMER = "customer";

  @GetMapping(value = {"/register"})
  public String showRegisterView(Model model) {
    model.addAttribute(CUSTOMER, new CustomerForm());
    return "Customer/RegisterView";
  }

  @PostMapping(value = {"/register"})
  public String register(@ModelAttribute("customer") @Validated CustomerForm customerForm,
      Model model, BindingResult result) {
    customerValidator.validate(customerForm, result);
    if (!result.hasErrors()) {
      var passwordEncoder = new BCryptPasswordEncoder();
      var customer = new Customer();
      customer.setCustomerName(customerForm.getCustomerName());
      customer.setNumberPhone(customerForm.getNumberPhone());
      customer.setAddress(customerForm.getAddress());
      customer.setEmail(customerForm.getEmail());
      customer.setPassword(passwordEncoder.encode(customerForm.getPassword()));
      customer.setStatusActive(1);
      customerService.save(customer);
      model.addAttribute(CUSTOMER, new CustomerForm());
      model.addAttribute(MESSAGE, "Đăng kí tài khoản thành công");
    } else {
      model.addAttribute(CUSTOMER, customerForm);
    }
    return "Customer/RegisterView";
  }

  @GetMapping(value = {"/login"})
  public String showLoginView(Model model) {
    model.addAttribute(CUSTOMER, new CustomerForm());
    return "Customer/LoginView";
  }

  @GetMapping(value = "/user/logout")
  public String logout() {
    SecurityContextHolder.getContext().setAuthentication(null);
    return "redirect:/login";
  }

  @GetMapping(value = "/user/profile")
  public String showEditProfileView(@AuthenticationPrincipal CustomerDetails customerLogged,
      Model model) {
    String email = customerLogged.getUsername();
    var customer = customerService.findByEmail(email);
    model.addAttribute(CUSTOMER, customer);
    return "Customer/EditProfileView";
  }

  @PostMapping(value = "/user/profile")
  public String editProfile(@AuthenticationPrincipal CustomerDetails customerLogged, Model model,
      @ModelAttribute("customer") @Validated CustomerForm customerForm, BindingResult result) {
    customerValidator.validateUpdate(customerForm, result);
    String email = customerLogged.getUsername();
    var customer = customerService.findByEmail(email);
    if (!result.hasErrors()) {
      customer.setCustomerName(customerForm.getCustomerName());
      customer.setNumberPhone(customerForm.getNumberPhone());
      customer.setAddress(customerForm.getAddress());
      var customerUpdated = customerService.save(customer);
      //update customer logged
      customerLogged.setCustomerName(customerUpdated.getCustomerName());
      customerLogged.setPhoneNumber(customerUpdated.getNumberPhone());
      customerLogged.setAddress(customerUpdated.getAddress());
      model.addAttribute(MESSAGE, "Sửa thông tin cá nhân thành công");
    }
    model.addAttribute(CUSTOMER, customerForm);
    return "Customer/EditProfileView";
  }

  @GetMapping(value = "/user/ChangePassword")
  public String showChangePasswordView(Model model) {
    model.addAttribute(CUSTOMER, new CustomerForm());
    return "Customer/ChangePasswordView";
  }

  @PostMapping(value = "/user/ChangePassword")
  public String changePassword(@AuthenticationPrincipal CustomerDetails customerLogged, Model model,
      @ModelAttribute("customer") @Validated CustomerForm customerForm, BindingResult result) {
    String email = customerLogged.getUsername();
    var customer = customerService.findByEmail(email);
    customerValidator.validateChangePassword(customerForm, customer.getPassword(), result);
    if (!result.hasErrors()) {
      var passwordEncoder = new BCryptPasswordEncoder();
      customer.setPassword(passwordEncoder.encode(customerForm.getNewPassword()));
      customerService.save(customer);
      model.addAttribute(MESSAGE, "Đổi mật khẩu thành công");
    } else {
      model.addAttribute(CUSTOMER, customerForm);
    }
    return "Customer/ChangePasswordView";
  }

  @GetMapping(value = {"/manager/customers"})
  public String showCustomerListToAdmin(Model model) {
    List<Customer> customerList = customerService.findAll();
    model.addAttribute("customers", customerList);
    return "Admin/CustomerView";
  }

  @GetMapping(value = {"/manager/customer/{customerId}"})
  public String showCustomerInfoToAdmin(Model model,
      @PathVariable(name = "customerId") int customerId) throws NumberFormatException {
    var customer = customerService.finById(customerId);
    if (customer == null) {
      return "redirect:/manager/customers";
    }
    model.addAttribute(CUSTOMER, customer);
    return "Admin/CustomerDetail";
  }

  @ExceptionHandler({NumberFormatException.class})
  public String ex(NumberFormatException e) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    Collection<SimpleGrantedAuthority> authority = (Collection<SimpleGrantedAuthority>) auth
        .getAuthorities();
    if (authority.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
      return "redirect:/manager/customers";
    } else {
      return "redirect:/";
    }
  }

  @GetMapping(value = {"/manager/customer/search"})
  public String searchCustomerByAdmin(Model model,
      @RequestParam(name = "key", required = false, defaultValue = "") String customerName) {
    List<Customer> customerList = customerService.findByName(customerName);
    model.addAttribute("customers", customerList);
    model.addAttribute("key", customerName);
    return "Admin/CustomerView";
  }

  @ResponseBody
  @PutMapping(value = {
      "/manager/customer/{customerId}"}, produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> changeStatusAccountByAdmin(
      @RequestBody @Validated CustomerForm customerForm, BindingResult result,
      @PathVariable(name = "customerId") int customerId) throws NumberFormatException {
    var customer = customerService.finById(customerId);
    if (customer == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    customerValidator.validateStatus(customerForm, result);
    if (!result.hasErrors()) {
      customer.setStatusActive(Integer.parseInt(customerForm.getStatusActive()));
      customerService.changeStatus(customer);
      return new ResponseEntity<>("Thay đổi trạng thái tài khoản thành công", HttpStatus.OK);
    }
    return new ResponseEntity<>(result.getFieldErrors().get(0).getDefaultMessage(),
        HttpStatus.BAD_REQUEST);
  }

}
