package com.superkele.demo.test._2_processor_reftranslation;

import com.superkele.demo.processor.ProductVo5;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 对于 @Mapping和DefaultTranslator的基础使用，在BaseMappingProcessTest中查看
 *
 * @see com.superkele.demo.test._1_processor.BaseMappingProcessTest
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class RefTranslationMappingProcessTest {

    @Autowired
    public DefaultTranslationProcessor processor;

    /**
     * 测试 refTranslation是否能关联翻译
     * @see com.superkele.demo.processor.ProductVo5
     */
    @Test
    public void testRefTranslation() {
        ProductVo5 father = new ProductVo5();
        father.setProductId(1);
        father.setProductName("father");
        father.setTypeId(1);
        ProductVo5 child = new ProductVo5();
        father.setChild(child);
        child.setProductId(2);
        child.setProductName("child");
        child.setTypeId(2);
        process(father);
        Assert.assertNotNull(child.getTypeName());
    }

    private void process(Object obj) {
        log.info("beforeProcess:{}", obj);
        processor.process(obj);
        log.info("afterProcess:{}", obj);
    }

}
