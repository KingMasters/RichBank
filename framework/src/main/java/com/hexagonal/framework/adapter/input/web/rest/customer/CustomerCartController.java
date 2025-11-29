package com.hexagonal.framework.adapter.input.web.rest.customer;

import com.hexagonal.application.dto.AddProductToCartCommand;
import com.hexagonal.application.dto.RemoveProductFromCartCommand;
import com.hexagonal.application.dto.UpdateProductQuantityInCartCommand;
import com.hexagonal.application.port.in.customer.cart.AddProductToCartUseCase;
import com.hexagonal.application.port.in.customer.cart.RemoveProductFromCartUseCase;
import com.hexagonal.application.port.in.customer.cart.UpdateProductQuantityInCartUseCase;
import com.hexagonal.application.port.in.customer.cart.ViewCartUseCase;
import com.hexagonal.domain.entity.Cart;
import com.hexagonal.framework.common.WebAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@WebAdapter
@RestController
@RequestMapping("/api/customer/cart")
public class CustomerCartController {
    
    private final AddProductToCartUseCase addProductToCartUseCase;
    private final RemoveProductFromCartUseCase removeProductFromCartUseCase;
    private final UpdateProductQuantityInCartUseCase updateProductQuantityInCartUseCase;
    private final ViewCartUseCase viewCartUseCase;
    
    public CustomerCartController(
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
        ViewCartUseCase.ViewCartQuery query = new ViewCartUseCase.ViewCartQuery(customerId);
        Cart cart = viewCartUseCase.execute(query);
        return ResponseEntity.ok(cart);
    }
}

