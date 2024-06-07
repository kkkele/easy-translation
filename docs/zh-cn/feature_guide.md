# 未来规划

> 作者本人也在使用该框架，使用过程中也知晓了框架的一些不足，这些是未来的开发方法，用以改进框架

- JSON序列化时翻译

  有了这个之后，就不用担心忘记加`@TranslationExecute`导致没有翻译的问题了，同时也会支持自动进行拆包（因为是会对每个节点都进行检查）。

  不过这样也没有了排序翻译和异步翻译，适用于简单的情景。

- 原型Bean注册翻译器

  Spring中分为单例Bean和原型Bean，目前所有的Bean只会注册一次，原型Bean也会当作单例Bean来使用。对于某些有特殊需求的用户来说，可能不是非常友好，框架后续应该会补齐这些功能。

- RPC远程翻译

  对于远程翻译，我们不得不再声明一个类，然后在类中调用 remoteService，这种重复的劳动虽然可以提高我们的灵活性和安全性。（例如，作者在远程oss_id转成图片的url就使用了该方法，remoteService其实只有一个getById参数。当我们转成翻译器之后，我们可以扩展方法参数，采用了一个兜底url，来解决远程服务掉线导致翻译功能不能正常执行的问题）。

  ```java
  @AutoConfiguration
  @RequiredArgsConstructor
  @ConditionalOnBean(ConfigurableTransExecutorContext.class)
  public class ResourceTranslator{
  
      @DubboReference
      private ResourceService resourceService;
  
      @Translator(value = TransCommonNames.OSS_ID_TO_URL)
      public String ossIdToUrl(Long resourceId,String elseUrl) {
          String url = null;
          try {
              url = resourceService.getUrl(resourceId);
          } catch (Exception e) {
              url = elseUrl;
          }
          return url;
      }
  }
  ```
  
  **不过，这在某一方面增加了开发者的工作量，作者希望未来，可以在注册中心中找到我们所有注册了的翻译器，然后远程执行，而不仅仅是只能扫描本地包。不过，那应该是以扩张包的形式来。**