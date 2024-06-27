package com.superkele.demo.test._07resulthandler;


import cn.hutool.core.collection.ListUtil;
import com.superkele.demo.EasyTranslationDemoApplication;
import com.superkele.demo.resulthandler.Book;
import com.superkele.demo.resulthandler.BookVo;
import com.superkele.demo.resulthandler.BookVo2;
import com.superkele.demo.resulthandler.BookVo3;
import com.superkele.demo.test._06paramhandler.ParamHandlerTest;
import com.superkele.demo.test.utils.PropertyUtils;
import com.superkele.translation.annotation.constant.DefaultUnpackingHandler;
import com.superkele.translation.core.processor.TranslationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@SpringBootTest(classes = {EasyTranslationDemoApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ResultHandlerTest {

    @Autowired
    private TranslationProcessor processor;

    @Test
    public void test_single_result_handler() {
        BookVo book = new BookVo();
        book.setId(1);
        processor.process(book);
        PropertyUtils.getProperties(book).forEach((k, v) -> {
            log.debug("{}:{}", k, v);
            Assert.assertNotNull(v);
        });
    }

    @Test
    public void test_collection_result_handler() {
        BookVo2 book1 = new BookVo2();
        book1.setId(1);
        BookVo2 book2 = new BookVo2();
        book2.setId(2);
        BookVo2 book3 = new BookVo2();
        book3.setId(3);
        List<BookVo2> bookVo2s = ListUtil.of(book1, book2, book3);
        processor.process(bookVo2s, BookVo2.class, "", false, DefaultUnpackingHandler.class);
        bookVo2s.forEach(book -> {
            PropertyUtils.getProperties(book).forEach((k, v) -> {
                log.debug("{}:{}", k, v);
                Assert.assertNotNull(v);
            });
        });
    }


    @Test
    public void test_diy_result_handler() {
        BookVo3 book1 = new BookVo3();
        book1.setId(1);
        BookVo3 book2 = new BookVo3();
        book2.setId(2);
        BookVo3 book3 = new BookVo3();
        book3.setId(3);
        List<BookVo3> bookVo2s = ListUtil.of(book1, book2, book3);
        processor.process(bookVo2s, BookVo3.class, "", false, DefaultUnpackingHandler.class);
        bookVo2s.forEach(book -> PropertyUtils.getProperties(book).forEach((k, v) -> {
            log.debug("{}:{}", k, v);
            Assert.assertNotNull(v);
        }));
    }
}
