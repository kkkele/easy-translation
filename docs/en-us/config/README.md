# Config

## TranslationAutoConfigurationCustomizer

As long as you implement `TranslationAutoConfigurationCustomizer` related Bean, the framework will use it to make a change the Config

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



## Extended translator types

Because the principle of the framework is to get the handle of the method and then convert it to the `FunctionalInterface` corresponding to the number of parameters. So if your method has several parameters, you need to use the translator to receive the corresponding number of parameters.

As a Translator, a developer would inherit the Translator interface, define one and only one abstract method for it, and override the default doTranslate method.

For example

```java
public interface ThreeParamTranslator extends Translator {

    Object translate(Object var0, Object var1, Object var2);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2]);
    }
}
```

Thus, a three-parameter translator is implemented.

Then use `TranslationAutoConfigurationCustomizer` registered the three parameters for its type of translator.

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.registerTranslatorClazz(ThreeParamTranslator.class);
        };
    }
```

## Thread Pool

If you want to enable `asynchronous` translation, you must first configure the thread pool to be used for asynchronous in config.

like

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setThreadPoolExecutor(...);
        };
    }
```

## Timeout

Different properties of each object use different translators, the overall translation task is completed using `CountDownLatch` statistics, if the translation task is not completed, the framework will directly return the object being processed.

And overtime time, we can through `TranslationAutoConfigurationCustomizer` translate timeout for its Settings.

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setTimeout(1000); //单位:ms
        };
    }
```

## DefaultTranslatorNameGenerator

When the developer does not explicitly specify the name of the translator, the framework will generate a translator for its name, the name and the generation of strategy is decided by the Config.

We can use `TranslationAutoConfigurationCustomizer` for its configuration.

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setDefaultTranslatorNameGenerator((beanName, methodName) -> beanName + "." + methodName);
        };
    }
```

## DefaultBeanNameGetter

The BeanName source of the default translator name generator is determined by the beanNameGetter, which we can configure manually

```java
    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setBeanNameGetter(clazz -> clazz.getSimpleName());
        };
    }
```

