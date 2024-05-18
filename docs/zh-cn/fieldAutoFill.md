# 翻译字段自动填充

## 核心注解

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

## 翻译器的选择

`@Mapping`使用translator属性来使用对应的翻译器

例如

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

该str字段将会调用String getByCondition(Integer id, String other)方法，然后将返回值填充。

## 翻译映射字段

> 关于mapper和`@TransMapper`，详细请看[生成翻译器篇](zh-cn/genTranslator?#transmapper)

`@Mapping`的mapper属性，用于获取对象的其他属性，然后逐一填充至翻译器中为mapper的参数中。

支持嵌套使用，例如 next.next.val等

```java
class Node{
	Node next;
	int val;
}
```

这是一个句子生成翻译器

```java
    @Translator("introduce")
    public static String getSentence(@TransMapper Integer id,@TransMapper String name) {
        return "你好，我的名字是" + name + ",我的编号为" + id;
    }
```

我们使用该翻译器

```java
@Data
public class UserVO {

    private Integer userId;

    private String userName;

    @Mapping(translator = "introduce", mapper = {"userId","userName"})
    private String sentence;
}

```

编写测试类

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

执行结果为

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

## 翻译条件补充

> 关于mapper和`@TransMapper`，详细请看[生成翻译器篇](zh-cn/genTranslator?#transmapper)

`@Mapping`使用other属性来接收常量，然后逐一填充至翻译器的非mapper的参数中。

开发者开发过程中，或许需要一些条件来辅助判断。比如常见的字典表，查看字典type。

举个例子

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

## 选择结果填充

当您希望获取翻译结果中的某个属性时，

可以使用`@Mapping`的receive属性

**【重要】**调用同一翻译器，且mapper，ohter等参数一致的情况下，如果该翻译器已经执行且获取到了结果，则不会重复执行。

例如

```java
    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username")
    private String username;
```

nickName会接收getUser翻译器的nickName属性，username会接收getUser翻译器的username属性。

支持嵌套调用，例如 next.next.val

```java
class Node{
	Node next;
	int val;
}
```

## 翻译执行时机

`@Mapping`的timing属性可以决定该字段翻译的时机

```java
public enum TranslateTiming {

    AFTER_RETURN,
    JSON_SERIALIZE,
    NO_EXECUTE;
}
```

`AFTER_RETURN`将在processor处理后立马赋值（可以使用spring依赖注入提前翻译处理，也可以使用注解，交由aop处理）。

`JSON_SERIALIZE`将在对象json序列化的执行。（1.1.1暂不支持，请期待后续更新）

`NO_EXECUTE`则为忽略该字段。

## 不为空时是否翻译

`@Mapping`的notNullMapping注解可以决定，当该属性的值已经不为空的时候，是否调用翻译器填充值

true：不为空也翻译填充

## 翻译排序

有时候，我们需要拿到一个值之后，在映射另一个值

例如，每个员工有部门，数据表中记录了员工的部门Id。这个时候，我们想获取该员工所属的部门名称，就可以先翻译出员工的部门Id，再根据部门Id渲染出部门名称。

`@Mapping`的sort属性可以控制翻译器的执行顺序，从小到大依次执行

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



## 异步翻译

当一些字段没有关联性时，即可使用`@Mapping`的**async**属性开启异步翻译，不过需要先配置一个异步翻译器使用的线程池。

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setThreadPoolExecutor(threadPool);
        };
    }
```

然后我们，我们就可以实现异步翻译了，异步翻译仍然会遵循按sort排序翻译。

不过如果开发者希望在异步翻译执行后再翻译，可以使用[回调翻译](#回调翻译（推荐异步翻译使用）)。

## 回调翻译（推荐异步翻译使用）

`@Mapping`的after属性，可以强制该字段在其他字段翻译执行后再翻译，将不再遵守sort的顺序。

如果需要希望控制在指定的几个属性翻译填充后再执行，可以使用该属性。

举个例子

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

因为没有sort关系，所以 spuName，createName和deptId将会同时执行，再deptId翻译过后将会执行翻译deptName，然后再触发desc的执行。

【重要】需要注意的是，**异步翻译会导致上下文丢失**，请自行配置**上下文Holder**，或者选择同步执行。

**如果IO的消耗不多，不必使用异步。**

## 空属性异常处理器

`@Mapping`的nullPointerHandler将会处理翻译过程中，**属性值为空导致的空指针异常**。

**注意**，并不是所有的空指针异常都会处理

例如，当mapper="user.id"，user为空导致了id属性获取失败，这时，将会使用nullPointerHandler进行处理。

框架已经提供了两种策略可供选择

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

默认会抛出异常，如果采用IgnoreNullPointerExceptionHandler将会忽略该异常然后不再注入。