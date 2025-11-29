# Service Architecture Refactoring

## Genel Bakış

Hexagonal mimaride service'ler iki ana katmana ayrılmıştır:

### 1. Domain Services (domain.service)
Sadece **domain logic**'i içeren, **port'a bağımlı olmayan** servislerin bulunduğu katman.

**Özellikler:**
- Domain entity'lerini manipüle eder
- Business rules'ı enforce eder
- Stateless işlemler yapar
- External dependencies'ye bağımlı değildir
- Testleri kolaydır (mock'lama gerekmez)

**Mevcut Domain Services:**

#### ProductDomainService
```java
// Kategorileri ürüne ekle
assignCategoriesToProduct(Product product, Set<ID> categoryIds)

// Stok operasyonları
addStock(Product product, Quantity quantity)
removeStock(Product product, Quantity quantity)
setStock(Product product, Quantity quantity)

// Ürün açıklaması güncelle
updateDescription(Product product, String description)
```

#### CustomerDomainService
```java
// Müşteri bilgilerini güncelle
updatePersonalInformation(Customer customer, String firstName, String lastName)

// Adresi güncelle
updateAddress(Customer customer, Address address)

// Telefon numarasını güncelle
updatePhoneNumber(Customer customer, String phoneNumber)

// Hesabı deaktif et
deactivateAccount(Customer customer)
```

#### CartDomainService
```java
// Sepete ürün ekle (stok kontrolü ile)
addProductToCart(Cart cart, Product product, Quantity quantity)

// Sepetten ürün çıkar
removeProductFromCart(Cart cart, ID productId)

// Sepetteki ürün miktarını güncelle
updateProductQuantityInCart(Cart cart, Product product, Quantity newQuantity)

// Sepeti temizle
clearCart(Cart cart)
```

---

### 2. Application Services (application.service)
**Use case'leri implement eden** ve orchestration'ı yönetilen servislerin bulunduğu katman.

**Özellikler:**
- Port'lar aracılığıyla repository'lere erişir
- Domain service'lerini çağırır
- Business workflow'ları yönetir
- Transaction yönetimini handle eder
- External dependencies'ye bağımlıdır

**Servis Türleri:**

#### A. Basit Application Services (Tek bir Domain Service çağırırlar)

```java
// Örnek: ManageProductStockService
1. Repository'den ürünü al
2. Domain Service'i çağır (addStock, removeStock, setStock)
3. Güncellenmiş ürünü repository'ye kaydet
```

**Örnekler:**
- `UpdateProductService` → ProductDomainService kullanır
- `UpdatePersonalInformationService` → CustomerDomainService kullanır
- `AddProductToCartService` → CartDomainService kullanır
- `RemoveProductFromCartService` → CartDomainService kullanır
- `UpdateProductQuantityInCartService` → CartDomainService kullanır

#### B. Orkestrasyon Services (Birden Fazla İş Yapan)

```java
// Örnek: CompletePurchaseService
1. Müşteri ve sepeti al
2. Sepet ürünlerini doğrula
3. Her ürün için ProductDomainService'i çağır (stok azalt)
4. Order entity'sini oluştur
5. Tüm değişiklikleri kaydet
```

**Örnekler:**
- `CompletePurchaseService` → Karmaşık workflow
- `CreateProductService` → Ürün oluştur + kategorileri ekle
- `AssignProductsToCategoryService` → Kategoriye ürünler ata

#### C. Query Services (Salt Okuma)

```java
// Örnek: ListAllProductsService
- Repository'den veri al
- Filtreleme/Mapping yaparak döndür
```

**Örnekler:**
- `ListAllProductsService`
- `FilterProductsByCategoryService`
- `ViewCartService`
- `ViewOrderHistoryService`

---

## Refactoring Öncesi vs Sonrası

### ÖNCE (Tek Katman)
```
application/service/
├── admin/product/
│   ├── CreateProductService (Port'ı çağırır + domain logic)
│   ├── UpdateProductService (Port'ı çağırır + domain logic)
│   └── ManageProductStockService (Port'ı çağırır + domain logic)
└── customer/cart/
    └── AddProductToCartService (Port'ı çağırır + domain logic)
```

### SONRA (İki Katman)
```
domain/service/
├── ProductDomainService (Pure business logic)
├── CustomerDomainService (Pure business logic)
└── CartDomainService (Pure business logic)

application/service/
├── admin/product/
│   ├── CreateProductService (Orchestration + ProductDomainService)
│   ├── UpdateProductService (Orchestration + ProductDomainService)
│   └── ManageProductStockService (Orchestration + ProductDomainService)
└── customer/cart/
    └── AddProductToCartService (Orchestration + CartDomainService)
```

---

## Faydalı Önemli Noktalar

### ✅ Domain Service Çağırırken
```java
// DOĞRU
Product product = productRepository.findById(productId)...;
productDomainService.addStock(product, quantity);
productRepository.save(product);

// YANLIŞ (Domain logic'i application service'de yapmak)
product.addStock(quantity);
productRepository.save(product);
```

### ✅ Orkestrasyonun Sorumluluğu
```java
// Application Service sırası:
1. Validasyon (business rules check)
2. Verileri repository'den al
3. Domain Service'i çağır
4. Repository'ye kaydet
5. Domain events'leri handle et
```

### ✅ Port Dependency Kuralı
```java
// DOĞRU
public class ProductDomainService {
    // Port dependency yok!
}

// YANLIŞ
public class ProductDomainService {
    private final ProductRepositoryPort repository; // ❌
}
```

---

## Tüm Application Services Listesi

### Admin Services

#### Product Management
- **CreateProductService** → Port dependency: ProductRepositoryPort
- **UpdateProductService** → Port dependency: ProductRepositoryPort
- **ManageProductStockService** → Port dependency: ProductRepositoryPort
- **RemoveProductService** → Port dependency: ProductRepositoryPort

#### Category Management
- **AssignProductsToCategoryService** → Port dependency: CategoryRepositoryPort, ProductRepositoryPort

### Customer Services

#### Account Management
- **RegisterAccountService** → Port dependency: CustomerRepositoryPort
- **LoginUseService** → Port dependency: CustomerRepositoryPort
- **LogoutService** → No port dependency
- **UpdatePersonalInformationService** → Port dependency: CustomerRepositoryPort
- **PasswordChangeService** → Port dependency: CustomerRepositoryPort
- **ViewOrderHistoryService** → Port dependency: OrderRepositoryPort (Query)

#### Cart Management
- **AddProductToCartService** → Port dependency: CartRepositoryPort, ProductRepositoryPort
- **UpdateProductQuantityInCartService** → Port dependency: CartRepositoryPort, ProductRepositoryPort
- **RemoveProductFromCartService** → Port dependency: CartRepositoryPort
- **ViewCartService** → Port dependency: CartRepositoryPort (Query)

#### Catalog Management
- **ListAllProductsService** → Port dependency: ProductRepositoryPort (Query)
- **FilterProductsByCategoryService** → Port dependency: ProductRepositoryPort (Query)

#### Checkout Management
- **EnterShippingInformationService** → Port dependency: CartRepositoryPort, CustomerRepositoryPort
- **SelectPaymentMethodService** → Port dependency: CartRepositoryPort
- **ApplyDiscountCodeService** → Port dependency: CartRepositoryPort
- **CompletePurchaseService** → Port dependency: CartRepositoryPort, CustomerRepositoryPort, OrderRepositoryPort, ProductRepositoryPort (Orchestration)

---

## Dependency Injection Örneği

### Domain Service Injection
```java
@Configuration
public class DomainServiceConfig {
    @Bean
    public ProductDomainService productDomainService() {
        return new ProductDomainService();
    }

    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainService();
    }

    @Bean
    public CartDomainService cartDomainService() {
        return new CartDomainService();
    }
}
```

### Application Service Injection
```java
@Configuration
public class ApplicationServiceConfig {
    @Bean
    public ManageProductStockService manageProductStockService(
            ProductRepositoryPort productRepository,
            ProductDomainService productDomainService) {
        return new ManageProductStockService(productRepository, productDomainService);
    }

    @Bean
    public CompletePurchaseService completePurchaseService(
            CartRepositoryPort cartRepository,
            CustomerRepositoryPort customerRepository,
            OrderRepositoryPort orderRepository,
            ProductRepositoryPort productRepository,
            ProductDomainService productDomainService) {
        return new CompletePurchaseService(
            cartRepository, customerRepository, orderRepository, 
            productRepository, productDomainService);
    }
}
```

---

## Testing Stratejisi

### Domain Service Tests (Basit)
```java
@Test
public void testAddStock() {
    // Arrange
    Product product = Product.create(...);
    Quantity quantity = Quantity.of(10);
    
    // Act
    productDomainService.addStock(product, quantity);
    
    // Assert
    assertEquals(10, product.getStockQuantity().getValue());
}
```

### Application Service Tests (Mock'lu)
```java
@Test
public void testManageStock() {
    // Arrange
    when(productRepository.findById(...)).thenReturn(Optional.of(product));
    
    // Act
    Product result = manageProductStockService.execute(command);
    
    // Assert
    verify(productRepository).save(product);
    assertEquals(10, result.getStockQuantity().getValue());
}
```

---

## Özet

| Kriter | Domain Service | Application Service |
|--------|---|---|
| **Port Bağımlılığı** | ❌ Yok | ✅ Var |
| **Sorumluluğu** | Business Logic | Orchestration |
| **Harici Bağımlılık** | ❌ Yok | ✅ Var (Repository) |
| **Entity İşlemi** | ✅ Evet | ✅ Evet (Dolaylı) |
| **Test Kolaylığı** | ✅ Çok kolay | ⚠️ Mock gerekir |
| **Reusability** | ✅ Yüksek | ⚠️ Orta |


