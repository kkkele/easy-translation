package com.superkele.demo.test.processor;

import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 翻译器 上下文获取
 */

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class TranslatorProcessTest {

    @Autowired
    private DefaultTranslationProcessor translationProcessor;

    /**
     * 测试静态方法创建的translator （无需invokeBean激活，单例的翻译器）
     */
    @Test
    public void testStaticTranslator() {

    }

}
