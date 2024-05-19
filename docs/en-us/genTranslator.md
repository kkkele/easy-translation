# Generate a translator

> This page shows how to use SpringBoot. In a non-SpringBoot environment, please see other documentation

## Core annotation

All methods or enumerations registered as translators must use the `@Translation` annotation or **a combination annotation with the annotation marked**

```java
/**
 * 用来标记成为翻译器的静态方法，动态方法，枚举类
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translation {

    /**
     * <p>转换器名称</p>
     * <p>translator name</p>
     */
    String name() default "";

}

```

If you are using the <font size=4>**spring-boot**</font> version, then the framework has provided you with a combined annotation `@Translator` to avoid having to spell the 'name' when using the annotation.

You can also customize other combined annotations, as long as you mark `@Translation`. (The following only says `@Translation`, but please don't ignore its combination note)

## Method translator

### Object Dynamic Translator

`EasyTranslation` automatically loads a dynamic method labeled by `@Translation` under a package that can be scanned, and registers it in a container if its declared class is also a Bean object.

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Translation(name = "getUser")
    @Cacheable(cacheNames = "user", key = "#id")
    public SysUser getById(Integer id) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setUsername("username" + DateUtil.date());
        sysUser.setNickName("nickName" + DateUtil.date());
        return sysUser;
    }
}
```

**Note** that dynamic methods of the class that became the Bean must be loaded automatically.

### StaticTranslator

`EasyTranslation` automatically loads static methods labeled by `@Translation` under packages that can be scanned and registers them in the container.

```java
    @Translation(name = "getTime")
    public static Date getDate() {
        return DateUtil.date();
    }
```

## Multi params translation

You may have noticed above that in the example referenced in [Static Translator](#StaticTranslator), the method does not take any parameters.

This is the power of `EasyTranslation`, which allows you to convert methods of arbitrary parameters into translators and provides extremely flexible judgment criteria.

First of all, remember two concepts, one is mapper, one is orher

### mapper

The `mapper` property converts your mapper string into other properties of the object.

```java
private Integer id;

@Mapping(translator = "getStrV2", mapper = "id",other = "1")
private String user0;
```

### other

The `other` attribute is a secondary judgment condition, which does not perform any operation, passing the original as a method argument to the translator.

For example, if the other of the above code is "1", it will pass the value "1" to the translator.

### @TransMapper

Method arguments marked with this annotation will become `mapper` fields.

**It should be noted** that for easy development, when your method parameter does not add any `@TransMapper` field and the number of parameters is greater than 0, the framework will automatically mark the first parameter as a `mapper` field.

So, in the previous example, without the '@TransMapper' annotation, you can still do a translation fill through **mapper**.

**[Important]** The attribute values corresponding to mapper are passed into the `@TransMapper` marked arguments in order, and then other will fill in the remaining method arguments in order.

For some example

```java
    @Translation(name = "getStrV2")
    public String getByCondition(Integer id, String other) {
        return StrUtil.join(",",id,other);
    }

    @Translation(name = "getStrV3")
    public String getByCondition(Integer id, Integer other, String other2) {
        return StrUtil.join(",",id,other,other2);
    }

    @Translation(name = "getStrV4")
    public String getByCondition(String other, @TransMapper Integer id, Boolean other2) {
        return StrUtil.join(",",id,other,other2);
    }
```

The above code lists the use cases of three translators, and the corresponding `@Mapping` is respectively written

```java
@Data
public class TestVo {

    private Integer id;

    @Mapping(translator = "getStrV2", mapper = "id",other = "1")
    private String user0;

    @Mapping(translator = "getStrV3", mapper = "id",other = {"1","2"})
    private String user1;

    @Mapping(translator = "getStrV4", mapper = "id",other = {"1","false"})
    private String user2;



}

```

controller

```java
@RestController
@RequestMapping("/sysUser")
@RequiredArgsConstructor
public class SysUserController {

    @GetMapping("/{id}")
    @TranslationExecute(field = "data")
    public R<TestVo> getById(@PathVariable Integer id){
        TestVo testVo = new TestVo();
        testVo.setId(id);
        return R.ok(testVo);
    }
}
```

result

```http
GET http://localhost:8080/sysUser/200

{
  "code": 200,
  "data": {
    "id": 200,
    "user0": "200,1",
    "user1": "200,1,2",
    "user2": "200,1,false"
  }
}
```



However, if you directly follow the above code, the project may not start successfully, and you need to do some additional configuration first.

## Application startup failure? Please expand the translator type

Let's start by implementing an `interface` where you need a few arguments, and the method writes a few. To avoid the hassle, override the Translator's doTranslate method and call your own method.

The parameters and returns of the method are set to type Object.

```java
public interface ThreeParamTranslator extends Translator {

    Object translate(Object var0, Object var1, Object var2);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2]);
    }
}

```

Then, we need to implement a ` TranslationAutoConfigurationCustomizer ` Bean, to register the config in the Bean will be registered in this new type of translator.

```java
@Bean
public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
    return config -> {
        config.registerTranslatorClazz(ThreeParamTranslator.class);
    };
}
```

Then, we need to implement a ` TranslationAutoConfigurationCustomizer ` Bean, to register the config in the Bean will be registered in this new type of translator. In this way, our three-parameter method is processed into the corresponding three-parameter translator. The project can start smoothly.

By default, the framework provides **no param translator, 1 param translator, 2 params translator** to address the most common needs.

## Enumeration translator

Enumeration translators limited to one parameter are used as follows.

Mark `@Translation` on the enumeration class, then mark the mapper field with `@TransMapper` and the translation field with `@TransValue`, and the framework will automatically generate a **one-parameter translator** for you.

Therefore, enumeration does not support multiple arguments, does not support other conditions, complex enumeration will only lead to programming difficulties, so the author believes that only this can meet most needs.

```java
@Translator("getStatus")
public enum Status {

    UNKNOWN(0, "未知"),

    NORMAL(1, "正常"),

    DELETED(2, "删除");

    @TransMapper
    private int code;

    @TransValue
    private String desc;

    Status(int code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }

    public int getCode()
    {
        return code;
    }

    public String getDesc()
    {
        return desc;
   }
}
```

