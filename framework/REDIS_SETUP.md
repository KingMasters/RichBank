# Redis Cache Setup Guide

Bu dokümantasyon, projeye Redis cache'i nasıl ekleyeceğinizi açıklar.

## Mevcut Durum

Proje şu anda **Spring Boot Built-In In-Memory Cache** kullanıyor. Bu, `ConcurrentMapCacheManager` ile sağlanıyor ve herhangi bir ek kurulum gerektirmiyor.

## Redis'e Geçiş

### 1. Redis Server Kurulumu

#### Docker ile (Önerilen)
```bash
docker run -d -p 6379:6379 --name redis-cache redis:latest
```

#### Local Kurulum
- macOS: `brew install redis` ve `brew services start redis`
- Linux: `sudo apt-get install redis-server` ve `sudo systemctl start redis`
- Windows: [Redis for Windows](https://github.com/microsoftarchive/redis/releases) indirin

### 2. application.yaml Güncellemesi

`framework/src/main/resources/application.yaml` dosyasında şu değişiklikleri yapın:

```yaml
spring:
  cache:
    type: redis  # 'simple' yerine 'redis' olarak değiştirin
  
  data:
    redis:
      host: localhost
      port: 6379
      password:  # Şifre varsa buraya yazın
      timeout: 2000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
```

### 3. Cache Configuration

`CacheConfig.java` dosyası zaten Redis desteği içeriyor. Sadece `spring.cache.type=redis` ayarlandığında otomatik olarak Redis cache manager kullanılacak.

### 4. Test

Redis'in çalıştığını kontrol edin:
```bash
redis-cli ping
# PONG yanıtı almalısınız
```

### 5. Uygulamayı Başlatma

Uygulamayı başlattığınızda, loglarda şunu göreceksiniz:
```
Using Redis cache manager
```

## Cache Kullanımı

Cache annotation'ları zaten repository adapter'larında kullanılıyor:

- `@Cacheable`: Cache'den okuma
- `@CacheEvict`: Cache'i temizleme

Örnek:
```java
@Cacheable(value = "products", key = "#id.value.toString()")
public Optional<Product> findById(ID id) {
    // ...
}

@CacheEvict(value = "products", allEntries = true)
public Product save(Product product) {
    // ...
}
```

## Cache İsimleri

Projede kullanılan cache isimleri:
- `products`
- `customers`
- `categories`
- `orders`
- `carts`

## Redis Monitoring

Redis'i izlemek için:
```bash
redis-cli monitor
```

## Production Notları

Production ortamında:
1. Redis için password authentication kullanın
2. Redis cluster veya sentinel kullanmayı düşünün (high availability için)
3. Memory limit ayarlarını yapın (`maxmemory` ve `maxmemory-policy`)
4. Persistence ayarlarını yapın (RDB veya AOF)

## Geri Dönüş (Fallback)

Eğer Redis kullanılamazsa ve `spring.cache.type=simple` olarak ayarlanırsa, otomatik olarak in-memory cache kullanılacaktır.

