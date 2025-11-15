package com.hexagonal.framework.adapter.input.rest.admin;

import com.hexagonal.application.dto.RefundCommand;
import com.hexagonal.application.ports.input.admin.order.HandleReturnOrRefundInputPort;
import com.hexagonal.application.ports.input.admin.order.UpdateOrderStatusInputPort;
import com.hexagonal.application.ports.input.admin.order.ViewAllOrdersInputPort;
import com.hexagonal.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderRestController {
    
    private final ViewAllOrdersInputPort viewAllOrdersInputPort;
    private final UpdateOrderStatusInputPort updateOrderStatusInputPort;
    private final HandleReturnOrRefundInputPort handleReturnOrRefundInputPort;
    
    public AdminOrderRestController(
            ViewAllOrdersInputPort viewAllOrdersInputPort,
            UpdateOrderStatusInputPort updateOrderStatusInputPort,
            HandleReturnOrRefundInputPort handleReturnOrRefundInputPort) {
        this.viewAllOrdersInputPort = viewAllOrdersInputPort;
        this.updateOrderStatusInputPort = updateOrderStatusInputPort;
        this.handleReturnOrRefundInputPort = handleReturnOrRefundInputPort;
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> viewAllOrders() {
        List<Order> orders = viewAllOrdersInputPort.execute();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {
        Order order = updateOrderStatusInputPort.execute(
            com.hexagonal.vo.ID.of(orderId),
            com.hexagonal.vo.OrderStatus.valueOf(status)
        );
        return ResponseEntity.ok(order);
    }
    
    @PostMapping("/refund")
    public ResponseEntity<Order> handleRefund(
            @RequestParam String orderId,
            @RequestBody RefundCommand command) {
        Order order = handleReturnOrRefundInputPort.execute(
            com.hexagonal.vo.ID.of(orderId),
            command
        );
        return ResponseEntity.ok(order);
    }
}

