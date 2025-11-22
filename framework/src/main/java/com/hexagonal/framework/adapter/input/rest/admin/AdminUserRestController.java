package com.hexagonal.framework.adapter.input.rest.admin;

import com.hexagonal.application.dto.SupportIssueCommand;
import com.hexagonal.application.usecase.admin.user.HandleSupportIssueUseCase;
import com.hexagonal.application.usecase.admin.user.ToggleCustomerActiveUseCase;
import com.hexagonal.application.usecase.admin.user.ViewCustomersUseCase;
import com.hexagonal.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserRestController {
    
    private final ViewCustomersUseCase viewCustomersUseCase;
    private final ToggleCustomerActiveUseCase toggleCustomerActiveUseCase;
    private final HandleSupportIssueUseCase handleSupportIssueUseCase;
    
    public AdminUserRestController(
            ViewCustomersUseCase viewCustomersUseCase,
            ToggleCustomerActiveUseCase toggleCustomerActiveUseCase,
            HandleSupportIssueUseCase handleSupportIssueUseCase) {
        this.viewCustomersUseCase = viewCustomersUseCase;
        this.toggleCustomerActiveUseCase = toggleCustomerActiveUseCase;
        this.handleSupportIssueUseCase = handleSupportIssueUseCase;
    }
    
    @GetMapping
    public ResponseEntity<List<Customer>> viewCustomers() {
        List<Customer> customers = viewCustomersUseCase.execute();
        return ResponseEntity.ok(customers);
    }
    
    @PutMapping("/{customerId}/toggle-active")
    public ResponseEntity<Customer> toggleCustomerActive(
            @PathVariable String customerId,
            @RequestParam boolean enable) {
        Customer customer = toggleCustomerActiveUseCase.execute(
            com.hexagonal.vo.ID.of(customerId),
            enable
        );
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/support")
    public ResponseEntity<Void> handleSupportIssue(@RequestBody SupportIssueCommand command) {
        handleSupportIssueUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}

