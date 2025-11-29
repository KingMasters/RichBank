# RichBank Metrics & Monitoring Module

Micrometer ve Prometheus kullanarak uygulamada çalışan metrikleri toplayan, Hexagonal Architecture'a uygun bir monitoring çözümü.

## 📊 Özellikler

✅ **Otomatik Metrik Toplama**: AOP ile service metotlarının otomatik olarak metrikleri toplama
✅ **Prometheus Compatible**: Prometheus tarafından scrape edilebilir format
✅ **Hexagonal Architecture**: Clean Architecture ilkelerine uygun port-adapter pattern
✅ **Comprehensive Metrics**: Service performance, cache, database, business events
✅ **Alerting Support**: Prometheus Alert Rules ve AlertManager entegrasyonu
✅ **Grafana Integration**: Önceden hazırlanmış dashboard örnekleri
✅ **Easy Integration**: MetricsService ile basit kullanım arayüzü

## 🏗️ Mimari

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                         │
│                 (com.hexagonal.application)                 │
├─────────────────────────────────────────────────────────────┤
│  MetricsPort (Interface)                                    │
│  - recordServiceExecutionTime()                             │
│  - recordServiceSuccess/Failure()                           │
│  - recordBusinessMetric()                                   │
└──────────────┬──────────────────────────────────────────────┘
               │ implements
┌──────────────▼──────────────────────────────────────────────┐
│                    Framework Layer                           │
│            (com.hexagonal.framework.crosscutting.metric)    │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  PrometheusMetricsAdapter (implements MetricsPort)  │   │
│  └────────────────────┬────────────────────────────────┘   │
│                       │ uses                                │
│  ┌────────────────────▼────────────────────────────────┐   │
│  │  MetricsRegistry                                     │   │
│  │  (Counter, Timer, Gauge management)                │   │
│  └────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌────────────────────────────────────────────────────┐   │
│  │  MetricsCollectorAspect (@Around advice)           │   │
│  │  (Automatic metrics collection via AOP)            │   │
│  └────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌────────────────────────────────────────────────────┐   │
│  │  MetricsService (Utility component)                │   │
│  │  (Easy-to-use business metrics API)                │   │
│  └────────────────────────────────────────────────────┘   │
│                                                              │
│  ┌────────────────────────────────────────────────────┐   │
│  │  MetricsConfiguration (Spring Configuration)       │   │
│  │  (Common tags, environment setup)                  │   │
│  └────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
               │ exposes
               ▼
┌──────────────────────────────────────────────────────────────┐
│  Prometheus                                                  │
│  - scrapes /actuator/prometheus endpoint                    │
│  - stores time-series data                                  │
│  - evaluates alert rules                                    │
└──────────────────────────────────────────────────────────────┘
               │
       ┌───────┴───────┬──────────────┐
       ▼               ▼              ▼
   Grafana        AlertManager    External Systems
   (Dashboards)   (Notifications) (Slack, etc.)
```

## 📂 Dosya Yapısı

```
richbank/
├── framework/
│   ├── src/main/java/com/hexagonal/framework/crosscutting/metric/
│   │   ├── MetricsRegistry.java              # Merkezi metrik yönetimi
│   │   ├── PrometheusMetricsAdapter.java     # Prometheus uygulaması
│   │   ├── MetricsCollectorAspect.java       # AOP aspect
│   │   └── MetricsService.java               # Business metrics utility
│   ├── src/test/java/.../metric/
│   │   ├── MetricsRegistryTest.java          # Unit tests
│   │   ├── PrometheusMetricsAdapterTest.java # Unit tests
│   │   ├── MetricsCollectorAspectTest.java   # Unit tests
│   │   ├── MetricsServiceTest.java           # Unit tests
│   │   └── MetricsIntegrationTest.java       # Integration tests
│   └── src/main/java/.../config/
│       └── MetricsConfiguration.java         # Spring config
│
├── application/
│   ├── src/main/java/com/hexagonal/application/port/out/
│   │   └── MetricsPort.java                  # Port definition
│   └── src/main/resources/
│       └── application.yml                   # Actuator config
│
├── monitoring/
│   ├── prometheus.yml                        # Prometheus konfigürasyonu
│   ├── prometheus-rules.yml                  # Alert rules
│   ├── alertmanager.yml                      # AlertManager konfigürasyonu
│   ├── test-metrics-api.http                 # Test API istekleri
│   └── grafana/
│       └── provisioning/                     # Grafana dashboards (opsiyonel)
│
├── docker-compose.monitoring.yml             # Monitoring stack
├── METRICS_SETUP.md                          # Teknik setup dokümantasyonu
├── METRICS_IMPLEMENTATION_GUIDE.md           # Kullanım rehberi
└── pom.xml                                   # Maven bağımlılıkları
```

## 🚀 Hızlı Başlangıç

### 1. Bağımlılıkları İndir

```bash
cd /Users/cemilcetin/Documents/Dev/BackendGuru/Code/Java/richbank
./mvnw clean install
```

### 2. Uygulamayı Başlat

```bash
./mvnw spring-boot:run
```

### 3. Metrikleri Test Et

```bash
# Prometheus formatında metrikleri görüntüle
curl http://localhost:8080/actuator/prometheus

