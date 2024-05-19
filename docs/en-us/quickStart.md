# Quick Start

> If you want to find the corresponding code in the example, can go to [Gitee](https://gitee.com/cai-zhiyuDaKeLe/easy-translation) | [Github](https://github.com/kkkele/easy-translation) View the demo module
>
> This page shows how to use SpringBoot in a non-SpringBoot environment, please see other documentation

## Enable Easy-Translation

Add the annotation `@EnableTranslation` to the startup class or any class that can be a **Bean** (e.g. `@Component`)

```java
@SpringBootApplication
@EnableTranslation
public class EasyTranslationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyTranslationApplication.class, args);
    }
}

```

`@EnableTranslation` can pass the package path

```java
@EnableTranslation({"com.superkele","io.github"})
```

As shown in the code above, `Easy-Translation` will automatically scan the "com.superkele", "io.github" packages and **subpackages of the class with the annotation added**.

The scan package is to obtain the **method**  and **enumeration** under the path as a translator, thus registering them into a translator.

It should be noted that **static methods** and **enumerations** can be directly registered as translators because they do not require object calls.

Dynamic methods require object calls, so they need to provide additional objects. However, in the SpringBoot environment, you do not need to do this step because the framework automatically retrieves eligible **beans** for registration.

## Register the translator

> **【Important】** Detailed usage Please see the core functions, only some functions are shown here to help you quickly get started

For example

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    @Translator("getUser")
    public SysUser getById(Integer id) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(id);
        sysUser.setUsername("username" + id);
        sysUser.setNickName("nickName" + id);
        return sysUser;
    }
}


@Data
public class SysUser {

    private Integer userId;

    private String username;

    private String nickName;
}
```

The method with `@Translator("getUser")` under UserServiceImpl registers as a translator named `getUser`. It is worth mentioning that even if you do not specify the name of this translator, `EasyTranslation` will still generate a **default** translator name for you. The default translator name rule developer is free to configure.

## Set the  translation field

> **【Important】** Detailed usage Please see the core functions, only some functions are shown here to help you quickly get started

Suppose we are developing a product management module, which consists of users, products, and product categories. Then we need to get the user information and product classification information together when we get the product information.

```java
@Data
public class Product {

    private Integer productId;

    private String productName;

    private Integer catId;

    private Integer createBy;

}

@Data
public class ProductCategory {
    private Integer catId;

    private String catName;
}

@Data
public class SysUser {

    private Integer userId;

    private String username;

    private String nickName;
}


```

This is the information we need to return to the user.

```java
@Data
public class ProductVo extends Product {


    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username")
    private String username;

    @Mapping(translator = "getCatName", mapper = "catId")
    private String catName;

}
```

We only need to specify the translator using the `@Mapping` annotation on the field that needs to be translated and then auto-populated, and we can use it.

Where `mapper` in `@Mapping` is the value of the mapping, you can think that `EasyTranslation` will get the value of ProductVo createBy and pass it to the `getUser` method as its argument.

`receive` is the attribute to receive. In the diagram, the nickName and username attributes are both derived from the same translator getUser. We can use **any attribute of the value returned by this translator**.

**[Important]** It is worth mentioning that if you **multiple properties call the same translator, and the parameters are consistent**, that is, you want the translation result of the translator to be received with multiple fields in the class. Then, the framework will reuse the results of the translator to reduce IO overhead. So you don't have to worry too much about performance.

Here's another example, this one to show that mapper can select properties of properties. Similar operations can also be performed on the receive field, and you can customize the separator, not only for the '.'

```java
@Data
public class ProductVoV2 {

    private Product product;

    @Mapping(translator = "getUser", mapper = "product.createBy", receive = "nickName")
    private String nickName;

    @Mapping(translator = "getUser", mapper = "product.createBy", receive = "username")
    private String username;

    @Mapping(translator = "getCatName", mapper = "product.catId")
    private String catName;
}

```

## Add annotation to the method you want to translate

> **【Important】** Detailed usage Please see the core functions, only some functions are shown here to help you quickly get started

To enable translation, add the annotation `@TranslationExecute` to the method you want to populate

```java
    @Override
    @TranslationExecute
    public ProductVoV2 getDetailByIdV2(Integer id) {
        Product byId = getById(id);
        ProductVoV2 productVO = new ProductVoV2();
        productVO.setProduct(byId);
        return productVO;
    }
```

It is worth mentioning that you can use this annotation on any method that returns a value other than void, as long as you **specify the property** that needs to be translated, for example, in controller.

```java
    @GetMapping("/{id}")
    @TranslationExecute(field = "data")
    public R<ProductVo> getDetailById(@PathVariable Integer id) {
        return R.ok(productService.getDetailById(id));
    }

@Data
public class R<T> {

    int code;

    T data;

    public R(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, data);
    }

    public static <T> R<T> error(T data) {
        return new R<>(500, data);
    }

    public static <T> R<T> error() {
        return new R<>(500, null);
    }

    public static <T> R<T> ok() {
        return new R<>(200, null);
    }
}

```

For `List`, `array`, `map`, etc., `EasyTranslation` unpacks it and then performs translation, for example

```java
    @GetMapping("/list")
    @TranslationExecute(field = "data")
    public R<List<ProductVo>> getList() {
        return R.ok(mappingToList(ListUtil.of(1, 2, 3, 4, 4)));
    }

    @GetMapping("/array")
    @TranslationExecute(field = "data")
    public R<ProductVo[]> getArray() {
        return R.ok(mappingToArray(1, 2, 3, 4, 5));
    }

    @GetMapping("/map")
    @TranslationExecute(field = "data")
    public R<Map<Integer, ProductVo>> getMap() {
        return R.ok(mappingToMap(1, 2, 3, 4, 5));
    }
```

If you want to unpack translation one by one on nested structures such as trees, linked List nodes, List<List<Object>>, or complex structures such as two-dimensional arrays, you can do it yourself

` TranslationUnpackingHandler ` interface, and then in ` @TranslationExecute ` specified on listTypeHandler attribute, the framework to the user to the realization of freedom, in order to meet the different needs of each developer.

```java
    @GetMapping("/array")
    @TranslationExecute(field = "data",listTypeHandler = DefaultTranslationTypeHandler.class)
    public R<ProductVo[]> getArray() {
        return R.ok(mappingToArray(1, 2, 3, 4, 5));
    }

```

## Start the application

At this point, you can launch your SpringBoot application and verify that the plug-in is working correctly.

Other more complex and more powerful usage and configuration, you can have a look at the core function.

If you have any questions, please contact the author or visit [gitee](https://gitee.com/cai-zhiyuDaKeLe/easy-translation), [github](https://github.com/kkkele/easy-translation) to propose your valuable issue.