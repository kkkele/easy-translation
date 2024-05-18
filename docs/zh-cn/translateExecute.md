# 执行翻译

> 本文着重讲spring boot环境下的实现，core版本需要自行配置或者查阅相关文档

`EasyTranslation`的SpringBoot版本，使用`DefaultTransExecutorContext`来存储翻译器的相关信息。

使用`DefaultTranslationProcessor`来调用`DefaultTransExecutorContext`处理对象，翻译并填充字段。

框架分别为其配置了1个bean实例。

## 手动翻译

我们只需要注入DefaultTranslationProcessor，便可以进行一个提前的手动处理

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

## aop翻译

选择要翻译的方法，加上`@TranslationExecute`注解，则aop会自动处理返回值。

```java
    @Override
    @TranslationExecute
    public ProductVoV2 getDetailByIdV2(Integer id) {
       // ... 执行逻辑
    }
```



## 翻译属性选择

如果不指定`@TranslationExecute`的field字段，则会默认处理整个返回值，指定了field字段，则会获取返回值的field属性进行一个处理

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

## 自动拆包

当使用Aop进行翻译的时候，框架会对方法的返回结果判断结果是否可以拆包，如果可以拆包，则将结果拆包然后依次执行。

如果您希望处理非常复杂的拆包类型，这需要您手动实现`TranslationUnpackingHandler`接口

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

您可以参考DefaultTranslationTypeHandler自行实现其他复杂的拆包方式，像是嵌套List<List<Object>>，二维数组，链表结构的类，树结构的类等等

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

### 异步翻译

拆包后，支持异步翻译。只需要您将`TranslationExecute`设为true即可，不过它只是同时处理拆包后的每个对象，并不影响对象内字段的翻译顺序。
