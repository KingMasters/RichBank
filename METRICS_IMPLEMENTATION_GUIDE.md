# Metrics Implementation Guide

## Quick Start

### 1. Uygulamayı Başlat
```bash
cd /Users/cemilcetin/Documents/Dev/BackendGuru/Code/Java/richbank
./mvnw clean spring-boot:run
```

### 2. Metrikleri Kontrol Et
```bash
# Tüm metrikleri görmek için
curl http://localhost:8080/actuator/metrics

# Prometheus format'ında metrikleri görmek için (scraping)
curl http://localhost:8080/actuator/prometheus
```

### 3. Monitoring Stack'i Başlat
```bash
# Docker Compose ile Prometheus, Grafana, AlertManager'ı başlat
docker-compose -f docker-compose.monitoring.yml up -d
```

### 4. Web Interface'leri Aç
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **AlertManager**: http://localhost:9093

---

## Kullanım Örnekleri

### Örnek 1: Otomatik Metrik Toplama (AOP)

Service sınıfı otomatik olarak metriklendi:

```java
@Service
public class LoginUseService implements LoginUseCase {
    
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        // Bu metot otomatik olarak:
        // 1. Invocation count artırılır
        // 2. Execution time kaydedilir
        // 3. Success/Failure sayılır
        
        try {
            return new AuthenticationResponse(...);
        } catch (Exception e) {
            // Error automatically recorded
            throw e;
        }
    }
}
```

### Örnek 2: Custom Business Metrics

```java
@Service
public class OrderService {
    
    private final MetricsService metricsService;
    
    public OrderService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order();
        
        // Business event kaydı
        metricsService.recordBusinessEvent("order.created", 
            "status", "confirmed",
            "customer_type", request.getCustomerType());
        
        // Cache operation kaydı
        metricsService.recordCacheOperation("category_cache", "hit");
        
        // Database operation kaydı
        metricsService.recordDatabaseOperation("insert", "Order", 45);
        
        return order;
    }
}
```

### Örnek 3: Timed Operation

```java
@Service
public class PaymentService {
    
    private final MetricsService metricsService;
    
    public PaymentService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    
    public PaymentResult processPayment(Payment payment) throws Exception {
        return metricsService.executeWithTiming(
            "PaymentService",
            "processPayment",
            () -> {
                // Payment processing logic
                // Metrics automatically recorded:
                // - Success count if completes successfully
                // - Failure count if throws exception
                // - Execution time in finally block
            }
        );
    }
}
```

### Örnek 4: Custom Metrics (Direct)

```java
@Service
public class AnalyticsService {
    
    private final MetricsPort metricsPort;
    
    public AnalyticsService(MetricsPort metricsPort) {
        this.metricsPort = metricsPort;
    }
    
    public void trackUserBehavior(User user) {
        // Increment counter
        metricsPort.incrementCounter(
            "user.behavior",
            "action", "login",
            "user_type", user.getType()
        );
        
        // Record gauge (current state)
        metricsPort.recordGaugeValue("active.users", getActiveUserCount());
        
        // Record timer
        long queryTime = calculateAnalytics(user);
        metricsPort.recordTimer("analytics.query", queryTime, 
            "user_segment", user.getSegment()
        );
    }
}
```

---

## Prometheus Queries

### Service Health

**Success Rate (son 5 dakika):**
```promql
rate(application_service_success_count_total[5m]) / 
rate(application_service_invocation_count_total[5m])
```

**Error Rate (son 5 dakika):**
```promql
rate(application_service_failure_count_total[5m]) / 
rate(application_service_invocation_count_total[5m])
```

**Total Failures (son 1 saat):**
```promql
increase(application_service_failure_count_total[1h])
```

### Performance Metrics

**Average Response Time (son 5 dakika):**
```promql
rate(application_service_execution_time_seconds_sum[5m]) / 
rate(application_service_execution_time_seconds_count[5m])
```

**95th Percentile Response Time:**
```promql
histogram_quantile(0.95, 
  rate(application_service_execution_time_seconds_bucket[5m]))
```

**99th Percentile Response Time:**
```promql
histogram_quantile(0.99, 
  rate(application_service_execution_time_seconds_bucket[5m]))
```

### Business Metrics

**Order Creation Rate:**
```promql
rate(application_business_order_created_total[5m])
```

**Cache Hit Rate:**
```promql
rate(application_business_cache_category_cache_hit_total[5m]) /
(rate(application_business_cache_category_cache_hit_total[5m]) +
 rate(application_business_cache_category_cache_miss_total[5m]))
```

**Database Query Time (by entity):**
```promql
rate(application_business_database_query_sum[5m]{entity=~"User|Order|Product"}) /
rate(application_business_database_query_count[5m]{entity=~"User|Order|Product"})
```

---

