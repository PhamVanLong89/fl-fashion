package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Admin;
import com.example.projectjavaspring.model.form.AdminForm;
import com.example.projectjavaspring.security.AdminDetails;
import com.example.projectjavaspring.service.AdminService;
import com.example.projectjavaspring.util.ImageUtil;
import com.example.projectjavaspring.validator.AdminValidator;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/manager")
@RequiredArgsConstructor
public class AdminController {

  private static final String ADD_ADMIN_VIEW = "Admin/AddAdminView";
  private static final String ADMIN = "admin";
  private static final String MESSAGE = "message";

  private final AdminValidator adminValidator;
  private final AdminService adminService;

  @GetMapping(value = {"/", ""})
  public String displayHomePage() {
    return "Admin/HomeView";
  }

  @GetMapping(value = {"/admin"})
  public String displayViewAddAdmin(Model model) {
    model.addAttribute(ADMIN, new AdminForm());
    return ADD_ADMIN_VIEW;
  }

  @PostMapping(value = {"/admin"})
  public String addAdmin(Model model, @ModelAttribute("admin") @Validated AdminForm adminForm,
      BindingResult result) throws IOException {
    adminValidator.validate(adminForm, result);
    if (!result.hasErrors()) {
      var passwordEncoder = new BCryptPasswordEncoder();
      var admin = new Admin();
      admin.setAdminName(adminForm.getAdminName());
      admin.setNumberPhone(adminForm.getNumberPhone());
      admin.setSex(adminForm.getSex());
      admin.setAddress(adminForm.getAddress());
      admin.setEmail(adminForm.getEmail());
      admin.setPassword(passwordEncoder.encode(adminForm.getPassword()));
      admin.setImage(ImageUtil.uploadImage(adminForm.getFile()));
      adminService.save(admin);
      model.addAttribute(ADMIN, new AdminForm());
      model.addAttribute(MESSAGE, "Thêm tài khoản admin thành công");
    } else {
      model.addAttribute(ADMIN, adminForm);
    }
    return ADD_ADMIN_VIEW;
  }

  @GetMapping(value = {"/admins"})
  public String showAdminList(Model model) {
    List<Admin> adminList = adminService.findAll();
    model.addAttribute("admins", adminList);
    return "Admin/AdminListView";
  }

  @GetMapping(value = {"/admin/{adminId}"})
  public String showAdminInfo(Model model, @PathVariable(name = "adminId") int adminId)
      throws NumberFormatException {
    var admin = adminService.findById(adminId);
    if (admin == null) {
      return "redirect:/manager/admins";
    }
    model.addAttribute(ADMIN, admin);
    return "Admin/AdminDetail";
  }

  @ExceptionHandler({NumberFormatException.class})
  public String ex(NumberFormatException e) {
    return "redirect:/manager/admins";
  }

  @GetMapping(value = {"/admin/search"})
  public String searchAdmin(Model model,
      @RequestParam(name = "key", required = false, defaultValue = "") String adminName) {
    List<Admin> adminList = adminService.findByName(adminName);
    model.addAttribute("admins", adminList);
    model.addAttribute("key", adminName);
    return "Admin/AdminListView";
  }

  @GetMapping(value = {"/login"})
  public String showLoginView(Model model) {
    model.addAttribute(ADMIN, new AdminForm());
    return "Admin/LoginView";
  }

  @GetMapping(value = "/logout")
  public String logout() {
    SecurityContextHolder.getContext().setAuthentication(null);
    return "redirect:/manager/login";
  }

  @GetMapping(value = "/admin/profile")
  public String showEditProfileView(@AuthenticationPrincipal AdminDetails adminLogged,
      Model model) {
    String email = adminLogged.getUsername();
    var admin = adminService.findByEmail(email);
    model.addAttribute(ADMIN, admin);
    return "Admin/EditAdminView";
  }

  @PostMapping(value = "/admin/profile")
  public String editProfile(@AuthenticationPrincipal AdminDetails adminLogged, Model model,
      @ModelAttribute("admin") @Validated AdminForm adminForm, BindingResult result)
      throws IOException {
    adminValidator.validateAdminUpdate(adminForm, result);
    String email = adminLogged.getUsername();
    var admin = adminService.findByEmail(email);
    if (!result.hasErrors()) {
      admin.setAdminName(adminForm.getAdminName());
      admin.setNumberPhone(adminForm.getNumberPhone());
      admin.setSex(adminForm.getSex());
      admin.setAddress(adminForm.getAddress());
      String fileName = ImageUtil.uploadImage(adminForm.getFile());
      if (fileName != null) {
        admin.setImage(fileName);
      }
      var adminUpdated = adminService.save(admin);
      //update admin logged
      updateAdminLogged(adminLogged, adminUpdated);
      model.addAttribute(MESSAGE, "Sửa thông tin cá nhân thành công");
    }
    adminForm.setImage(admin.getImage());
    model.addAttribute(ADMIN, adminForm);
    return "Admin/EditAdminView";
  }

  public void updateAdminLogged(AdminDetails adminLogged, Admin admin) {
    adminLogged.setAdminName(admin.getAdminName());
    adminLogged.setPhoneNumber(admin.getNumberPhone());
    adminLogged.setGender(admin.getSex());
    adminLogged.setAddress(admin.getAddress());
    adminLogged.setImage(admin.getImage());
  }

  @GetMapping(value = "/admin/ChangePassword")
  public String showChangePasswordView(Model model) {
    model.addAttribute(ADMIN, new AdminForm());
    return "Admin/ChangePasswordView";
  }

  @PostMapping(value = "/admin/ChangePassword")
  public String changePasswordView(@AuthenticationPrincipal AdminDetails adminLogged, Model model,
      @ModelAttribute("admin") @Validated AdminForm adminForm, BindingResult result) {
    String email = adminLogged.getUsername();
    var admin = adminService.findByEmail(email);
    adminValidator.validateChangePassword(adminForm, admin.getPassword(), result);
    if (!result.hasErrors()) {
      var passwordEncoder = new BCryptPasswordEncoder();
      admin.setPassword(passwordEncoder.encode(adminForm.getNewPassword()));
      adminService.save(admin);
      model.addAttribute(MESSAGE, "Đổi mật khẩu thành công");
    } else {
      model.addAttribute(ADMIN, adminForm);
    }
    return "Admin/ChangePasswordView";
  }

}
