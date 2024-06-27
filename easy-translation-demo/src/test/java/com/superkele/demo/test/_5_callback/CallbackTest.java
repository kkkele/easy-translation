package com.superkele.demo.test._5_callback;

import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.extension.executecallback.TranslationCallBack;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = {EasyTranslationDemoApplication.class, CallbackTest.PrintCallback.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class CallbackTest {

    @Autowired
    private DefaultTranslatorContext defaultTranslatorContext;
    @Test
    public void test_callback() {
        Translator translator = defaultTranslatorContext.findTranslator("getPair");
        translator.doTranslate();
    }

    @Translation(name = "getPair")
    public static Pair<String,String> getStr() {
        return Pair.of("hello","hello");
    }

    @Component
    public static class PrintCallback implements TranslationCallBack<Pair<String,String>> {

        @Override
        public String match() {
            return "getPair";
        }

        @Override
        public void onSuccess(Pair<String, String> result) {
            System.out.println(result);
        }

    }

}
