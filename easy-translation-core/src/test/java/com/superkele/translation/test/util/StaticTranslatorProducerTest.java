package com.superkele.translation.test.util;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.function.Translator;
import com.superkele.translation.core.handler.impl.StaticTranslatorProducer;
import org.junit.Test;

import java.lang.invoke.LambdaConversionException;

public class StaticTranslatorProducerTest {

    public static String getName(Long id, String name) {
        return StrUtil.join("_", id, name);
    }

    public static String getName(String name) {
        return StrUtil.repeat(name,5);
    }

    @Test
    public void test() {
        try {
            Translator<Long,String> getName = new StaticTranslatorProducer().produce(StaticTranslatorProducerTest.class.getDeclaredMethod("getName", String.class));
            System.out.println(getName.translate());
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
