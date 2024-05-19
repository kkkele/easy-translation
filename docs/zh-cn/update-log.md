<style>
    li{
 		 font-size: 18px; /* 设置默认的字体大小 */
	}
</style>


# 更新日志

## v1.2.2@2024-05-19

- feat：增加插件性能分析器。
- feat：增加插件翻译回调注册器。
- refacotr：提前实例化translator，考虑后续增加原型bean调用动态方法。
- fix：插入TranslatorPostProcessor导致的空指针异常。
- fix：修复slf4j引起的日志依赖冲突，框架去除slf4j相关依赖，交由开发者自行实现。
- fix：增加对应的报错提示。

## v1.1.1@2024-05-18

- feat：增加other字段自动转为对应方法参数类型。
- refactor：重构TranslatorDefinitionReader写法，将其功能拆分为两个对象去完成。
- bugfix：重写Reflections框架，为其增加扫描组合注解的能力。
- bugfix：修复代理类的Bean翻译器无法找到的问题。
- refactor：小重构DefaultTransExecutorContext类。

## v1.1.0@2024-05-17

- refactor：更改easy-tranlsation-spring-boot-start命名为easy-tranlsation-spring-boot-start。
- feat：easy-translation-spring-boot3-starter正式发布。
- feat：增加空指针异常处理器，使得开发者可以灵活的处理映射过程中空指针的情况。**【重要】**
- refactor：贯彻组合优于继承的思想，将获取属性值的过程交给对象来做，使得开发者可以自由决定功能是如何实现的。**【重要】**
- refactor：默认采用MethodHandle来获取属性，而非反射，使得获取属性的性能有5倍左右的提升。
- refacotr：更改超时时间的单位为毫秒。

## v1.0.0 @2024-05-15

- 第一个版本正式发布！！！！

