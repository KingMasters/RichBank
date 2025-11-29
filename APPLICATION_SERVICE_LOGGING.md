# Application Service Logging Architecture

## Genel Bakış

Application katmanındaki tüm service'ler (execute() metodu çağırıldığında) otomatik olarak loglanır. AOP (Aspect-Oriented Programming) kullanılarak çok az kodla temiz logging sağlanır.

## Mimarisi

### 1. LoggingPort (application/port/out)
```java
public interface LoggingPort {
    void logRequest(String serviceName, String methodName, Object[] args);
    void logResponse(String serviceName, String methodName, Object result, long executionTimeMs);
    void logError(String serviceName, String methodName, Throwable error);
}
```

### 2. ApplicationServiceLogger (framework/logging)
- `LoggingPort` implementasyonu
- SLF4J kullanarak logging yapar
- Request/Response mesajlarını formatlar

### 3. ApplicationServiceLoggingAspect (framework/logging)
- Spring AOP Aspect
- `@UseCase` annotasyonlu service'lerin `execute()` metodlarını otomatik yakalar
- Request öncesi, response sonrası ve error durumunda log yazar
- Yürütme zamanını ölçer

## Log Format

```
→ [REQUEST] Service: CreateCategoryService | Method: execute | Args: [CreateCategoryCommand]
← [RESPONSE] Service: CreateCategoryService | Method: execute | Result: Category | ExecutionTime: 45ms
✗ [ERROR] Service: CreateCategoryService | Method: execute | Error: Duplicate category
```

## Konfigürasyon (application.yml)

```yaml
logging:
  level:
    APPLICATION_SERVICE: INFO  # Application service logger seviyesi
    com.hexagonal: DEBUG        # Tüm hexagonal paketleri
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## Nasıl Çalışır?

1. Service `@UseCase` ile işaretlenmiştir
2. Service'in `execute()` metodu çağrıldığında, AOP Aspect otomatik olarak:
   - Request mesajı loglar (giriş parametreleri ile)
   - Metodun yürütülmesini sağlar
   - Response mesajı loglar (sonuç ve yürütme zamanı ile)
   - Eğer hata varsa error mesajı loglar

## Örnek: CreateCategoryService

```java
@UseCase
public class CreateCategoryService implements CreateCategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final CategoryCachePort categoryCache;

    public CreateCategoryService(CategoryRepositoryPort categoryRepository, 
                                 CategoryCachePort categoryCache) {
        this.categoryRepository = categoryRepository;
        this.categoryCache = categoryCache;
    }

    @Override
    public Category execute(CreateCategoryCommand command) {
        // Otomatik log yapılır:
        // → [REQUEST] Service: CreateCategoryService | Method: execute | Args: [CreateCategoryCommand]
        
        Category category = Category.create(command.getName());
        category.updateDescription(command.getDescription());
        if (command.getParentCategoryId() != null) {
            category.setParentCategory(ID.of(command.getParentCategoryId()));
        }
        Category saved = categoryRepository.save(category);
        categoryCache.putById(saved.getId().toString(), saved);
        
        // Otomatik log yapılır:
        // ← [RESPONSE] Service: CreateCategoryService | Method: execute | Result: Category | ExecutionTime: 42ms
        
        return saved;
    }
}
```

## Örnek: ListCategoriesService

```java
@UseCase
public class ListCategoriesService implements ListCategoriesUseCase {
    // ...existing code...

    @Override
    public List<Category> execute() {
        // Otomatik log yapılır:
        // → [REQUEST] Service: ListCategoriesService | Method: execute | Args: []
        
        return categoryCache.getAll()
                .orElseGet(() -> {
                    List<Category> categories = categoryRepository.findAll();
                    categoryCache.putAll(categories);
                    return categories;
                });
        
        // Otomatik log yapılır:
        // ← [RESPONSE] Service: ListCategoriesService | Method: execute | Result: List | ExecutionTime: 12ms
    }
}
```

## Avantajları

✅ **Temiz**: Service kodu logging ile kirlenmiyor  
✅ **Otomatik**: @UseCase olan tüm service'ler otomatik loglanır  
✅ **Performans**: AOP minimal overhead ekler  
✅ **Flexible**: Farklı logging implementasyonları ekleme kolay (LoggingPort interface sadece)  
✅ **Testable**: Port interface olduğu için mock'lanabilir  
✅ **Centralized**: Tüm logging konfigürasyonu bir yerde (application.yml)

## Yeni Service Eklemek

Yeni bir Application Service oluşturduğunuzda, otomatik logging almak için sadece:

1. `@UseCase` annotation'ı ekleyin
2. `execute()` metodunu implement edin
3. Hepsi! Logging otomatik olacak.

```java
@UseCase
public class MyNewService implements MyUseCase {
    @Override
    public void execute(MyCommand command) {
        // Automatically logged
    }
}
```

## Production Ready

- SLF4J kullanıldığı için Logback, Log4j vb. kütüphaneler ile uyumlu
- Log levels (INFO, DEBUG, ERROR) kontrolü kolay
- Pattern konfigürasyonu ile format özelleştirilebilir
- Performans optimizasyonları eklenebilir (filtering, sampling vb.)

