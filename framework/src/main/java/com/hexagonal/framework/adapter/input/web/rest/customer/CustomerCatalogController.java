package com.hexagonal.framework.adapter.input.web.rest.customer;

import com.hexagonal.application.dto.FilterProductsByCategoryCommand;
import com.hexagonal.application.dto.SearchProductsCommand;
import com.hexagonal.application.dto.SortProductsCommand;
import com.hexagonal.application.port.in.customer.catalog.FilterProductsByCategoryUseCase;
import com.hexagonal.application.port.in.customer.catalog.ListAllProductsUseCase;
import com.hexagonal.application.port.in.customer.catalog.SearchProductsUseCase;
import com.hexagonal.application.port.in.customer.catalog.SortProductsUseCase;
import com.hexagonal.domain.entity.Product;
import com.hexagonal.framework.adapter.input.web.WebAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/api/customer/catalog")
public class CustomerCatalogController {
    
    private final ListAllProductsUseCase listAllProductsUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final FilterProductsByCategoryUseCase filterProductsByCategoryUseCase;
    private final SortProductsUseCase sortProductsUseCase;
    
    public CustomerCatalogController(
            ListAllProductsUseCase listAllProductsUseCase,
            SearchProductsUseCase searchProductsUseCase,
            FilterProductsByCategoryUseCase filterProductsByCategoryUseCase,
            SortProductsUseCase sortProductsUseCase) {
        this.listAllProductsUseCase = listAllProductsUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
        this.filterProductsByCategoryUseCase = filterProductsByCategoryUseCase;
        this.sortProductsUseCase = sortProductsUseCase;
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> listAllProducts() {
        List<Product> products = listAllProductsUseCase.execute();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String searchTerm) {
        SearchProductsCommand command = new SearchProductsCommand(searchTerm);
        List<Product> products = searchProductsUseCase.execute(command);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/products/filter")
    public ResponseEntity<List<Product>> filterProductsByCategory(@RequestParam String categoryId) {
        FilterProductsByCategoryCommand command = new FilterProductsByCategoryCommand(categoryId);
        List<Product> products = filterProductsByCategoryUseCase.execute(command);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping("/products/sort")
    public ResponseEntity<List<Product>> sortProducts(@RequestBody SortProductsCommand command) {
        List<Product> allProducts = listAllProductsUseCase.execute();
        List<Product> sortedProducts = sortProductsUseCase.execute(allProducts, command);
        return ResponseEntity.ok(sortedProducts);
    }
}

