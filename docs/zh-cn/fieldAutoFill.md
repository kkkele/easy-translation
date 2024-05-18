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



## 翻译执行时机

## 不为空时是否翻译

## 翻译排序

## 异步翻译

## 回调翻译（推荐异步翻译使用）