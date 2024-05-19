# Executive translation

> This article focuses on the implementation in the spring boot environment, and the core version needs to be configured by itself or refer to the relevant documentation

` EasyTranslation ` SpringBoot version, using ` DefaultTransExecutorContext ` to store the translator of the relevant information.

Using ` DefaultTranslationProcessor ` to invoke ` DefaultTransExecutorContext ` processing object, translation and populate the fields.

The framework has configured one bean instance for each.

## Manual translation

We only need to inject DefaultTranslationProcessor, can carry out a manual processing of ahead of time.

```java
@Autowired
private  DefaultTranslationProcessor defaultTranslationProcessor;

    @GetMapping("/{id}")
    public R<TestVo> getById(@PathVariable Integer id){
        TestVo testVo = new TestVo();
        testVo.setId(id);
        defaultTranslationProcessor.process(testVo); //手动翻译
        return R.ok(testVo);
    }

```

## aop translation

Select the method to be translated with the `@TranslationExecute` annotation, and aop automatically handles the return value.

```java
    @Override
    @TranslationExecute
    public ProductVoV2 getDetailByIdV2(Integer id) {
       // ... 执行逻辑
    }
```



## Translation attribute selection

If the field field of `@TranslationExecute` is not specified, the entire return value is processed by default. If the field field is specified, the field attribute of the returned value is obtained for a processing.

```java
    @GetMapping("/test")
    @TranslationExecute(field = "data")
    public R<UserVO> test(){
        UserVO userVO = new UserVO();
        userVO.setUserId(1);
        userVO.setUserName("小红");
        return R.ok(userVO);
    }
```

```java
public class R<T> {

    int code;

    T data;
   // ... 省略其他函数
}
```

## Automatic unpacking

When using Aop for translation, the framework determines whether the result of a method can be unpacked, and if it can be unpacked, the results are unpacked and then executed in sequence.

If you want to deal with complicated unpacking type, this requires you to manually implement ` TranslationUnpackingHandler ` interface

```java
/**
 * 参数解包处理器
 * 用来处理List<List<> Object[] Map<>...等复杂结构
 */
public interface TranslationUnpackingHandler {

    /**
     * 解包List
     */
    List<BeanDescription> unpackingCollection(Collection collection, Class<?> clazz);

    /**
     * 解包map
     */
    List<BeanDescription> unpackingMap(Map map, Class<?> clazz);

    /**
     * 解包array
     */
    List<BeanDescription> unpackingArray(Object[] array, Class<?> clazz);

    /**
     * 解包其他类型
     */
    List<BeanDescription> unpackingOther(Object object, Class<?> clazz);

    /**
     * 解析是否需要解包
     * @param obj 解析的参数
     * @return 0：不需要解包
     * 1：需要调用unpackingCollection方法解包
     * 2: 需要调用unpackingMap方法解包
     * 3: 需要调用unpackingArray方法解包
     * 4: 需要调用unpackingOther方法解包
     */
    int unpackingType(Object obj);
}

```

When using Aop for translation, the framework determines whether the result of a method can be unpacked, and if it can be unpacked, the results are unpacked and then executed in sequence.
You can refer to `DefaultTranslationTypeHandler` to implement other complex way of unpacking, like nested List < List < Object > >, two-dimensional arrays, chain table structure, tree structure of classes and so on
If you want to deal with complicated unpacking type, this requires you to manually implement ` TranslationUnpackingHandler ` interface

```java

public class DefaultTranslationTypeHandler implements TranslationUnpackingHandler {

    @Override
    public List<BeanDescription> unpackingCollection(Collection collection, Class<?> clazz) {
        return (List<BeanDescription>) collection.stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }


    @Override
    public List<BeanDescription> unpackingMap(Map map, Class<?> clazz) {
        return (List<BeanDescription>) map.values().stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public List<BeanDescription> unpackingArray(Object[] array, Class<?> clazz) {
        return Arrays.stream(array)
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public List<BeanDescription> unpackingOther(Object object, Class<?> clazz) {
        return Collections.emptyList();
    }

    @Override
    public int unpackingType(Object parsingObj) {
        if (parsingObj instanceof Collection) {
            return 1;
        }else if (parsingObj instanceof Map){
            return 2;
        }else if (parsingObj instanceof Object[]){
            return 3;
        }
        return 0;
    }
}
```

### Asynchronous Translation

After unpacking, asynchronous translation is supported. You only need to set `TranslationExecute's async` to true, but it only handles every unpacked object at the same time, and does not affect the translation order of the fields in the object.
