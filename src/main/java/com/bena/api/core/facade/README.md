# AbstractFacade - دليل الاستخدام

## 📖 نظرة عامة
`AbstractFacade` هو كلاس أساسي يوفر جميع عمليات CRUD الأساسية لأي Entity. بدلاً من كتابة نفس الكود لكل Entity، فقط ورث من هذا الكلاس واستخدم العمليات الجاهزة!

## ✨ الميزات

### العمليات الأساسية:
- ✅ `create(entity)` - إنشاء كيان جديد
- ✅ `update(entity)` - تحديث كيان موجود
- ✅ `remove(entity)` - حذف كيان
- ✅ `find(id)` - البحث بالـ ID
- ✅ `findAll()` - جلب جميع الكيانات
- ✅ `count()` - عد الكيانات
- ✅ `exists(id)` - التحقق من وجود كيان

### عمليات متقدمة:
- ✅ `findRange(first, max)` - Pagination
- ✅ `removeById(id)` - حذف بالـ ID
- ✅ `removeAll()` - حذف الكل
- ✅ `findByQuery(jpql, params)` - استعلام مخصص
- ✅ `executeUpdate(jpql, params)` - تنفيذ تحديث

## 🚀 كيفية الاستخدام

### 1. إنشاء Facade جديد

```java
@Repository
public class ProductFacade extends AbstractFacade<Product> {
    
    public ProductFacade() {
        super(Product.class);
    }
    
    // يمكنك إضافة دوال مخصصة
    public List<Product> findByCategory(String category) {
        return findByQuery(
            "SELECT p FROM Product p WHERE p.category = ?1",
            category
        );
    }
}
```

### 2. استخدام الـ Facade في Controller

```java
@RestController
@RequestMapping("/v1/products")
public class ProductController {
    
    @Autowired
    private ProductFacade productFacade;
    
    // جلب جميع المنتجات
    @GetMapping
    public List<Product> getAll() {
        return productFacade.findAll();
    }
    
    // جلب منتج بالـ ID
    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productFacade.find(id);
    }
    
    // إنشاء منتج جديد
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productFacade.create(product);
    }
    
    // تحديث منتج
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productFacade.update(product);
    }
    
    // حذف منتج
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productFacade.removeById(id);
    }
    
    // عد المنتجات
    @GetMapping("/count")
    public long count() {
        return productFacade.count();
    }
}
```

### 3. استعلامات مخصصة

```java
// البحث
public List<Product> search(String keyword) {
    return findByQuery(
        "SELECT p FROM Product p WHERE p.name LIKE ?1",
        "%" + keyword + "%"
    );
}

// جلب بشرط معقد
public List<Product> findExpensiveProducts(double minPrice) {
    return findByQuery(
        "SELECT p FROM Product p WHERE p.price > ?1 ORDER BY p.price DESC",
        minPrice
    );
}

// تحديث مجموعة
public int updatePrices(double percentage) {
    return executeUpdate(
        "UPDATE Product p SET p.price = p.price * ?1",
        1 + (percentage / 100)
    );
}
```

### 4. Pagination

```java
@GetMapping("/page")
public List<Product> getPage(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    
    return productFacade.findRange(page * size, size);
}
```

## 📋 أمثلة واقعية

### مثال 1: User Facade
```java
@Repository
public class UserFacade extends AbstractFacade<User> {
    
    public UserFacade() {
        super(User.class);
    }
    
    public Optional<User> findByEmail(String email) {
        return findSingleByQuery(
            "SELECT u FROM User u WHERE u.email = ?1",
            email
        );
    }
    
    public List<User> findActiveUsers() {
        return findByQuery(
            "SELECT u FROM User u WHERE u.isActive = true"
        );
    }
}
```

### مثال 2: Order Facade
```java
@Repository
public class OrderFacade extends AbstractFacade<Order> {
    
    public OrderFacade() {
        super(Order.class);
    }
    
    public List<Order> findByUser(Long userId) {
        return findByQuery(
            "SELECT o FROM Order o WHERE o.user.id = ?1 ORDER BY o.createdAt DESC",
            userId
        );
    }
    
    public List<Order> findPending() {
        return findByQuery(
            "SELECT o FROM Order o WHERE o.status = 'PENDING'"
        );
    }
}
```

## 🎯 نصائح

1. **استخدم @Repository** على الـ Facade
2. **أضف @Transactional** للعمليات المعقدة
3. **استخدم JPQL** للاستعلامات المخصصة
4. **استفد من Pagination** للبيانات الكبيرة
5. **أضف دوال مخصصة** حسب احتياجاتك

## ⚡ الأداء

- ✅ استخدام JPA Criteria API للأداء الأفضل
- ✅ Lazy Loading للعلاقات
- ✅ Caching جاهز للاستخدام
- ✅ Batch Operations مدعومة

## 🔒 الأمان

- ✅ جميع العمليات داخل Transactions
- ✅ معالجة الأخطاء تلقائياً
- ✅ Validation جاهز

## 📚 المراجع

- [JPA Documentation](https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [JPQL Guide](https://www.objectdb.com/java/jpa/query/jpql)
