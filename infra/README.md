# Framework Hexagon - RichBank

Bu modül, Hexagonal Architecture'ın Framework (Infrastructure) katmanını içerir.

## Yapı

### Input Adapters (Giriş Adaptörleri)

1. **REST API** (`adapter/input/rest/`)
   - Customer REST Controllers
   - Admin REST Controllers

2. **gRPC** (`adapter/input/grpc/`)
   - Customer gRPC Service
   - Admin gRPC Service
   - Proto dosyaları: `src/main/proto/`

3. **Kafka** (`adapter/input/kafka/`)
   - Customer Kafka Listeners
   - Admin Kafka Listeners

### Output Adapters (Çıkış Adaptörleri)

1. **H2 Database** (`adapter/output/persistence/h2/`)
   - JPA Entities
   - JPA Repositories
   - Adapters (Output Port implementations)

2. **MongoDB** (`adapter/output/persistence/mongodb/`)
   - MongoDB Documents
   - MongoDB Repositories
   - Adapters (Output Port implementations)

## Cache

- **In-Memory Cache**: Spring Boot built-in cache (varsayılan)
- **Redis Cache**: Gelecekte kullanım için hazır (bakınız: `REDIS_SETUP.md`)

## Build

gRPC sınıflarını oluşturmak için:
```bash
mvn clean compile
```

Proto dosyalarından Java sınıfları otomatik olarak oluşturulacaktır.

## Configuration

Tüm konfigürasyon `src/main/resources/application.yaml` dosyasında yapılır.

## Notlar

- gRPC servisleri proto dosyalarından generate edilir (build zamanında)
- H2 ve MongoDB adapters aynı anda aktif olabilir (conditional on property)
- Cache yapılandırması `CacheConfig.java` dosyasında yapılır