## Grafana Dashboard Oluşturma

### 1. Prometheus Datasource Ekle

1. Grafana'ya git: http://localhost:3000
2. Configuration → Data Sources
3. "Add data source" butonuna tıkla
4. **Type**: Prometheus
5. **URL**: http://prometheus:9090
6. Test et ve kaydet

### 2. Dashboard Oluştur

1. "+" butonundan "Dashboard" seç
2. "Add a new panel" tıkla
3. Query başlığında Prometheus seç

### 3. Example Panels

**Panel 1: Success Rate**
- Title: Service Success Rate
- Query: `rate(application_service_success_count_total[5m]) / rate(application_service_invocation_count_total[5m])`
- Visualization: Gauge

**Panel 2: Request Latency**
- Title: P95 Response Time
- Query: `histogram_quantile(0.95, rate(application_service_execution_time_seconds_bucket[5m]))`
- Visualization: Graph

**Panel 3: Error Count**
- Title: Errors per Second
- Query: `rate(application_service_failure_count_total[5m])`
- Visualization: Stat

**Panel 4: Business Events**
- Title: Orders Created (last hour)
- Query: `increase(application_business_order_created_total[1h])`
- Visualization: Stat

---

## Alerting Setup

### Alert Kuralları Ekle

Prometheus'a alert rules dosyasını ekle:

```yaml
# prometheus.yml içinde
rule_files:
  - '/etc/prometheus/prometheus-rules.yml'

alerting:
  alertmanagers:
    - static_configs:
        - targets:
            - alertmanager:9093
```

### Alert Kanallarını Yapılandır

AlertManager'da Slack, PagerDuty, Email vb. notification kanalları konfigüre et.

**monitoring/alertmanager.yml** dosyasını düzenle:
```yaml
receivers:
  - name: 'default'
    slack_configs:
      - channel: '#alerts'
        api_url: 'YOUR_SLACK_WEBHOOK_URL'
```

---

## Best Practices

### 1. Metric Naming Convention

```
application_[component]_[metric_name]_[unit]_[type]

Örnekler:
- application_service_execution_time_seconds_bucket (timer/histogram)
- application_service_failure_count_total (counter)
- application_business_active_connections_gauge (gauge)
- application_cache_hit_rate_percent (gauge)
```

### 2. Tag Usage

Metrikleri kategorize etmek için tutarlı tag'lar kullan:

```java
// ✅ Doğru
metricsPort.incrementCounter("api.calls", 
    "endpoint", "/api/orders",
    "method", "POST",
    "status", "201");

// ❌ Yanlış (çok fazla unique value)
metricsPort.incrementCounter("api.calls", 
    "user_id", "user_123456",  // High cardinality!
    "request_id", "req_789");
```

### 3. Error Handling

Metrik recording başarısız olsa da uygulama devam etmeli:

```java
try {
    metricsPort.recordServiceSuccess(serviceName, methodName);
} catch (Exception e) {
    logger.warn("Failed to record metric", e);
    // Application continues normally
}
```

### 4. Performance Optimization

Yoğun traffic'te overhead'i minimize et:

```java
// ✅ Doğru: Simple operations
metricsPort.incrementCounter("requests");

// ⚠️ Dikkat: Expensive operations
metricsPort.recordGaugeValue("active_connections", 
    calculateExpensiveConnection());  // This runs every scrape!
```

---

## Troubleshooting

### Problem 1: Metrikleri görmüyorum

**Çözüm:**
1. Actuator endpoints'inin exposed olup olmadığını kontrol et:
   ```bash
   curl http://localhost:8080/actuator
   ```

2. application.yml'de metriklerin enabled olduğundan emin ol:
   ```yaml
   management:
     endpoints:
       web:
         exposure:
           include: prometheus
     metrics:
       export:
         prometheus:
           enabled: true
   ```

### Problem 2: Prometheus servisi çalışmıyor

**Çözüm:**
```bash
# Docker logs kontrolü
docker-compose -f docker-compose.monitoring.yml logs prometheus

# Prometheus config dosyasını valide et
docker exec richbank-prometheus promtool check config /etc/prometheus/prometheus.yml
```

### Problem 3: High Memory Usage

**Çözüm:**
- Metrik sayısını azalt
- High cardinality tag'ları kaldır
- Garbage metrikleri temizle
```java
// Eski metrikleri temizle
meterRegistry.clear();
```

---

## Kaynaklar

- [Micrometer Documentation](https://micrometer.io/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Grafana Dashboard Guide](https://grafana.com/docs/grafana/latest/dashboards/)
- [AlertManager Setup](https://prometheus.io/docs/alerting/latest/overview/)

---

## İletişim & Destek

Sorularınız ve önerileri için:
- Backend Team: @backend-team
- Monitoring: @platform-team

