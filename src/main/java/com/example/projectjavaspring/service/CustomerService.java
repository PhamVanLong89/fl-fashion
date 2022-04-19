package com.example.projectjavaspring.service;

import com.example.projectjavaspring.model.entity.Customer;
import com.example.projectjavaspring.model.form.CustomerForm;
import com.example.projectjavaspring.repository.CustomerRepository;
import com.example.projectjavaspring.security.CustomerDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  public Customer findByEmail(String email) {
    return customerRepository.findByEmail(email);
  }

  public Customer save(Customer customer) {
    return customerRepository.save(customer);
  }

  public List<Customer> findAll() {
    return customerRepository.findAll();
  }

  public Customer finById(int id) {
    return customerRepository.findByCustomerId(id);
  }

  public List<Customer> findByName(String customerName) {
    return customerRepository.findByName(customerName);
  }

  public void changeStatus(Customer customer) {
    customerRepository
        .changeStatus(String.valueOf(customer.getStatusActive()), customer.getCustomerId());
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var customer = customerRepository.findByEmail(email);
    if (customer == null) {
      throw new UsernameNotFoundException("Email khoản không tồn tại!");
    }
    return new CustomerDetails(customer);
  }

  public CustomerForm convertToCustomerForm(Customer customer) {
    var customerForm = new CustomerForm();
    customerForm.setCustomerId(String.valueOf(customer.getCustomerId()));
    customerForm.setCustomerName(customer.getCustomerName());
    customerForm.setNumberPhone(customer.getNumberPhone());
    customerForm.setAddress(customer.getAddress());
    customerForm.setEmail(customer.getEmail());
    customerForm.setPassword(customer.getPassword());
    customerForm.setStatusActive(String.valueOf(customer.getStatusActive()));
    return customerForm;
  }
}
