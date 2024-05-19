# 性能分析器

> 本文主要讲解SpringBoot环境下如何使用，非SpringBoot环境需要额外进行手动配置
>
> 开发者可能会遇到无法找到slf4j实现类的报错，请自行解决相关问题

## application.yml中配置

```yml
easy-translation:
  debug: true
logging:
  level:
    com.superkele.translation: debug

```

## maven

maven中引入依赖

```xml
        <dependency>
            <groupId>io.github.kkkele</groupId>
            <artifactId>easy-translation-perf-record</artifactId>
            <version>${last.version}</version> <!--最小版本为1.2.0-->
        </dependency>
```

## gradle

使用gradle

```gradle
implementation group: 'io.github.kkkele', name: 'easy-translation-perf-record', version: ${last.version}  <!--最小版本为1.2.0-->
```



### 效果

```console
2024-05-19 16:28:28.919 DEBUG 22208 --- [nio-8080-exec-1] .t.e.p.PerfRecordTranslatorPostProcessor : [Easy Translation] getUser执行完成，耗时5ms
2024-05-19 16:28:28.924 DEBUG 22208 --- [         task-1] .t.e.p.PerfRecordTranslatorPostProcessor : [Easy Translation] getCatName开始执行
2024-05-19 16:28:28.924 DEBUG 22208 --- [         task-1] PerfRecordTranslatorFactoryPostProcessor : [Easy Translation] getCatName 接收参数 [-556595081]
```

![image-20240519163134277](./assets/image-20240519163134277.png)