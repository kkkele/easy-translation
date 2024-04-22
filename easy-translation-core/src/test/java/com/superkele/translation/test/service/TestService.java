package com.superkele.translation.test.service;


import com.superkele.translation.annotation.Translate;
import com.superkele.translation.annotation.Translator;
import com.superkele.translation.core.executor.TranslateExecutor;
import com.superkele.translation.core.container.AbstractTranslatorContainer;
import com.superkele.translation.test.entity.Book;
import org.junit.Test;


public class TestService {

/*    TestService service = this;
    AbstractTranslatorContainer abstractTranslatorContainer = null;
    TranslateExecutor translateExecutor = new TranslateExecutor(abstractTranslatorContainer);

    @Test
    public void test() {
        Book book = getBook();
        checkBook(book);
        translateExecutor.execute(book);
        checkBook(book);
    }

    @Translate
    public Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setName("testBook");
        book.setStudentId(1L);
        return book;
    }


    public void checkBook(Book... books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    @Translator(name = "studentTranslator")
    public String getByStudentId(Long studentId) {
        return studentId + "号:小明";
    }*/
}
