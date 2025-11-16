package com.hexagonal.framework.adapter.input.rest.customer;

import com.hexagonal.application.dto.LoginCommand;
import com.hexagonal.application.dto.RegisterAccountCommand;
import com.hexagonal.application.dto.UpdatePersonalInformationCommand;
import com.hexagonal.application.dto.ViewOrderHistoryCommand;
import com.hexagonal.application.dto.ChangePasswordCommand;
import com.hexagonal.application.ports.input.customer.account.LoginInputPort;
import com.hexagonal.application.ports.input.customer.account.LogoutInputPort;
import com.hexagonal.application.ports.input.customer.account.RegisterAccountInputPort;
import com.hexagonal.application.ports.input.customer.account.UpdatePersonalInformationInputPort;
import com.hexagonal.application.ports.input.customer.account.ViewOrderHistoryInputPort;
import com.hexagonal.application.ports.input.customer.account.PasswordChangeInputPort;
import com.hexagonal.entity.Customer;
import com.hexagonal.entity.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/account")
public class CustomerAccountRestController {
    
    private final RegisterAccountInputPort registerAccountInputPort;
    private final LoginInputPort loginInputPort;
    private final LogoutInputPort logoutInputPort;
    private final UpdatePersonalInformationInputPort updatePersonalInformationInputPort;
    private final ViewOrderHistoryInputPort viewOrderHistoryInputPort;
    private final PasswordChangeInputPort passwordChangeInputPort;
    
    public CustomerAccountRestController(
            RegisterAccountInputPort registerAccountInputPort,
            LoginInputPort loginInputPort,
            LogoutInputPort logoutInputPort,
            UpdatePersonalInformationInputPort updatePersonalInformationInputPort,
            ViewOrderHistoryInputPort viewOrderHistoryInputPort,
            PasswordChangeInputPort passwordChangeInputPort) {
        this.registerAccountInputPort = registerAccountInputPort;
        this.loginInputPort = loginInputPort;
        this.logoutInputPort = logoutInputPort;
        this.updatePersonalInformationInputPort = updatePersonalInformationInputPort;
        this.viewOrderHistoryInputPort = viewOrderHistoryInputPort;
        this.passwordChangeInputPort = passwordChangeInputPort;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody RegisterAccountCommand command) {
        Customer customer = registerAccountInputPort.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody LoginCommand command) {
        Customer customer = loginInputPort.execute(command);
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LoginCommand command) {
        logoutInputPort.execute(command);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/personal-info")
    public ResponseEntity<Customer> updatePersonalInfo(@RequestBody UpdatePersonalInformationCommand command) {
        Customer customer = updatePersonalInformationInputPort.execute(command);
        return ResponseEntity.ok(customer);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordCommand command) {
        passwordChangeInputPort.execute(command);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> viewOrderHistory(@RequestParam String customerId) {
        ViewOrderHistoryCommand command = new ViewOrderHistoryCommand(customerId);
        List<Order> orders = viewOrderHistoryInputPort.execute(command);
        return ResponseEntity.ok(orders);
    }
}
