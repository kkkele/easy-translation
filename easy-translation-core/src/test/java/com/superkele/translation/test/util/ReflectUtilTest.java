package com.superkele.translation.test.util;

import com.superkele.translation.core.executor.DefaultTranslatorExecutor;
import com.superkele.translation.core.context.ClassPathTranslationContext;
import com.superkele.translation.core.function.Translator;
import com.superkele.translation.test.util.entity.Book;
import org.junit.Test;

public class ReflectUtilTest {

    @Test
    public void test() {
        ClassPathTranslationContext context = new ClassPathTranslationContext("com.superkele.translation.test.util");
        DefaultTranslatorExecutor defaultTranslatorExecutor = new DefaultTranslatorExecutor(context);
        context.register("bookNameTranslator", new Translator() {
            @Override
            public Object translate(Object mapper, Object other) {
                return String.format("%s号书，备注:%s", mapper, other);
            }
        });
        Book book = new Book();
        book.setId(1L);
        defaultTranslatorExecutor.execute(book);
        System.out.println(book);
    }
}