# Tüm metrikleri listele
curl http://localhost:8080/actuator/metrics
```

### 4. Monitoring Stack'i Başlat (Optional)

```bash
docker-compose -f docker-compose.monitoring.yml up -d

# Açılan servisler:
# - Prometheus: http://localhost:9090
# - Grafana: http://localhost:3000 (admin/admin)
# - AlertManager: http://localhost:9093
```

## 📊 Toplanan Metrikler

### Service Metrics
```
application.service.execution.time       (Timer/Histogram)
application.service.invocation.count      (Counter)
application.service.success.count         (Counter)
application.service.failure.count         (Counter)
```

### Business Metrics
```
application.business.order.created        (Counter)
application.business.cache.*              (Counter)
application.business.database.*           (Timer)
application.business.errors               (Counter)
```

### Common Tags
```
- service: Service adı
- method: Metot adı
- error_type: Error türü
- entity: Database entity
- cache_type: Cache türü
- status: HTTP status / operation status
```

## 💡 Kullanım Örnekleri

### Otomatik Metrik Toplama (AOP)

```java
@Service
public class LoginUseService implements LoginUseCase {
    @Override
    public AuthenticationResponse login(LoginRequest request) {
        // Otomatik olarak metriklendi
        return new AuthenticationResponse(...);
    }
}
```

### Custom Business Metrics

```java
@Service
public class OrderService {
    private final MetricsService metricsService;
    
    public void createOrder(Order order) {
        // Business event
        metricsService.recordBusinessEvent("order.created", 
            "status", "confirmed");
        
        // Cache operation
        metricsService.recordCacheOperation("category_cache", "hit");
        
        // Database operation
        metricsService.recordDatabaseOperation("insert", "Order", 45);
    }
}
```

### Timed Operation

```java
metricsService.executeWithTiming("PaymentService", "processPayment", () -> {
    // İşlem yapılır, metrikler otomatik kaydedilir
    processPaymentLogic();
});
```

## 🔍 Prometheus Queries

```promql
# Success Rate (son 5 dakika)
rate(application_service_success_count_total[5m]) / 
rate(application_service_invocation_count_total[5m])

# P95 Response Time
histogram_quantile(0.95, 
    rate(application_service_execution_time_seconds_bucket[5m]))

# Error Rate
rate(application_service_failure_count_total[5m])

# Business Events (Order Creation Rate)
rate(application_business_order_created_total[5m])
```

## 🛡️ Alerting

Prometheus alert rules otomatik olarak tetiklenir:

- **HighErrorRate**: Error rate > 5% for 5 minutes
- **SlowServiceExecution**: P95 response time > 2 seconds
- **HighCacheMissRate**: Cache miss rate > 30%
- **DatabaseOperationTimeout**: DB operations > 5 seconds
- **ServiceNotInvoking**: No invocations for 10 minutes

Alertler AlertManager üzerinden Slack, PagerDuty, Email vb. kanallara gönderilir.

## 📈 Grafana Dashboards

Grafana'da önceden hazırlanmış dashboard template'leri kullanılabilir:
- Service Health Dashboard
- Performance Metrics Dashboard
- Business Metrics Dashboard
- Infrastructure Dashboard

## 🧪 Test

```bash
# Unit tests
./mvnw test -Dtest=Metrics*Test

# Integration tests
./mvnw test -Dtest=MetricsIntegrationTest

# Tüm testler
./mvnw test
```

## 🔧 Configuration

**application.yml** dosyasında metrikleri konfigüre et:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        application.service.execution.time: true
      slo:
        application.service.execution.time: 50ms,100ms,200ms,500ms,1s,2s,5s
```

## ⚙️ Sistem Gereksinimleri

- **Java**: 21+
- **Spring Boot**: 3.1+
- **Micrometer**: 1.12+
- **Maven**: 3.8+
- **Docker**: (Monitoring stack için opsiyonel)

## 🐛 Troubleshooting

### Metrikleri görmüyorum

1. Actuator endpoint'ini kontrol et: `curl http://localhost:8080/actuator`
2. `management.endpoints.web.exposure.include` konfigürasyonunu kontrol et
3. Service sınıfları `com.hexagonal.application.service` paketinde olmalı

### Prometheus connection refused

1. Prometheus'un çalışıp çalışmadığını kontrol et: `http://localhost:9090`
2. Application URL'i prometheus.yml'de doğru mı kontrol et
3. Firewall rules kontrol et

### High memory usage

1. Metrik sayısını azalt
2. High cardinality tags'i kaldır (user_id, session_id, etc.)
3. Prometheus data retention'ını kısalt

## 📚 Dokumentasyon

- **[METRICS_SETUP.md](METRICS_SETUP.md)**: Teknik setup ve detaylı mimari
- **[METRICS_IMPLEMENTATION_GUIDE.md](METRICS_IMPLEMENTATION_GUIDE.md)**: Kullanım örnekleri ve best practices

## 🤝 Katkıda Bulunma

Backend team tarafından yönetilmektedir. Sorular ve öneriler için iletişime geçin.

## 📝 Lisans

RichBank Project - Internal Use Only

---

**Daha fazla bilgi için**: `./METRICS_IMPLEMENTATION_GUIDE.md` dosyasını okuyun

