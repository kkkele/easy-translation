package com.superkele.translation.test.context;

import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.test.method.LoginUser;
import org.junit.Test;

public class DefaultTranslatorContextTest {

    @Test
    public void testDynamic(){
        LoginUser loginUser = new LoginUser("小红");
        DefaultTransExecutorContext context = new DefaultTransExecutorContext(loginUser);
        TranslateExecutor getById = context.findExecutor("getName");
        Object execute = getById.execute(1L);
        System.out.println(execute);
    }


    @Test
    public void testEnum(){
        DefaultTransExecutorContext context = new DefaultTransExecutorContext("com.superkele.translation.test");
        TranslateExecutor enumClazz = context.findExecutor("EnumClazz");
        System.out.println(enumClazz.execute(1));
    }
}
