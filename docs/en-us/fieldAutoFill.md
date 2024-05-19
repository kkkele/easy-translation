# The translation field

## Core annotation

```java
/**
 * use the annotation to map the field value
 * support SPEL expression
 * It enables field self-translation,and the value of some fields can be implemented to translate another field
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * 翻译器名称
     */
    String translator() default "";

    /**
     * 映射的字段
     */
    String[] mapper() default {};

    /**
     * 接收的属性内容
     */
    String receive() default "";

    /**
     * 其他字段
     */
    String[] other() default {};

    /**
     * 执行时机
     */
    TranslateTiming timing() default TranslateTiming.AFTER_RETURN;

    /**
     * 当不为null时，是否也映射
     */
    boolean notNullMapping() default false;

    /**
     * 控制同步任务的执行顺序
     */
    int sort() default 0;

    /**
     * 是否异步执行;
     * 开启后仍然遵循sort排序，需要等待sort低的批次同步翻译字段全部执行完毕才开始翻译
     */
    boolean async() default false;


    /**
     * 在该字段翻译执行后再开始翻译，主要用于精细化控制异步翻译时的执行顺序
     * 当该字段生效时，无视sort的执行顺序
     * 该字段默认是由事件驱动进行翻译的，所以即时您将async设为false,也存在不在主线程中运行的情况
     * 这主要取决于最后触发该事件的翻译字段在哪个线程中
     */
    String[] after() default {};


    /**
     * 当翻译时，属性为空导致了空指针异常的解决方案
     * @return
     */
    Class<? extends NullPointerExceptionHandler> nullPointerHandler() default DefaultNullPointerExceptionHandler.class;

}
```

## The choice of translator

`@Mapping`use the `translator` attribute to use the corresponding translator

For example

```java
    @Translation(name = "getStrV2")
    public String getByCondition(Integer id, String other) {
        return StrUtil.join(",",id,other);
    }
```

```java
@Mapping(translator = "getStrV2", mapper = "id",other = "1")
private String str;
```

The str field will call the `String getByCondition(Integer id, String other)` method, which will then fill in the return value.

## Translation mapping field

> about mapper and`@TransMapper`，for the detail you can click [here](en-us/genTranslator?#transmapper)

The mapper property of `@Mapping` is used to get the other properties of the object and then populate them one by one into the parameters of the `mapper` in the translator.

Support nested use, such as 'next.next.val', etc

```java
class Node{
	Node next;
	int val;
}
```

This is a sentence generation translator

```java
    @Translator("introduce")
    public static String getSentence(@TransMapper Integer id,@TransMapper String name) {
        return "你好，我的名字是" + name + ",我的编号为" + id;
    }
```

We use the translator

```java
@Data
public class UserVO {

    private Integer userId;

    private String userName;

    @Mapping(translator = "introduce", mapper = {"userId","userName"})
    private String sentence;
}

```

Controller test

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

result:

```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "userName": "小红",
    "sentence": "你好，我的名字是小红,我的编号为1"
  }
}
```

## Translation condition complement

> About mapper and`@TransMapper`，for the detail you can click [here](zh-cn/genTranslator?#transmapper)

`@Mapping` uses the `other` attribute to receive constants and then populates them one by one into the translator's non-mapper arguments.

During the development process, developers may need some conditions to assist judgment. For common dictionary tables, see dictionary type.

For example

```java
    @Translator("dict")
    public static String getDictValue(Integer code, String dictType) {
        switch (dictType) {
            case "sex":
                switch (code) {
                    case 1:
                        return "男";
                    case 2:
                        return "女";
                    default:
                        return "未知";
                }
            case "status":
                switch (code) {
                    case 0:
                        return "正常";
                    case 1:
                        return "停用";
                    default:
                        return "未知";
                }
            default:
                return "未知";
        }
    }

    @Translator("str")
    public static String getStr(Boolean filter, @TransMapper String id) {
        if (!filter) {
            return "不给你看";
        }
        return "给你看==>" + id;
    }
```

```java
@Data
public class AppUserVo {

    private Integer userId;

    private Integer sexCode;

    @Mapping(translator = "dict", mapper = "sexCode",other = "sex")
    private String sexValue;

    private Integer statusCode;

    @Mapping(translator = "dict", mapper = "statusCode",other = "status")
    private String statusValue;

    @Mapping(translator = "str", mapper = "userId",other = "true")
    private String str1;

    @Mapping(translator = "str", mapper = "userId",other = "false")
    private String str2;
}
```

```java
    @GetMapping("/test2")
    @TranslationExecute(field = "data")
    public R<AppUserVo> test2(){
        AppUserVo appUserVo = new AppUserVo();
        appUserVo.setUserId(1);
        appUserVo.setSexCode(2);
        appUserVo.setStatusCode(1);
        return R.ok(appUserVo);
    }

```

```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "sexCode": 2,
    "sexValue": "女",
    "statusCode": 1,
    "statusValue": "停用",
    "str1": "给你看==>1",
    "str2": "不给你看"
  }
}
```

## Select result to fill property

When you want to get an attribute in the translation result,

You can use the `receive` attribute of `@Mapping`

**[Important]** If the same translator is called and the mapper, ohter and other parameters are the same, if the translator has been executed and the result has been obtained, the execution will not be repeated.

Fpr example

```java
    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username")
    private String username;
```

## TranslationTiming

The `timing` attribute of `@Mapping` determines the timing of the translation of this field

```java
public enum TranslateTiming {

    AFTER_RETURN,
    JSON_SERIALIZE,
    NO_EXECUTE;
}
```

`AFTER_RETURN` will be assigned immediately after processor processing (either pre-translation processing using spring dependency injection, or annotation, handled by aop).

`JSON_SERIALIZE` will be executed in object json serialization. (1.2.2 is not currently supported, please look forward to future updates)

`NO_EXECUTE` ignores this field.

## Whether to translate when not null

The notNullMapping annotation for `@Mapping` determines whether to call the translator to fill in the value when the value of the attribute is no longer empty

## Translation sorting

Sometimes, we need to take one value and map another value

For example, each employee has a department, and the department Id of the employee is recorded in the data table. At this time, we want to obtain the name of the department to which the employee belongs, we can first translate the department Id of the employee, and then render the department name according to the department Id.

The `sort` attribute of `@Mapping` controls the order in which the translator is executed, from smallest to largest

```java
@Service
public class DeptService {


    private static final Map<Integer, Integer> map;
    private static final Map<Integer, String> deptNameMap;

    static {
        map = MapUtil.newHashMap();
        map.put(1, 1);
        map.put(2, 2);
        deptNameMap = MapUtil.newHashMap();
        deptNameMap.put(1, "研发部门");
        deptNameMap.put(2, "测试部门");
    }

    /**
     * 模仿关联表获取Id
     *
     * @param id
     * @return
     */
    @Translator("getDeptId")
    public Integer getDeptIdById(Integer id) {
        return map.get(id);
    }

    @Translator("getDeptName")
    public String getDeptNameById(Integer id) {
        return deptNameMap.get(id);
    }
}
```

```java
@Data
public class Employee {

    private Integer employeeId;

    private String employeeName;

    @Mapping(translator = "getDeptId",mapper = "employeeId",sort = 1)
    private Integer deptId;

    @Mapping(translator = "getDeptName",mapper = "deptId",sort = 2)
    private String deptName;
}

```

```java
    @GetMapping("/test3")
    @TranslationExecute(field = "data")
    public R<Employee> test3(){
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        return R.ok(employee);
    }
```

```json
{
  "code": 200,
  "data": {
    "employeeId": 1,
    "employeeName": null,
    "deptId": 1,
    "deptName": "研发部门"
  }
}
```



## Asynchronous translation

When some fields are not related, you can use the `async` property of `@Mapping` to enable asynchronous translation, but you need to configure a thread pool for the asynchronous translator first.

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setThreadPoolExecutor(threadPool);
        };
    }
