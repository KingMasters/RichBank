package com.hexagonal.framework.adapter.input.rest.admin;

import com.hexagonal.application.dto.SupportIssueCommand;
import com.hexagonal.application.port.in.admin.user.HandleSupportIssueInputPort;
import com.hexagonal.application.port.in.admin.user.ToggleCustomerActiveInputPort;
import com.hexagonal.application.port.in.admin.user.ViewCustomersInputPort;
import com.hexagonal.entity.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserRestController {
    
    private final ViewCustomersInputPort viewCustomersInputPort;
    private final ToggleCustomerActiveInputPort toggleCustomerActiveInputPort;
    private final HandleSupportIssueInputPort handleSupportIssueInputPort;
    
    public AdminUserRestController(
            ViewCustomersInputPort viewCustomersInputPort,
            ToggleCustomerActiveInputPort toggleCustomerActiveInputPort,
            HandleSupportIssueInputPort handleSupportIssueInputPort) {
        this.viewCustomersInputPort = viewCustomersInputPort;
        this.toggleCustomerActiveInputPort = toggleCustomerActiveInputPort;
        this.handleSupportIssueInputPort = handleSupportIssueInputPort;
    }
    
    @GetMapping
    public ResponseEntity<List<Customer>> viewCustomers() {
        List<Customer> customers = viewCustomersInputPort.execute();
        return ResponseEntity.ok(customers);
    }
    
    @PutMapping("/{customerId}/toggle-active")
    public ResponseEntity<Customer> toggleCustomerActive(
            @PathVariable String customerId,
            @RequestParam boolean enable) {
        Customer customer = toggleCustomerActiveInputPort.execute(
            com.hexagonal.vo.ID.of(customerId),
            enable
        );
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping("/support")
    public ResponseEntity<Void> handleSupportIssue(@RequestBody SupportIssueCommand command) {
        handleSupportIssueInputPort.execute(command);
        return ResponseEntity.ok().build();
    }
}

