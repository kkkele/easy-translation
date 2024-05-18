# 上下文传递

在使用异步翻译的时候，会发生上下文丢失的情况。这会导致一些操作发生错误，如上下文保存了用户信息，翻译器中调用了存储在上下文的用户信息。

这时，我们可以自行实现多个ContextHolder，来存储线程中的上下文传递给子线程。

用法，声明了**ContextHolder**相关的bean并交予spring容器管理，框架**自动会帮开发者收集相关的ContextHolder**。来保存一个上下文状态。

举个例子

```java
@Component
public class UserContextHolder implements ContextHolder {
    @Override
    public Object getContext() {
        return RequestContextHolder.getRequestAttributes();

    }

    @Override
    public void passContext(Object context) {
        RequestContextHolder.setRequestAttributes((RequestAttributes) context);
    }

    @Override
    public void clearContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}

```

```java
@Data
public class ProductVo extends Product {


    @Mapping(translator = "getUser", mapper = "createBy", receive = "nickName",async = true)
    private String nickName;

    @Mapping(translator = "getUser", mapper = "createBy", receive = "username",async = true)
    private String username;

    @Mapping(translator = "getCatName", mapper = "catId",async = true)
    private String catName;

}

```

```java
    @Override
    @Translation(name = "getUser")
    @Cacheable(cacheNames = "user", key = "#id")
    public SysUser getById(Integer id) {
		//....
        System.out.println("getUser:"+RequestContextHolder.getRequestAttributes());
        //....
        return sysUser;
    }
    @Translator("getCatName")
    public String getNameById(Integer id) {
        System.out.println("getCatName:"+ RequestContextHolder.getRequestAttributes());
        return getById(id).getCatName();
    }
```

执行结果为

![image-20240518222510236](./assets/image-20240518222510236.png)

```json
{
  "code": 200,
  "data": {
    "productId": 1,
    "productName": "productName:1",
    "catId": 1821705670,
    "createBy": 1,
    "nickName": "nickName2024-05-18 22:19:07",
    "username": "username2024-05-18 22:19:07",
    "catName": "商品分类1821705670"
  }
}
```

