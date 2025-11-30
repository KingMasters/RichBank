# Cache Mimarisi - RichBank

## 📋 İçindekiler
1. [Genel Bakış](#genel-bakış)
2. [Cache Katmanı Tasarımı](#cache-katmanı-tasarımı)
3. [Implementasyon](#implementasyon)
4. [Konfigürasyon](#konfigürasyon)
5. [Kullanım Örnekleri](#kullanım-örnekleri)
6. [İleri Seviye Özellikler](#ileri-seviye-özellikler)

---

## 🎯 Genel Bakış

RichBank projesinde **Hexagonal Architecture** (Altıgen Mimari) prensiplerine uyarak cache yapısı tasarlanmıştır. Bu tasarım ile:

- **Bağımsızlık**: Cache implementasyonu iş mantığından ayrıdır
- **Esneklik**: Cache provider'ı kolayca değiştirilebilir (Caffeine → Redis)
- **Testability**: Mock'lama ile unit test yazması kolaydır
- **Performans**: Tekrarlanan veritabanı sorgularından kaçınılır

### Cache Kullanım Alanı

Şu anda **Kategoriler** cache'lenmektedir. Gelecekte diğer varlıklar (Products, Customers, Orders) da eklenir.

---

## 🏗️ Cache Katmanı Tasarımı

### Mimari Diyagramı

```
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  (ListCategoriesService, CreateProductService, vb.)        │
└────────────────────┬────────────────────────────────────────┘
                     │ depends on
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                    Port Layer (Interface)                    │
│            CategoryCachePort (Input/Output Port)            │
│  - getAll()          - putAll()                            │
│  - getById()         - putById()                           │
│  - invalidateAll()   - invalidateById()                    │
└────────────────────┬────────────────────────────────────────┘
                     │ implements
                     ↓
┌─────────────────────────────────────────────────────────────┐
│                  Adapter Layer (Implementation)              │
│  ┌──────────────────────┐        ┌─────────────────────┐   │
│  │ CaffeineCategoryCache │        │  Redis Cache        │   │
│  │    Adapter (Active)  │        │  (Future)           │   │
│  └──────────────────────┘        └─────────────────────┘   │
│  ┌──────────────────────┐        ┌─────────────────────┐   │
│  │ In-Memory Cache      │        │  Distributed Cache  │   │
│  │ (Testing)            │        │  (Production)       │   │
│  └──────────────────────┘        └─────────────────────┘   │
└────────────────────┬────────────────────────────────────────┘
                     │ uses
                     ↓
┌─────────────────────────────────────────────────────────────┐
│              Infrastructure (Caffeine Library)               │
│         com.github.benmanes.caffeine.cache.Cache           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Implementasyon

### 1. **Port Layer** - `CategoryCachePort.java`

```java
public interface CategoryCachePort {
    // Tüm kategorileri işleyen operasyonlar
    Optional<List<Category>> getAll();
    void putAll(List<Category> categories);
    void invalidateAll();

    // Kategori-spesifik operasyonlar
    Optional<Category> getById(String categoryId);
    void putById(String categoryId, Category category);
    void invalidateById(String categoryId);
}
```

**Port Özellikleri:**
- ✅ Domain-agnostic: İş mantığından bağımsız
- ✅ Adapter-independent: Herhangi bir cache teknolojisiyle uyumlu
- ✅ Dual-level caching: Hem tam liste hem de item-by-item caching desteği

### 2. **Adapter Layer** - `CaffeineCategoryCacheAdapter.java`

```
╔════════════════════════════════════════════════════════════════╗
║                 CaffeineCategoryCacheAdapter                  ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  ┌─────────────────────────────────────────────────────────┐  ║
║  │ allCache: Cache<String, List<Category>>                 │  ║
║  │ Key: "categories:all"                                   │  ║
║  │ TTL: 300 saniye (config'den alınır)                     │  ║
║  │ Max Size: 1000                                          │  ║
║  └─────────────────────────────────────────────────────────┘  ║
║                                                                ║
║  ┌─────────────────────────────────────────────────────────┐  ║
║  │ byIdCache: Cache<String, Category>                      │  ║
║  │ Keys: "cat-id-1", "cat-id-2", ...                       │  ║
║  │ TTL: 300 saniye (config'den alınır)                     │  ║
║  │ Max Size: 2000 (allCache'in 2x'i)                       │  ║
║  └─────────────────────────────────────────────────────────┘  ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
```

#### Dual-Level Caching Stratejisi

**1. Full List Caching**
```
getAll() → Cache'de "categories:all" var mı?
           ├─ Evet: Döndür
           └─ Hayır: DB'den al → Cache'e koy
```

**2. Per-ID Caching**
```
getById(categoryId) → byIdCache'de var mı?
                      ├─ Evet: Döndür
                      └─ Hayır: 
                         ├─ allCache'de tam liste var mı?
                         │  ├─ Evet: Listede ara → byIdCache'e koy
                         │  └─ Hayır: DB'den al
                         └─ Döndür
```

**3. Senkronizasyon**
```
putAll(categories)
  → allCache'e tam liste koy
  → Tüm kategorileri byIdCache'e de koy

putById(categoryId, category)
  → byIdCache'e koy
  → allCache'de tam liste var mı?
     └─ Evet: Güncellenmiş listeyi allCache'e koy
```

#### Anahtar Özellikler

| Özellik | Açıklama |
|---------|----------|
| **Expire After Write** | Verilerin yazılmasından sonra belirtilen süre sonra otomatik olarak silinir |
| **Maximum Size** | Cache boyutu bu limite ulaştığında en eski veriler silinir (LRU eviction) |
| **Optional<T> Pattern** | Null-unsafe API. Cache miss durumunda `Optional.empty()` döndürülür |
| **Synchronous** | Thread-safe, ek konfigürasyon gerekmez |

---

## ⚙️ Konfigürasyon

### 1. **application.yml** - Application Properties

```yaml
cache:
  category:
    ttlSeconds: 300      # 5 dakika (saniye cinsinden)
    maxSize: 1000        # Maksimum cache boyutu
```

**TTL Değerleri:**
- `300 saniye` = 5 dakika (geliştirme ortamı için ideal)
- `3600 saniye` = 1 saat (üretim ortamı önerisi)
- `86400 saniye` = 1 gün (statik kategoriler için)

### 2. **CacheConfiguration.java** - Bean Oluşturma

```java
@Configuration
public class CacheConfiguration {

    @Bean
    public CategoryCachePort categoryCachePort(
            @Value("${cache.category.ttlSeconds:300}") long ttlSeconds,
            @Value("${cache.category.maxSize:1000}") long maxSize
    ) {
        return new CaffeineCategoryCacheAdapter(ttlSeconds, maxSize);
    }
}
```

**Bean Yaşam Döngüsü:**
1. Spring Application başlatıldığında
2. `@Value` anotasyonu ile `application.yml`'den değerler alınır
3. `CategoryCachePort` Bean olarak oluşturulur
4. Dependency Injection ile servisler tarafından kullanılır

### 3. **CacheConfig.java** - Spring Cache Manager (İleri)

Bu konfigürasyon gelecekteki Redis entegrasyonu için:

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", 
                          havingValue = "simple", 
                          matchIfMissing = true)
    public CacheManager inMemoryCacheManager() {
        // In-memory cache (şu anki kullanım)
        return new ConcurrentMapCacheManager(
            "products", "categories", "orders", ...
        );
    }

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", 
                          havingValue = "redis")
    public CacheManager redisCacheManager(
            RedisConnectionFactory connectionFactory) {
        // Redis cache (gelecekte)
        return RedisCacheManager.builder(connectionFactory)
            .withCacheConfiguration("categories", 
                config.entryTtl(Duration.ofDays(1)))
            .build();
    }
}
```

**Redis Aktivasyonu:**
```yaml
# application.yml'ye eklenecek
spring:
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
```

---

## 💡 Kullanım Örnekleri

### Örnek 1: Query Service - Kategori Listeleme

```java
@UseCase
public class ListCategoriesService implements ListCategoriesUseCase {
    private final CategoryCachePort categoryCache;
    private final CategoryRepositoryPort categoryRepository;

    @Override
    public List<Category> execute() {
        // 1. Cache'den al
        return categoryCache.getAll()
                // 2. Cache miss: DB'den al
                .orElseGet(() -> {
                    List<Category> categories = 
                        categoryRepository.findAll();
                    // 3. Cache'e koy
                    categoryCache.putAll(categories);
                    return categories;
                });
    }
}
```

**Akış:**
```
1. İlk istek
   → Cache miss
   → DB sorgusu: SELECT * FROM categories
   → 1000ms
   
2. 5 dakika içinde yapılan istek
   → Cache hit
   → Bellek erişimi: ~1ms
   → 1000x daha hızlı!
   
3. 5 dakika sonra yapılan istek
   → Cache expire
   → DB sorgusu tekrar yapılır
```

### Örnek 2: Command Service - Kategori Oluşturma

```java
@UseCase
public class CreateCategoryService 
        implements CreateCategoryUseCase {
    private final CategoryRepositoryPort repository;
    private final CategoryCachePort cache;

    @Override
    public Category execute(CreateCategoryCommand cmd) {
        // 1. DB'ye kaydet
        Category created = repository.save(new Category(...));
        
        // 2. Cache'i invalidate et (yeni kategori eklendi)
        cache.invalidateAll();
        
        // 3. Yeni kategoriyi cache'e koy
        cache.putById(created.getId().toString(), created);
        
        return created;
    }
}
```

**Cache Invalidation Stratejisi:**
- ✅ **Write-Through**: Yazma işlemi cache'i güncellerken
- ✅ **Full Invalidation**: Tam liste cache'i temizlenir (yeni sorgu gerekli)
- ✅ **Selective Caching**: Yeni kategori hemen cache'e eklenir

### Örnek 3: Test - In-Memory Cache Adapter

```java
public class CategoryCacheIntegrationTest {
    
    private CategoryRepositoryPort categoryRepository;
    private CategoryCachePort categoryCache;
    private ListCategoriesService service;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepositoryPort.class);
        // Test için hafif in-memory adapter
        categoryCache = new InMemoryCategoryCacheAdapter();
        service = new ListCategoriesService(
            categoryCache, categoryRepository);
    }

    @Test
    void cacheShouldBePopulatedAndUsedOnSecondCall() {
        Category c1 = Category.create("cat1");
        List<Category> categories = List.of(c1);
        
        when(categoryRepository.findAll())
            .thenReturn(categories);

        // İlk çağrı: Repository çağrılır
        service.execute();
        verify(categoryRepository, times(1)).findAll();

        // İkinci çağrı: Cache'den alınır, 
        // Repository çağrılmaz
        service.execute();
        verify(categoryRepository, times(1)).findAll();
    }
}
```

---

## 🚀 İleri Seviye Özellikler

### 1. Cache Hit/Miss Metrikleri

Prometheus metrics entegrasyon:

```yaml
# prometheus.yml
- job_name: 'richbank'
  static_configs:
    - targets: ['localhost:8080']
  metrics_path: '/actuator/prometheus'
```

Cache performansı izleme:
```
application_cache_hits_total
application_cache_misses_total
application_cache_evictions_total
application_cache_size_bytes
```

### 2. Distributed Caching (Redis)

Gelecekte birden çok instance için:

```java
// Redis konfigürasyonu
@Bean
public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory();
}

@Bean
public RedisCacheManager cacheManager(
        LettuceConnectionFactory connectionFactory) {
    return RedisCacheManager.create(connectionFactory);
}
```

**Avantajları:**
- 🔄 Instance'lar arası veri paylaşımı
- 📊 Merkezi cache yönetimi
- 🔒 Persistence (veriler kalıcı)

### 3. Warm-up Caching

Uygulama başlangıçında cache'i önceden doldurma:

```java
@Component
public class CacheWarmer {
    
    @EventListener(ApplicationReadyEvent.class)
    public void warmCache() {
        logger.info("Warming up category cache...");
        List<Category> categories = 
            categoryRepository.findAll();
        categoryCache.putAll(categories);
    }
}
```

### 4. Cache Statistics

Cache kullanım istatistikleri:

```java
@RestController
@RequestMapping("/admin/cache")
public class CacheStatsController {
    
    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        return Map.of(
            "allCacheSize", allCache.estimatedSize(),
            "byIdCacheSize", byIdCache.estimatedSize(),
            "lastUpdateTime", lastUpdateTime
        );
    }
    
    @PostMapping("/invalidate")
    public String invalidateCache() {
        categoryCache.invalidateAll();
        return "Cache invalidated";
    }
}
```

---

## 📊 Performans Karşılaştırması

| Senaryo | Süre | Fark | Açıklama |
|--------|------|------|---------|
| **Cache Miss** | ~100ms | Baseline | DB'den sorgu |
| **Cache Hit** | ~1ms | 100x hızlı | Bellek erişimi |
| **Warm Cache (100 kategori)** | ~50ms | 2x hızlı | Önceden doldurulmuş |

**Örnek: 10.000 istek/gün**
- Tüm DB'den: 10.000 × 100ms = **1.000 saniye**
- Cache ile: (1 × 100ms) + (9.999 × 1ms) = **~10 saniye**
- **Tasarruf: 99% hızlanma!** 🎉

---

## 🔍 Troubleshooting

### Problem: Cache'de veri güncel olmıyor

**Çözüm:**
```java
// Cache'i manuel olarak invalidate et
categoryCache.invalidateAll();
```

### Problem: TTL çok kısa, veriler çok hızlı silinüyor

**Çözüm:**
```yaml
cache:
  category:
    ttlSeconds: 3600  # 1 saat olarak ayarla
```

### Problem: Cache boyutu aşıldı, performans düşüyor

**Çözüm:**
```yaml
cache:
  category:
    maxSize: 5000  # Limit artır
```

---

## ✅ Checklist

- [x] `CategoryCachePort` interface tanımlandı
- [x] `CaffeineCategoryCacheAdapter` implementasyonu yapıldı
- [x] Spring Bean konfigürasyonu (`CacheConfiguration.java`)
- [x] `application.yml` konfigürasyonu
- [x] Unit tests (`CategoryCacheIntegrationTest.java`)
- [x] Dual-level caching stratejisi
- [x] Query/Command service entegrasyonu
- [ ] Redis adapter (gelecekte)
- [ ] Metrics dashboard (gelecekte)
- [ ] Distributed caching (gelecekte)

---

## 📚 İlgili Dosyalar

| Dosya | Amaç |
|------|------|
| `application/src/main/resources/application.yml` | Cache konfigürasyonu |
| `infra/src/main/java/.../cache/CaffeineCategoryCacheAdapter.java` | Caffeine adapter |
| `infra/src/main/java/.../config/CacheConfiguration.java` | Spring Bean config |
| `infra/src/main/java/.../config/CacheConfig.java` | Advanced cache config |
| `application/src/main/java/.../port/out/CategoryCachePort.java` | Cache port interface |
| `application/src/main/java/.../service/query/.../ListCategoriesService.java` | Cache kullanım örneği |
| `application/src/test/java/.../cache/CategoryCacheIntegrationTest.java` | Cache testleri |

---

**Yazılı:** November 2025  
**Mimarı:** Hexagonal Architecture Principles  
**Teknoloji Stack:** Caffeine Cache + Spring Boot

