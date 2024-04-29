package com.superkele.translation.test.context;

import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import org.junit.Test;

public class DefaultTranslatorContextTest {

    @Test
    public void test(){
        DefaultTransExecutorContext context = new DefaultTransExecutorContext("com.superkele.translation.test");
        TranslateExecutor getById = context.findExecutor("getById");
        Object execute = getById.execute(1L);
        System.out.println(execute);
    }
}
