package com.hexagonal.framework.adapter.input.rest.customer;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.application.dto.SortProductsCommand;
import com.hexagonal.application.port.in.customer.catalog.FilterProductsByCategoryInputPort;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsInputPort;
import com.hexagonal.application.port.in.customer.catalog.SearchProductsInputPort;
import com.hexagonal.application.port.in.customer.catalog.SortProductsInputPort;
import com.hexagonal.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/catalog")
public class CustomerCatalogRestController {
    
    private final ListAllProductsInputPort listAllProductsInputPort;
    private final SearchProductsInputPort searchProductsInputPort;
    private final FilterProductsByCategoryInputPort filterProductsByCategoryInputPort;
    private final SortProductsInputPort sortProductsInputPort;
    
    public CustomerCatalogRestController(
            ListAllProductsInputPort listAllProductsInputPort,
            SearchProductsInputPort searchProductsInputPort,
            FilterProductsByCategoryInputPort filterProductsByCategoryInputPort,
            SortProductsInputPort sortProductsInputPort) {
        this.listAllProductsInputPort = listAllProductsInputPort;
        this.searchProductsInputPort = searchProductsInputPort;
        this.filterProductsByCategoryInputPort = filterProductsByCategoryInputPort;
        this.sortProductsInputPort = sortProductsInputPort;
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listAllProducts() {
        List<Product> products = listAllProductsInputPort.execute();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String searchTerm) {
        SearchProductsCommand command = new SearchProductsCommand(searchTerm);
        List<Product> products = searchProductsInputPort.execute(command);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/filter")
    public ResponseEntity<List<Product>> filterProductsByCategory(@RequestParam String categoryId) {
        FilterProductsByCategoryCommand command = new FilterProductsByCategoryCommand(categoryId);
        List<Product> products = filterProductsByCategoryInputPort.execute(command);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/products/sort")
    public ResponseEntity<List<Product>> sortProducts(@RequestBody SortProductsCommand command) {
        List<Product> allProducts = listAllProductsInputPort.execute();
        List<Product> sortedProducts = sortProductsInputPort.execute(allProducts, command);
        return ResponseEntity.ok(sortedProducts);
    }
}

