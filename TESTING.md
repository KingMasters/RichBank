# Testing Documentation

Bu dokümantasyon, RichBank projesindeki test yapısını ve test stratejisini açıklar.

## Test Yapısı

Proje, Hexagonal Architecture prensiplerine uygun olarak üç katmanda test edilmiştir:

### 1. Domain Layer Tests (`domain/src/test/java`)

**Kapsam:**
- Entity testleri (Product, Customer, Order, Cart, vb.)
- Value Object testleri (Money, Email, ID, Quantity, vb.)
- Specification testleri

**Örnekler:**
- `ProductTest.java` - Product entity için 20+ test case
- `CustomerTest.java` - Customer entity için 11 test case
- `MoneyTest.java` - Money value object için 11 test case
- `EmailTest.java` - Email value object için 5 test case

**Test Framework:**
- JUnit 5
- AssertJ
- Mockito (gerektiğinde)

### 2. Application Layer Tests (`application/src/test/java`)

**Kapsam:**
- Use Case testleri
- Input Port testleri
- Business logic testleri

**Örnekler:**
- `CreateProductInputPortTest.java` - Ürün oluşturma use case testleri
- `LoginInputPortTest.java` - Login use case testleri

**Test Framework:**
- JUnit 5
- AssertJ
- Mockito

### 3. Framework Layer Tests (`framework/src/test/java`)

**Kapsam:**
- Adapter testleri (H2, MongoDB)
- Mapper testleri
- REST API contract testleri
- gRPC contract testleri
- Kafka contract testleri

**Örnekler:**
- `ProductH2AdapterTest.java` - H2 adapter testleri
- `ProductEntityMapperTest.java` - Entity mapper testleri
- `CustomerAccountRestControllerTest.java` - REST API contract testleri
- `AdminProductRestControllerTest.java` - Admin REST API testleri
- `CustomerKafkaListenerTest.java` - Kafka listener testleri
- `CustomerGrpcServiceContractTest.java` - gRPC service testleri

**Test Framework:**
- JUnit 5
- AssertJ
- Mockito
- Spring Boot Test
- MockMvc (REST API testleri için)
- Testcontainers (integration testleri için)

## Test Tipleri

### Unit Tests

Domain ve Application katmanlarındaki saf Java sınıfları için unit testler yazılmıştır. Bu testler:
- Hızlı çalışır
- Dış bağımlılıkları mock'lar
- Business logic'i test eder

**Örnek:**
```java
@Test
void shouldCreateProductWithValidData() {
    Product product = Product.create("Test Product", price, "SKU-001");
    assertThat(product).isNotNull();
    assertThat(product.getName()).isEqualTo("Test Product");
}
```

### Contract Tests

Framework katmanındaki adaptörler için contract testler yazılmıştır. Bu testler:
- API contract'larını doğrular
- Request/Response formatlarını test eder
- Integration noktalarını test eder

**REST API Contract Tests:**
```java
@WebMvcTest(CustomerAccountRestController.class)
class CustomerAccountRestControllerTest {
    @Test
    void shouldRegisterCustomerSuccessfully() throws Exception {
        mockMvc.perform(post("/api/customer/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

**Kafka Contract Tests:**
```java
@Test
void shouldHandleCustomerRegisterEvent() throws Exception {
    listener.handleCustomerRegister(message, "customer.register", acknowledgment);
    verify(registerAccountUseCase).execute(any());
    verify(acknowledgment).acknowledge();
}
```

## Test Çalıştırma

### Tüm Testleri Çalıştırma
```bash
mvn test
```

### Belirli Bir Modülün Testlerini Çalıştırma
```bash
mvn test -pl domain
mvn test -pl application
mvn test -pl framework
```

### Belirli Bir Test Sınıfını Çalıştırma
```bash
mvn test -Dtest=ProductTest
```

### Test Coverage Raporu
```bash
mvn test jacoco:report
```

## Test Coverage

### Domain Layer
- ✅ Product Entity: 20+ test cases
- ✅ Customer Entity: 11 test cases
- ✅ Money VO: 11 test cases
- ✅ Email VO: 5 test cases

### Application Layer
- ✅ CreateProductInputPort: 4 test cases
- ✅ LoginInputPort: 4 test cases

### Framework Layer
- ✅ ProductH2Adapter: 8 test cases
- ✅ ProductEntityMapper: 4 test cases
- ✅ CustomerAccountRestController: 3 contract tests
- ✅ AdminProductRestController: 4 contract tests
- ✅ CustomerKafkaListener: 3 contract tests

## Test Best Practices

1. **AAA Pattern (Arrange-Act-Assert)**: Tüm testler bu pattern'i kullanır
2. **Descriptive Test Names**: `@DisplayName` annotation'ı ile açıklayıcı test isimleri
3. **Isolation**: Her test bağımsız çalışır
4. **Mocking**: Dış bağımlılıklar mock'lanır
5. **Assertions**: AssertJ kullanılarak okunabilir assertion'lar

## Gelecek Geliştirmeler

- [ ] Integration testleri (Testcontainers ile)
- [ ] Performance testleri
- [ ] Load testleri
- [ ] E2E testleri
- [ ] Test coverage raporları (Jacoco)
- [ ] Mutation testing (PIT)

## Notlar

- Testler hızlı çalışmalı (unit testler < 1 saniye)
- Integration testleri ayrı bir profile'da çalıştırılabilir
- Contract testleri API değişikliklerini yakalamalı
- Test data'sı için builder pattern kullanılabilir

