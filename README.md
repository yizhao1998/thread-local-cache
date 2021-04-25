# TLC (Thread Local Cache)

**TLC (Thread Local Cache)** is a general solution for caching time-consuming query results within thread.

Technical Details:
1. TLC is implemented based on Spring + Aspect.
2. TLC compares cache key by calling hashCode(), so make sure ID overrides hashCode() when ID is not a primitive type.
3. TLC clears thread local variable by intercepting servlet request, see: com.yi.thread.local.cache.interceptor.ThreadLocalCacheInterceptor

Usage Example:
```
@Service
public class TestService implements ThreadLocalCacheable<Integer, TestService.Supplier<Integer>> {

    @Override
    public Integer resolveKey(Supplier<Integer> sup) {
        System.out.println("resolve key" + GsonUtils.toJSONString(sup));
        return sup.getSupId();
    }

    @Override
    public List<Supplier<Integer>> cacheableGet(List<Integer> ids) {
        System.out.println("find key for " + GsonUtils.toJSONString(ids));
        List<Supplier<Integer>> results = new ArrayList<>();
        ...
        // detailed queryMethod()
        // ex: results = supplierService.querySuppliers(ids);
        ...
        return results;
    }

    ...
}
```
Test Example:
```
@RestController
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello() {
        List<Integer> keys = new ArrayList<>();
        keys.add(1);
        keys.add(2);
        List<TestService.Supplier<Integer>> strings = testService.cacheableGet(keys);
        System.out.println("result: " + GsonUtils.toJSONString(strings));
        keys = new ArrayList<>();
        keys.add(1);
        keys.add(3);
        strings = testService.cacheableGet(keys);
        System.out.println("result: " + GsonUtils.toJSONString(strings));
        return "hello";
    }
}
```
Output
```
find key for [1,2]
resolve key{"supId":1,"status":"NORMAL"}
resolve key{"supId":2,"status":"NORMAL"}
result: [{"supId":1,"status":"NORMAL"},{"supId":2,"status":"NORMAL"}]
find key for [3]
resolve key{"supId":3,"status":"NORMAL"}
result: [{"supId":1,"status":"NORMAL"},{"supId":3,"status":"NORMAL"}]
```
