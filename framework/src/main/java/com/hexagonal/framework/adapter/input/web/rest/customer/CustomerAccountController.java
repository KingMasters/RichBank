package com.hexagonal.framework.adapter.input.web.rest.customer;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.application.port.in.customer.account.*;
import com.hexagonal.application.port.in.customer.account.ViewOrderHistoryUseCase.ViewOrderHistoryQuery;
import com.hexagonal.domain.entity.Customer;
import com.hexagonal.domain.entity.Order;
import com.hexagonal.framework.adapter.input.web.WebAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/api/customer/account")
public class CustomerAccountController {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UpdatePersonalInformationUseCase updatePersonalInformationUseCase;
    private final ViewOrderHistoryUseCase viewOrderHistoryUseCase;
    
    public CustomerAccountController(
            RegisterAccountUseCase registerAccountUseCase,
            LoginUseCase loginUseCase,
            LogoutUseCase logoutUseCase,
            UpdatePersonalInformationUseCase updatePersonalInformationUseCase,
            ViewOrderHistoryUseCase viewOrderHistoryUseCase) {
        this.registerAccountUseCase = registerAccountUseCase;
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.updatePersonalInformationUseCase = updatePersonalInformationUseCase;
        this.viewOrderHistoryUseCase = viewOrderHistoryUseCase;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody RegisterAccountCommand command) {
        Customer customer = registerAccountUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody LoginCommand command) {
        Customer customer = loginUseCase.execute(command);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LoginCommand command) {
        logoutUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/personal-info")
    public ResponseEntity<Customer> updatePersonalInfo(@RequestBody UpdatePersonalInformationCommand command) {
        Customer customer = updatePersonalInformationUseCase.execute(command);
        return ResponseEntity.ok(customer);
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> viewOrderHistory(@RequestParam String customerId) {
        ViewOrderHistoryQuery query = new ViewOrderHistoryQuery(customerId);
        List<Order> orders = viewOrderHistoryUseCase.execute(query);
        return ResponseEntity.ok(orders);
    }
}

