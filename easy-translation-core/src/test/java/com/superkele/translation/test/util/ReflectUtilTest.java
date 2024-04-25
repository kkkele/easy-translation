package com.superkele.translation.test.util;

import com.superkele.translation.core.util.ReflectUtils;
import com.superkele.translation.test.util.entity.Book;
import org.junit.Test;

import java.lang.reflect.Method;

public class ReflectUtilTest {

    @Test
    public void test() {
        Book book = new Book();
        Book child = new Book();
        book.setBook(child);
        child.setName("xx");
        Object o = ReflectUtils.invokeGetter(book, "student.id");
        System.out.println(o);
    }


    public static void print(){
        System.out.println("Hello world");
    }

    public String getById(Long id){
        return "";
    }
}
