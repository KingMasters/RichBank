package com.hexagonal.framework.adapter.input.rest.customer;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.dto.ViewCartCommand;
import com.hexagonal.application.usecase.customer.cart.AddProductToCartUseCase;
import com.hexagonal.application.usecase.customer.cart.RemoveProductFromCartUseCase;
import com.hexagonal.application.usecase.customer.cart.UpdateProductQuantityInCartUseCase;
import com.hexagonal.application.usecase.customer.cart.ViewCartUseCase;
import com.hexagonal.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/cart")
public class CustomerCartRestController {
    
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final RemoveProductFromCartUseCase removeProductFromCartUseCase;
    private final UpdateProductQuantityInCartUseCase updateProductQuantityInCartUseCase;
    private final ViewCartUseCase viewCartUseCase;
    
    public CustomerCartRestController(
            AddProductToCartUseCase addProductToCartUseCase,
            RemoveProductFromCartUseCase removeProductFromCartUseCase,
            UpdateProductQuantityInCartUseCase updateProductQuantityInCartUseCase,
            ViewCartUseCase viewCartUseCase) {
        this.addProductToCartUseCase = addProductToCartUseCase;
        this.removeProductFromCartUseCase = removeProductFromCartUseCase;
        this.updateProductQuantityInCartUseCase = updateProductQuantityInCartUseCase;
        this.viewCartUseCase = viewCartUseCase;
    }
    
    @PostMapping("/add")
    public ResponseEntity<Cart> addProduct(@RequestBody AddProductToCartCommand command) {
        Cart cart = addProductToCartUseCase.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<Cart> removeProduct(@RequestBody RemoveProductFromCartCommand command) {
        Cart cart = removeProductFromCartUseCase.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @PutMapping("/update-quantity")
    public ResponseEntity<Cart> updateQuantity(@RequestBody UpdateProductQuantityInCartCommand command) {
        Cart cart = updateProductQuantityInCartUseCase.execute(command);
        return ResponseEntity.ok(cart);
    }
    
    @GetMapping("/view")
    public ResponseEntity<Cart> viewCart(@RequestParam String customerId) {
        ViewCartCommand command = new ViewCartCommand(customerId);
        Cart cart = viewCartUseCase.execute(command);
        return ResponseEntity.ok(cart);
    }
}

