# 配置

## TranslationAutoConfigurationCustomizer

只要您实现了TranslationAutoConfigurationCustomizer相关的Bean，框架会使用其对Config进行一个修改

示例：

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setTimeout(1000)
                    .setThreadPoolExecutor(threadPoolTaskExecutor.getThreadPoolExecutor());
            config.registerTranslatorClazz(ThreeParamTranslator.class);
        };
    }
```



## 扩充翻译器类型

因为框架的原理是获取方法的句柄，然后转化成对应参数个数的FunctionalInterface。所以你的方法有几个参数，就要使用对应的参数个数的翻译器来接收。

而作为一个翻译器，开发者需要继承Translator接口，然后为其定义有且只有一个抽象方法，并且对默认的doTranslate方法进行覆盖。

举个例子

```java
public interface ThreeParamTranslator extends Translator {

    Object translate(Object var0, Object var1, Object var2);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2]);
    }
}
```

这样就实现了一个三参数翻译器。

然后使用TranslationAutoConfigurationCustomizer 为其注册该三参数翻译器类型。

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.registerTranslatorClazz(ThreeParamTranslator.class);
        };
    }
```

## 线程池

如果您想开启异步翻译功能，得先在config中配置异步所要使用的线程池。

如下图所示

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setThreadPoolExecutor(...);
        };
    }
```

## 超时时间

每个对象的不同属性使用不同的翻译器，总体的翻译任务是否完成使用countDownLatch进行统计，如果翻译任务迟迟不完成，框架将直接返回正在处理的对象。

而超时时间，我们可以通过TranslationAutoConfigurationCustomizer为其设置翻译超时时间。

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setTimeout(1000); //单位:ms
        };
    }
```

## 默认翻译器名称生成器

当开发者没有显式的指定翻译器的名称时，框架将为其生成一个翻译器名称，而这个名称的生成策略由Config中的defaultTranslatorNameGenerator决定。

我们可以通过TranslationAutoConfigurationCustomizer为其配置。

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setDefaultTranslatorNameGenerator((beanName, methodName) -> beanName + "." + methodName);
        };
    }
```

## Bean名称获取

默认翻译器名称生成器的BeanName来源由beanNameGetter决定，我们可以为其进行手动配置

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setBeanNameGetter(clazz -> clazz.getSimpleName());
        };
    }
```

