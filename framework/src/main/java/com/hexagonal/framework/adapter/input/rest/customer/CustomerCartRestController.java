package com.hexagonal.framework.adapter.input.rest.customer;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.application.ports.input.customer.cart.AddProductToCartInputPort;
import com.hexagonal.application.ports.input.customer.cart.RemoveProductFromCartInputPort;
import com.hexagonal.application.ports.input.customer.cart.UpdateProductQuantityInCartInputPort;
import com.hexagonal.application.ports.input.customer.cart.ViewCartInputPort;
import com.hexagonal.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/cart")
public class CustomerCartRestController {
    
    private final AddProductToCartInputPort addProductToCartInputPort;
    private final RemoveProductFromCartInputPort removeProductFromCartInputPort;
    private final UpdateProductQuantityInCartInputPort updateProductQuantityInCartInputPort;
    private final ViewCartInputPort viewCartInputPort;
    
    public CustomerCartRestController(
            AddProductToCartInputPort addProductToCartInputPort,
            RemoveProductFromCartInputPort removeProductFromCartInputPort,
            UpdateProductQuantityInCartInputPort updateProductQuantityInCartInputPort,
            ViewCartInputPort viewCartInputPort) {
        this.addProductToCartInputPort = addProductToCartInputPort;
        this.removeProductFromCartInputPort = removeProductFromCartInputPort;
        this.updateProductQuantityInCartInputPort = updateProductQuantityInCartInputPort;
        this.viewCartInputPort = viewCartInputPort;
    }
    
    @PostMapping("/add")
    public ResponseEntity<Cart> addProduct(@RequestBody AddProductToCartCommand command) {
        Cart cart = addProductToCartInputPort.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeProduct(@RequestBody RemoveProductFromCartCommand command) {
        Cart cart = removeProductFromCartInputPort.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @PutMapping("/update-quantity")
    public ResponseEntity<Cart> updateQuantity(@RequestBody UpdateProductQuantityInCartCommand command) {
        Cart cart = updateProductQuantityInCartInputPort.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping("/view")
    public ResponseEntity<Cart> viewCart(@RequestParam String customerId) {
        ViewCartCommand command = new ViewCartCommand(customerId);
        Cart cart = viewCartInputPort.execute(command);
        return ResponseEntity.ok(cart);
    }
}

