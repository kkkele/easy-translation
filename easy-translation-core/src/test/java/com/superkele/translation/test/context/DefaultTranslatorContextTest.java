package com.superkele.translation.test.context;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.translator.support.DefaultTransExecutorFactory;
import com.superkele.translation.test.method.LoginUser;
import org.apache.commons.lang3.StringUtils;
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

    @Test
    public void testStatic(){
        DefaultTransExecutorContext context = new DefaultTransExecutorContext("com.superkele.translation.test");
        DefaultTransExecutorFactory translatorFactory = (DefaultTransExecutorFactory) context.getTranslatorFactory();
        for (String translatorName : translatorFactory.getTranslatorNames()) {
            System.out.println(translatorName);
        }
    }

    @Test
    public void test(){
        LoginUser loginUser = new LoginUser("小绿");
        LoginUser loginUser2 = new LoginUser("小绿2");
        Config config = new Config();
        config.setDefaultTranslatorNameGenerator((clazzName, methodName) -> StringUtils.join(clazzName, "#", methodName));
        DefaultTransExecutorContext context = new DefaultTransExecutorContext(config,
                new Object[]{loginUser,loginUser2},
                new String[]{"com.superkele.translation.test"});
        DefaultTransExecutorFactory translatorFactory = (DefaultTransExecutorFactory) context.getTranslatorFactory();
        for (String translatorName : translatorFactory.getTranslatorNames()) {
            System.out.println(translatorName);
        }
        System.out.println(context.findExecutor("getName").execute());
    }

    @Translation
    public static String testStaticLord(){
        return "hello world!";
    }
}
