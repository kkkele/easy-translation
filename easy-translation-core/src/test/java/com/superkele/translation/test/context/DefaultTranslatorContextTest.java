package com.superkele.translation.test.context;

import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import org.junit.Test;

public class DefaultTranslatorContextTest {

    @Test
    public void test(){
        DefaultTranslatorContext context = new DefaultTranslatorContext("com.superkele.translation.test");
        Translator getById = context.findTranslator("getById");
        System.out.println(getById.getDefaultTranslateHandler().execute(1L));
    }
}