```

And then we can, we can implement asynchronous translation, and asynchronous translation will still follow the sort translation.

However, if the developer wants to translate after the asynchronous translation is executed, they can use [callback translation](# Callback translation(recommended for asynchronous translation)).

## Callback translation (recommended for asyn translation)

The `after` attribute of `@Mapping`, which can force the field to be translated after other field translations have been performed, will no longer obey the sort order.

You can use this property if you want control to be performed after the specified several properties have been translated.

For example

```java
@Data
public class SkuVo {

    Integer skuId;

    String skuName;

    Integer spuId;

    @Mapping(translator = "getSpuName",mapper = "spuId",async = true)
    Integer spuName;

    Integer createBy;

    @Mapping(translator = "getUser",mapper = "createBy",async = true,receive = "nickName")
    String createName;

    @Mapping(translator = "getDeptId",mapper = "createBy",async = true)
    Integer deptId;
    
    @Mapping(translator = "getDeptName",mapper = "deptId",async = true,after = "deptId")
    String deptName;

    @Mapping(translator = "getDesc",mapper = {"skuName","spuName"},after = {"createName","deptName"})
    String desc;
    

}
```

Since there is no sort relationship, spuName, createName, and deptId will be executed at the same time, and deptId translation will translate deptName before desc is triggered.

**[Important]** Note that  **asynchronous translation will cause context loss**, please configure the **context Holder**, or choose synchronous execution.

**If the IO consumption is not large, do not use asynchronous.**

## NullPointerExcetionHandler

The `nullPointerHandler` of `@Mapping` will handle null pointer exceptions caused by null attribute values during translation.

The nullPointerHandler of `@Mapping` will handle null pointer exceptions caused by null attribute values during translation **. ** **Note that not all null pointer exceptions are handled**

For example, if mapper="user.id" and the id attribute fails to be obtained because the user is empty, the nullPointerHandler will be used.

The framework already provides two strategies to choose from

```java
public class DefaultNullPointerExceptionHandler implements NullPointerExceptionHandler {
    @Override
    public void handle(NullPointerException exception) {
        throw exception;
    }
}
```

```java
public class IgnoreNullPointerExceptionHandler implements NullPointerExceptionHandler {
    @Override
    public void handle(NullPointerException exception) {
        return;
    }
}

```

By default, will throw an exception if the IgnoreNullPointerExceptionHandler will ignore the exception and then no longer injection.