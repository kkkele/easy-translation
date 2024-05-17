# 安装使用

---

> 使用maven

- spring-boot 3以下的版本

```xml
<dependency>
    <groupId>io.github.kkkele</groupId>
    <artifactId>easy-translation-spring-boot-start</artifactId>
    <version>${last.version}</version>
</dependency>
```

- spring-boot3 以上的版本 （开发中）

  ```xml
  <dependency>
      <groupId>io.github.kkkele</groupId>
      <artifactId>easy-translation-spring-boot3-start</artifactId>
      <version>${last.version}</version>
  </dependency>
  ```

- 非spring-boot环境

  需要手动去加载一些全局配置

  ```xml
  <dependency>
      <groupId>io.github.kkkele</groupId>
      <artifactId>easy-translation-core</artifactId>
      <version>${last.version}</version>
  </dependency>
  ```

  

> gradle

- spring-boot 3以下的版本

  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-spring-boot-start', version: '${last.version}'
  ```

- spring-boot3 以上的版本 （开发中）

  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-spring-boot3-start', version: '${last.version}'
  ```

- 非spring-boot环境

  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-core', version: '${last.version}'
  ```

  

