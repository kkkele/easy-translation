# Context Hodler

Context loss occurs when using asynchronous translation. This causes some operations to go wrong, such as saving the user information in the context, or invoking the user information stored in the context in the translator.

At this point, we can implement multiple ContextHolder ourselves to store the context in the thread and pass it to the child thread.

The bean associated with the **ContextHolder** is declared and handed over to the spring container for management, and the framework  **automatically collects the relevant ContextHolder** for the developer. To save a context state.

For example：

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

result：

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

