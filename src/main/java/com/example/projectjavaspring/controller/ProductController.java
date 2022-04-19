package com.example.projectjavaspring.controller;

import com.example.projectjavaspring.model.entity.Product;
import com.example.projectjavaspring.model.entity.Variant;
import com.example.projectjavaspring.model.form.ProductForm;
import com.example.projectjavaspring.model.form.VariantForm;
import com.example.projectjavaspring.security.AdminDetails;
import com.example.projectjavaspring.service.ProductService;
import com.example.projectjavaspring.service.VariantService;
import com.example.projectjavaspring.validator.ProductValidator;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final ProductValidator productValidator;
  private final VariantService variantService;
  private static final String PRODUCT = "product";
  private static final String MESSAGE = "message";
  private static final String VARIANTS = "variants";
  private static final String URL_PRODUCT_VIEW = "redirect:/manager/products";
  private static final String PRODUCTS = "products";
  private static final String TOTAL_PAGE = "totalPage";
  private static final String CURRENT_PAGE = "currentPage";

  @GetMapping(value = {"/", ""})
  public String showHomePage(Model model) {
    Page<Product> pages = productService.findByDisplayHome(PageRequest.of(0, 3));
    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    model.addAttribute(PRODUCTS, productForms);
    return "Customer/Home";
  }

  @ResponseBody
  @GetMapping(value = {"/products"})
  public ResponseEntity<List<ProductForm>> loadMoreProducts(
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage)
      throws NumberFormatException {
    Page<Product> pages = productService.findByDisplayHome(PageRequest.of(currentPage - 1, 3));
    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    return new ResponseEntity<>(productForms, HttpStatus.OK);
  }

  @GetMapping(value = {"/products/{displayType}/{category}"})
  public String showProductsByCategoryToCustomers(
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      @RequestParam(name = "minPrice", required = false) Integer minPrice,
      @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
      @PathVariable(name = "displayType") String displayType,
      @PathVariable(name = "category") String category, Model model) throws NumberFormatException {
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Product> pages = new PageImpl<>(Collections.emptyList());
    switch (displayType) {
      case "category":
        pages = productService.findByCategoryAndProductPriceBetween(category, minPrice, maxPrice,
            PageRequest.of(currentPage - 1, 3));
        break;
      case "new":
        pages = productService.findNewProductsByCategoryAndProductPriceBetween(category, minPrice,
            maxPrice, PageRequest.of(currentPage - 1, 3));
        break;
      case "sale":
        pages = productService.findDiscountProductByCategoryAndProductPriceBetween(category,
            minPrice, maxPrice, PageRequest.of(currentPage - 1, 3));
        break;
      default:
        break;
    }

    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    model.addAttribute(PRODUCTS, productForms);
    model.addAttribute("displayType", displayType);
    model.addAttribute("category", category);
    model.addAttribute(TOTAL_PAGE, pages.getTotalPages());
    model.addAttribute(CURRENT_PAGE, currentPage);
    return "Customer/ProductsView";
  }

  @GetMapping(value = {"/product/{productId}/{productName}"})
  public String showProductDetailToCustomers(@PathVariable(name = "productId") int productId,
      Model model) throws NumberFormatException {
    var product = productService.findByProductId(productId);
    if (product == null) {
      return "redirect:/";
    }
    Set<String> colors = new HashSet<>();
    for (Variant variant : product.getVariants()) {
      colors.add(variant.getColor());
    }
    model.addAttribute(PRODUCT, product);
    model.addAttribute("colors", colors);
    model.addAttribute("similarProducts", findSimilarProducts(product));
    return "Customer/ProductDetailView";
  }

  public List<ProductForm> findSimilarProducts(Product productInput) {
    List<Product> products = productService.findByCategoryName(productInput.getCategoryName());
    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : products) {
      if (product.getProductId() != productInput.getProductId()) {
        productForms.add(productService.convertProductToProductForm(product));
      }
    }
    return productForms;
  }

  @GetMapping(value = {"/products/search"})
  public String searchProductByCustomer(
      @RequestParam(name = "key", required = false, defaultValue = "") String productName,
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      @RequestParam(name = "minPrice", required = false) Integer minPrice,
      @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
      Model model) throws NumberFormatException {
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Product> pages = productService
        .findByName(productName, minPrice, maxPrice, PageRequest.of(currentPage - 1, 3));
    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    model.addAttribute(PRODUCTS, productForms);
    model.addAttribute("key", productName);
    model.addAttribute(TOTAL_PAGE, pages.getTotalPages());
    model.addAttribute(CURRENT_PAGE, currentPage);
    return "Customer/ProductsSearchView";
  }


  @GetMapping(value = {"/manager/product"})
  public String displayViewAddProduct(Model model, HttpSession session) {
    session.removeAttribute(VARIANTS);
    model.addAttribute(PRODUCT, new ProductForm());
    return "Admin/AddProductView";
  }

  @PostMapping(value = {"/manager/product"})
  public String addProduct(@ModelAttribute("product") @Validated ProductForm productForm,
      Model model, BindingResult result, HttpSession session,
      @AuthenticationPrincipal AdminDetails adminLogged) {
    List<VariantForm> variantForms = (List<VariantForm>) session.getAttribute(VARIANTS);
    productForm.setVariants(variantForms);
    productValidator.validate(productForm, result);
    if (!result.hasErrors()) {
      var product = new Product();
      product.setProductName(productForm.getProductName());
      product.setCategoryName(productForm.getCategoryName());
      product.setProductPrice(Integer.parseInt(productForm.getProductPrice()));
      product.setSaleDate(Date.valueOf(java.time.LocalDate.now()));
      product.setDescription(productForm.getDescription());
      product.setProductSale((productForm.getProductSale().trim().length() == 0) ? 0
          : Integer.parseInt(productForm.getProductSale()));
      product.setDisplayHome(Integer.parseInt(productForm.getDisplayHome()));
      product.setAdminId(adminLogged.getAdminId());
      productService.save(product);

      variantForms.forEach(variantForm -> {
        var variant = new Variant();
        variant.setProduct(product);
        variant.setColor(variantForm.getColor());
        variant.setSize(variantForm.getSize());
        variant.setSKU(variantForm.getSKU());
        variant.setQuantity(Integer.parseInt(variantForm.getQuantity()));
        variant.setImage1(variantForm.getImage1());
        variant.setImage2(variantForm.getImage2());
        variantService.save(variant);
      });
      session.removeAttribute(VARIANTS);
      model.addAttribute(PRODUCT, new ProductForm());
      model.addAttribute(MESSAGE, "Thêm sản phẩm thành công");
    } else {
      model.addAttribute(PRODUCT, productForm);
      model.addAttribute(VARIANTS, variantForms);
    }
    return "Admin/AddProductView";
  }

  @GetMapping(value = {"/manager/products"})
  public String showProductsToAdmins(
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      @RequestParam(name = "sort", required = false, defaultValue = "") String field,
      @RequestParam(name = "type", required = false, defaultValue = "") String type,
      @RequestParam(name = "minPrice", required = false) Integer minPrice,
      @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
      @RequestParam(name = "category", required = false) String category, Model model)
      throws NumberFormatException {
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Product> pages;
    boolean existField = field.equals("productId") || field.equals("productName")
        || field.equals("productPrice") || field.equals("productSale")
        || field.equals("categoryName") || field.equals("saleDate");
    if (existField && type.equals("ASC")) {
      pages = productService.findByCategoryAndProductPriceBetween(category, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3, Sort.by(field).ascending()));
    } else if (existField && type.equals("DESC")) {
      pages = productService.findByCategoryAndProductPriceBetween(category, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3, Sort.by(field).descending()));
    } else {
      pages = productService.findByCategoryAndProductPriceBetween(category, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3));
    }

    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    model.addAttribute(PRODUCTS, productForms);
    model.addAttribute(TOTAL_PAGE, pages.getTotalPages());
    model.addAttribute(CURRENT_PAGE, currentPage);
    return "Admin/ProductView";
  }

  @GetMapping(value = {"/manager/products/search"})
  public String searchProductByAdmin(
      @RequestParam(name = "key", required = false, defaultValue = "") String productName,
      @RequestParam(name = "page", required = false, defaultValue = "1") int currentPage,
      @RequestParam(name = "sort", required = false, defaultValue = "") String field,
      @RequestParam(name = "type", required = false, defaultValue = "") String type,
      @RequestParam(name = "minPrice", required = false) Integer minPrice,
      @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
      Model model) throws NumberFormatException {
    currentPage = (currentPage < 1) ? 1 : currentPage;
    Page<Product> pages;
    boolean existField = field.equals("productId") || field.equals("productName")
        || field.equals("productPrice") || field.equals("productSale")
        || field.equals("categoryName") || field.equals("saleDate");
    if (existField && type.equals("ASC")) {
      pages = productService.findByName(productName, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3, Sort.by(field).ascending()));
    } else if (existField && type.equals("DESC")) {
      pages = productService.findByName(productName, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3, Sort.by(field).descending()));
    } else {
      pages = productService.findByName(productName, minPrice, maxPrice,
          PageRequest.of(currentPage - 1, 3));
    }
    List<ProductForm> productForms = new ArrayList<>();
    for (Product product : pages.getContent()) {
      productForms.add(productService.convertProductToProductForm(product));
    }
    model.addAttribute(PRODUCTS, productForms);
    model.addAttribute("key", productName);
    model.addAttribute(TOTAL_PAGE, pages.getTotalPages());
    model.addAttribute(CURRENT_PAGE, currentPage);
    return "Admin/ProductView";
  }

  @GetMapping(value = {"/manager/product/{productId}"})
  public String showEditProductView(@PathVariable(name = "productId") int productId, Model model)
      throws NumberFormatException {
    var product = productService.findByProductId(productId);
    if (product == null) {
      return URL_PRODUCT_VIEW;
    }
    model.addAttribute(PRODUCT, product);
    return "Admin/EditProductView";
  }

  @ExceptionHandler({NumberFormatException.class})
  public String ex(NumberFormatException e) {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    Collection<SimpleGrantedAuthority> a = (Collection<SimpleGrantedAuthority>) auth
        .getAuthorities();
    if (a.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
      return URL_PRODUCT_VIEW;
    } else {
      return "redirect:/";
    }
  }

  @PostMapping(value = "/manager/product/{productId}")
  public String editProduct(@PathVariable(name = "productId") int productId,
      @ModelAttribute("product") @Validated ProductForm productForm, Model model,
      BindingResult result, @AuthenticationPrincipal AdminDetails adminLogged)
      throws NumberFormatException {
    var product = productService.findByProductId(productId);
    if (product == null) {
      return URL_PRODUCT_VIEW;
    }
    productValidator.validateEdit(productForm, result);
    if (!result.hasErrors()) {
      product.setProductName(productForm.getProductName());
      product.setCategoryName(productForm.getCategoryName());
      product.setProductPrice(Integer.parseInt(productForm.getProductPrice()));
      product.setDescription(productForm.getDescription());
      product.setProductSale((productForm.getProductSale().trim().length() == 0) ? 0
          : Integer.parseInt(productForm.getProductSale()));
      product.setDisplayHome(Integer.parseInt(productForm.getDisplayHome()));
      product.setAdminId(adminLogged.getAdminId());
      productService.save(product);
      model.addAttribute(MESSAGE, "Sửa thông tin sản phẩm thành công");
    }

    List<VariantForm> variantForms = new ArrayList<>();
    for (Variant variant : product.getVariants()) {
      variantForms.add(variantService.convertToVariantForm(variant));
    }
    productForm.setProductId(String.valueOf(productId));
    productForm.setVariants(variantForms);
    model.addAttribute(PRODUCT, productForm);
    return "Admin/EditProductView";
  }

  @DeleteMapping(value = {"/manager/product/{productId}"})
  @ResponseBody
  public ResponseEntity<String> deleteProduct(@PathVariable(name = "productId") int productId)
      throws NumberFormatException {
    productService.deleteByProductId(productId);
    return new ResponseEntity<>("Xóa sản phẩm thành công", HttpStatus.OK);
  }

}
