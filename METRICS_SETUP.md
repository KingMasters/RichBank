# Metrics Collection with Micrometer and Prometheus

## Overview

Bu modül, RichBank uygulamasında metrikleri toplayıp Prometheus'a beslemek için Micrometer kullanır. Hexagonal (Clean) Architecture'a uygun şekilde tasarlanmıştır.

## Mimari Yapı

### Port Layer (Application)
- **`MetricsPort`**: Metrikleri kaydetmek için define edilen interface
  - Servis execution time kayıtları
  - Success/Failure sayaçları  
  - Business metrikleri
  - Gauge değerleri

### Adapter Layer (Framework)
- **`PrometheusMetricsAdapter`**: MetricsPort'u implement eden adapter
  - Micrometer kullanarak metrikleri toplar
  - Prometheus format'ında hazırlar

- **`MetricsRegistry`**: Merkezi metrik yönetim
  - Counter, Timer ve Gauge oluşturma
  - Metrik caching ve yönetim

- **`MetricsCollectorAspect`**: AOP aspect
  - Otomatik metrik toplama
  - Service metotlarını intercepte eder

- **`MetricsService`**: Utility component
  - Business metrikleri kaydetme için kolaylaştırılmış arayüz

### Configuration
- **`MetricsConfiguration`**: Spring configuration
  - Common tags tanımı
  - Environment bilgileri

## Exposed Metrics

### Service Metrics
```
application_service_execution_time_seconds (Timer)
application_service_invocation_count_total (Counter)
application_service_success_count_total (Counter)
application_service_failure_count_total (Counter)
```

### Business Metrics
```
application_business_* (Custom)
- Cache operations
- API calls
- Database operations
- Queue operations
- Errors
```

## API Endpoints

### Metrics Endpoint
```
GET /actuator/metrics
```
Tüm metrikleri JSON format'ında getir.

### Prometheus Endpoint
```
GET /actuator/prometheus
```
Prometheus format'ında metrikleri sunarak, Prometheus server tarafından scrape edilebilir hale getirir.

### Health Endpoint
```
GET /actuator/health
```
Uygulama sağlık durumunu kontrol et.

### Info Endpoint
```
GET /actuator/info
```
Uygulama bilgileri.

## Kullanım Örnekleri

### 1. Otomatik AOP Metrikleri

Service sınıflarındaki tüm metotlar otomatik olarak metriklenir:

```java
@Service
public class LoginUseService implements LoginUseCase {
    
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        // Bu metot otomatik olarak:
        // - Invocation count artırılır
        // - Execution time kaydedilir
        // - Success/Failure sayılır
        
        return new AuthenticationResponse(...);
    }
}
```

### 2. MetricsService ile Custom Metrikleri

```java
@Service
public class OrderService {
    
    private final MetricsService metricsService;
    
    public OrderService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    public void createOrder(Order order) {
        // Business event kaydı
        metricsService.recordBusinessEvent("order.created");
        
        // Database işlem metrikleri
        metricsService.recordDatabaseOperation("insert", "Order", 45);
        
        // Custom timing
        metricsService.executeWithTiming("OrderService", "createOrder", () -> {
            // İşlem yapılır
        });
    }
    
    public void cacheCategory(Category category) {
        // Cache hit/miss metrikleri
        metricsService.recordCacheOperation("category_cache", "hit");
    }
}
```

### 3. MetricsPort Direkt Kullanımı

```java
@Service
public class AdminService {
    
    private final MetricsPort metricsPort;
    
    public AdminService(MetricsPort metricsPort) {
        this.metricsPort = metricsPort;
    }
    
    public void processPayment(Payment payment) {
        long startTime = System.currentTimeMillis();
        try {
            // İşlem yapılır
            metricsPort.recordServiceSuccess("AdminService", "processPayment");
        } catch (Exception e) {
            metricsPort.recordServiceFailure("AdminService", "processPayment", e.getClass().getSimpleName());
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            metricsPort.recordServiceExecutionTime("AdminService", "processPayment", executionTime);
        }
    }
}
```

## Configuration (application.yml)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        application.service.execution.time: true
      slo:
        application.service.execution.time: 50ms,100ms,200ms,500ms,1s,2s,5s
    tags:
      application: richbank
      version: 0.0.1-SNAPSHOT
```

## Prometheus Setup

### Docker Compose

```yaml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    networks:
      - richbank-network

volumes:
  prometheus-data:

networks:
  richbank-network:
    driver: bridge
```

### Prometheus Configuration (prometheus.yml)

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'richbank'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
```

## Grafana Visualization

Prometheus metrikleri Grafana'da görselleştirmek için:

1. Grafana'da Prometheus datasource ekle
2. Dashboard oluştur ve metrikleri query et

### Örnek Queries

**Service Success Rate:**
```
rate(application_service_success_count_total[5m]) / rate(application_service_invocation_count_total[5m])
```

**Service Execution Time (95th percentile):**
```
histogram_quantile(0.95, application_service_execution_time_seconds_bucket)
```

**Error Rate:**
```
rate(application_service_failure_count_total[5m])
```

**Business Metrics:**
```
rate(application_business_order_created_total[5m])
```

## Metric Types

### Counters
Monoton artan sayaçlar:
- Invocation count
- Success/Failure count
- Cache hits/misses
- Orders created

### Timers
Execution time metrikleri:
- Service execution time
- Database operation time
- API response time

### Gauges
Anlık değer metrikleri:
- Active connections
- Cache size
- Queue depth

## Best Practices

1. **Consistent Naming**: Metrik isimleri consistent ve descriptive olmalı
   - Prefix: `application_` veya `application_business_`
   - Separator: underscore (`_`)
   - Suffix: `_total` (counter), `_seconds` (timer)

2. **Tagging**: Metrikleri kategorize etmek için tags kullan
   ```java
   metricsService.recordApiCall("/api/login", "POST", 200);
   // Tags: endpoint=/api/login, method=POST, status=200
   ```

3. **Error Handling**: Metrik recording başarısız olsa da uygulama devam etmeli
   - Try-catch kullanarak
   - Logging yoluyla track etme

4. **Performance**: Yoğun traffic'te metrik recording overhead'ini minimize et
   - Sampling kullan gerekirse
   - Asynchronous recording düşün

## Troubleshooting

### Metrikleri görmüyorum

1. Actuator endpoints exposed mı kontrol et:
```
GET http://localhost:8080/actuator
```

2. Prometheus endpoint'ine bağlan:
```
GET http://localhost:8080/actuator/prometheus
```

3. Service metotlarının `com.hexagonal.application.service` paketi altında olup olmadığını kontrol et

### High Memory Usage

- Metrik sayısını azalt
- Custom metriklerin tag cardinality'sini kontrol et
- Unnecessary gauges'ı kaldır

## References

- [Micrometer Documentation](https://micrometer.io/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)

