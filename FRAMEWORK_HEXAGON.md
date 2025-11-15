# Framework Hexagon Implementation

Bu dokümantasyon, RichBank projesi için Framework Hexagon (Hexagonal Architecture Infrastructure Layer) implementasyonunu açıklar.

## Genel Bakış

Framework katmanı, uygulama katmanı ile dış dünya arasındaki adaptörleri içerir:

- **Input Adapters**: REST API, gRPC, Kafka
- **Output Adapters**: H2 Database, MongoDB
- **Cache**: In-Memory (Spring Boot) ve Redis (gelecek için hazır)

## Input Adapters

### 1. REST API

**Customer Endpoints:**
- `/api/customer/account/*` - Hesap işlemleri (register, login, logout, personal info, order history)
- `/api/customer/catalog/*` - Ürün kataloğu (list, search, filter, sort)
- `/api/customer/cart/*` - Sepet işlemleri (add, remove, update quantity, view)
- `/api/customer/checkout/*` - Ödeme işlemleri (shipping, payment, discount, complete)

**Admin Endpoints:**
- `/api/admin/products/*` - Ürün yönetimi (create, update, delete, stock management)
- `/api/admin/users/*` - Kullanıcı yönetimi (view, toggle active, support issues)
- `/api/admin/orders/*` - Sipariş yönetimi (view all, update status, refund)

### 2. gRPC Services

**Customer Service:**
- `RegisterAccount` - Hesap kaydı
- `Login` - Giriş
- `ListProducts` - Ürün listesi
- `SearchProducts` - Ürün arama
- `ViewCart` - Sepet görüntüleme
- `AddProductToCart` - Sepete ürün ekleme
- `CompletePurchase` - Satın alma tamamlama

**Admin Service:**
- `CreateProduct` - Ürün oluşturma
- `UpdateProduct` - Ürün güncelleme
- `ViewAllOrders` - Tüm siparişleri görüntüleme
- `UpdateOrderStatus` - Sipariş durumu güncelleme
- `ViewCustomers` - Müşterileri görüntüleme
- `ToggleCustomerActive` - Müşteri aktif/pasif durumu

**Not:** gRPC servisleri proto dosyalarından build zamanında generate edilir. `mvn compile` komutu çalıştırıldığında Java sınıfları oluşturulur.

### 3. Kafka Event Listeners

**Customer Topics:**
- `customer.register` - Müşteri kayıt eventi
- `customer.add-to-cart` - Sepete ekleme eventi
- `customer.complete-purchase` - Satın alma tamamlama eventi

**Admin Topics:**
- `admin.create-product` - Ürün oluşturma eventi
- `admin.handle-support-issue` - Destek talebi eventi

## Output Adapters

### 1. H2 Database (In-Memory)

JPA tabanlı H2 database adapter'ı:
- `ProductH2Adapter` - Product repository implementation
- `CustomerH2Adapter` - Customer repository implementation
- JPA Entities: `ProductEntity`, `CustomerEntity`
- JPA Repositories: `ProductJpaRepository`, `CustomerJpaRepository`

**Kullanım:** Varsayılan olarak aktif. H2 console: `http://localhost:8080/h2-console`

### 2. MongoDB

MongoDB tabanlı adapter:
- `ProductMongoAdapter` - Product repository implementation
- `CustomerMongoAdapter` - Customer repository implementation
- MongoDB Documents: `ProductDocument`, `CustomerDocument`
- MongoDB Repositories: `ProductMongoRepository`, `CustomerMongoRepository`

**Kullanım:** `spring.data.mongodb.uri` property'si set edildiğinde aktif olur.

**Not:** H2 ve MongoDB aynı anda aktif olabilir. Conditional on property ile kontrol edilir.

## Cache Configuration

### In-Memory Cache (Varsayılan)

Spring Boot built-in cache (`ConcurrentMapCacheManager`) kullanılır. Herhangi bir ek kurulum gerektirmez.

### Redis Cache (Gelecek için)

Redis cache kullanmak için:
1. Redis server'ı başlatın (Docker: `docker run -d -p 6379:6379 redis`)
2. `application.yaml`'da `spring.cache.type=redis` yapın
3. Redis connection bilgilerini ekleyin

Detaylı bilgi için: `framework/REDIS_SETUP.md`

## Cache Kullanımı

Repository adapter'larında cache annotation'ları kullanılıyor:

```java
@Cacheable(value = "products", key = "#id.value.toString()")
public Optional<Product> findById(ID id) { ... }

@CacheEvict(value = "products", allEntries = true)
public Product save(Product product) { ... }
```

**Cache İsimleri:**
- `products`
- `customers`
- `categories`
- `orders`
- `carts`

## Configuration

Tüm konfigürasyon `framework/src/main/resources/application.yaml` dosyasında:

```yaml
spring:
  # H2 Database
  datasource:
    url: jdbc:h2:mem:richbankdb
  
  # MongoDB
  data:
    mongodb:
      uri: mongodb://localhost:27017/richbank
  
  # Cache
  cache:
    type: simple  # veya 'redis'
  
  # Kafka
  kafka:
    bootstrap-servers: localhost:9092

# gRPC
grpc:
  server:
    port: 9090
```

## Build ve Çalıştırma

### Build
```bash
mvn clean compile
```

gRPC sınıfları otomatik olarak proto dosyalarından generate edilir.

### Çalıştırma
```bash
mvn spring-boot:run
```

veya

```bash
java -jar framework/target/framework-0.0.1-SNAPSHOT.jar
```

## Test Endpoints

### REST API
- Swagger/OpenAPI: (eğer eklendiyse)
- H2 Console: http://localhost:8080/h2-console

### gRPC
- Port: 9090
- Test için: `grpcurl` veya gRPC client kullanın

### Kafka
- Bootstrap servers: localhost:9092
- Topics: `customer.*`, `admin.*`

## Notlar

1. **gRPC**: Proto dosyalarından generate edilen sınıflar build klasöründe oluşur. IDE'de hata gösterebilir ama build başarılı olur.

2. **Database Seçimi**: H2 ve MongoDB aynı anda aktif olabilir. Her ikisi de conditional on property ile kontrol edilir.

3. **Cache**: Redis kullanmak için sadece `spring.cache.type=redis` yapmanız yeterli. `CacheConfig` otomatik olarak Redis cache manager'ı kullanacaktır.

4. **Kafka**: Kafka server'ın çalışıyor olması gerekir. Test için local Kafka kurulumu veya Docker kullanabilirsiniz.

## Gelecek Geliştirmeler

- [ ] Swagger/OpenAPI dokümantasyonu
- [ ] gRPC reflection desteği
- [ ] Kafka producer'lar (şu anda sadece consumer var)
- [ ] Database migration scripts
- [ ] Health check endpoints
- [ ] Metrics ve monitoring

