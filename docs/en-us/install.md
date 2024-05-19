# Install

---

> maven Version 1.2.2 or later is recommended

- spring-boot 3 or earlier

```xml
<dependency>
    <groupId>io.github.kkkele</groupId>
    <artifactId>easy-translation-spring-boot-starter</artifactId>
    <version>${last.version}</version>
</dependency>
```

- spring-boot3

  ```xml
  <dependency>
      <groupId>io.github.kkkele</groupId>
      <artifactId>easy-translation-spring-boot3-start</artifactId>
      <version>${last.version}</version>
  </dependency>
  ```

- Non-spring-boot environment
Some global configurations need to be loaded manually
  
```xml
  <dependency>
      <groupId>io.github.kkkele</groupId>
      <artifactId>easy-translation-core</artifactId>
      <version>${last.version}</version>
  </dependency>
  ```
  


> gradle Version 1.2.2 or later is recommended

- spring-boot 3 or earlier

  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-spring-boot-starter', version: '${last.version}'
  ```

- spring-boot3

  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-spring-boot3-starter', version: '${last.version}'
  ```

- Non-spring-boot environment
  Some global configurations need to be loaded manually
  
  ```gradle
  implementation group: 'io.github.kkkele', name: 'easy-translation-core', version: '${last.version}'
  ```
  
  

