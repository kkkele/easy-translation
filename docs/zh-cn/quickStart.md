# 快速启动

> 如果您想找到示例中对应的代码，可以前往[Gitee](https://gitee.com/cai-zhiyuDaKeLe/easy-translation) | [Github](https://github.com/kkkele/easy-translation) 的demo模块查看
>
> 本页面展示的为SpringBoot环境下是如何使用的，在非SpringBoot请看其他文档

## 启用Easy-Translation

启动类或任意可以成为**Bean**的类上（例如：`@Component`）增加注解`@EnableTranslation`

```java
@SpringBootApplication
@EnableTranslation
public class EasyTranslationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyTranslationApplication.class, args);
    }
}

```

**重要**的一点是，`@EnableTranslation`可以传递包路径

```java
@EnableTranslation({"com.superkele","io.github"})
```

如上图代码所示，`Easy-Translation`将会自动扫描“com.superkele"，"io.github"包及**添加了该注解的类的子包**。

扫描包是为了将路径下当作翻译器的**方法**和**枚举**获取，从而将他们注册转化成为翻译器。

需要注意的是，**静态方法**和**枚举**因为不需要对象调用，所以可以直接注册转化成为翻译器。

动态方法需要对象调用，所以需要额外提供对象。不过，在`SpringBoot`环境下，您并不需要做这一步，因为框架会自动**获取有资格的Bean**进行注册。

## 注册翻译器

> 【**重要】**详细用法请看核心功能，这里只展示部分功能，帮助您快速上手

这里给出完整代码

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

如图， UserServiceImpl下带有@Translator("getUser")的方法即注册成为了一个翻译器，翻译器名为`getUser`。值得一提的是，即使您不指定这个翻译器的名称，`EasyTranslation`仍然会为您生成一个**默认**的翻译器名称。默认的翻译器名称规则开发者可以自由配置。

## 设置需要翻译的字段属性

> 【**重要】**详细用法请看核心功能，这里只展示部分功能，帮助您快速上手

假设我们在开发一个商品管理模块，这个模块由 用户，商品，商品分类构成。然后我们需要在拿到商品信息的时候，一起拿到用户信息和商品分类信息。

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

这是我们需要返回给用户看的信息

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

我们只需要在需要翻译然后自动填充的字段上使用`@Mapping`注解指定翻译器，即可使用。

其中图中的`@Mapping`中的 `mapper`为映射的值，您可以认为，`EasyTranslation`将获取ProductVo的 createBy的值，然后传递给`getUser`方法，当作它的参数。

`receive`为需要接收的属性，图中，nickName和username属性都来源于同一翻译器`getUser`，我们可以使用该翻译器**返回值的任意属性**。

**【重要】**值得一提的是，如果您**多个属性调用了同一翻译器，且参数一致**，也就是说，您希望该翻译器的翻译结果在该类中用多个字段接收。那么，框架将会复用翻译器的结果，以减少IO的开销。所以您不用太过担心性能问题。

这里给出另一个例子，这个例子是为了说明，mapper可以选择属性的属性。同理receive字段也可以进行类似的操作，且分隔符您可以自定义，不只能为`.`号

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

## 在需要翻译的方法上添加注解

> 【**重要】**详细用法请看核心功能，这里只展示部分功能，帮助您快速上手

在需要填充的方法上添加注解`@TranslationExecute`，即可开启翻译功能

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

值得一提的是，您可以将该注解用在所有返回不为void的方法上，只要您**指定了需要被翻译的属性**即可，例如，可以在controller中处理。

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

对于`List`，`array`，`map`等，`EasyTranslation`会对其进行拆包，然后执行翻译，例如

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

如果您希望在 树结构，链表节点等嵌套结构，List<List<Object>> 或是类似二维数组等复杂结构上进行逐个拆包翻译，你可以自行实现

`TranslationUnpackingHandler`接口，然后在`@TranslationExecute`上指定listTypeHandler属性即可，框架交由用户去自由的实现，以满足每个开发者的不同需求。

```java
    @GetMapping("/array")
    @TranslationExecute(field = "data",listTypeHandler = DefaultTranslationTypeHandler.class)
    public R<ProductVo[]> getArray() {
        return R.ok(mappingToArray(1, 2, 3, 4, 5));
    }

```

## 启动项目

至此，您可以启动您的SpringBoot应用，检验插件是否正常运行。

其他更多更复杂更强大的用法和配置，您可以在核心功能中进行一个翻阅查看。

如果有任何问题，欢迎联系作者或在[gitee](https://gitee.com/cai-zhiyuDaKeLe/easy-translation)，[github](https://github.com/kkkele/easy-translation)上提出您宝贵的issue。

![mmqrcode1715662117475](assets/mmqrcode1715662117475.png)