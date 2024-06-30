package com.superkele.demo.resulthandler;

import cn.hutool.core.map.MapUtil;
import com.superkele.translation.boot.annotation.Translator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookService {

    public Map<Integer, String> bookFactory() {
        return MapUtil.builder(0, "添砖加瓦")
                .put(1, "java入门")
                .put(2, "java精通")
                .put(3, "java转后厨")
                .build();
    }

    @Translator("getBookList")
    public List<Book> getBookList(List<Integer> ids) {
        return ids.stream()
                .map(id -> {
                    Book book = new Book();
                    book.setId(id);
                    book.setBookName(bookFactory().get(id));
                    return book;
                })
                .collect(Collectors.toList());
    }

    @Translator("getBookArr")
    public Book[] getBookArr(List<Integer> ids) {
        return ids.stream()
                .map(id -> {
                    Book book = new Book();
                    book.setId(id);
                    book.setBookName(bookFactory().get(id));
                    return book;
                })
                .toArray(Book[]::new);
    }

    @Translator("getBookSet")
    public Set<Book> getBookSet(List<Integer> ids) {
        return ids.stream()
                .map(id -> {
                    Book book = new Book();
                    book.setId(id);
                    book.setBookName(bookFactory().get(id));
                    return book;
                })
                .collect(Collectors.toSet());
    }

    @Translator("getBookMap")
    public Map<Integer, Book> getBookMap(List<Integer> ids) {
        return ids.stream()
                .map(id -> {
                    Book book = new Book();
                    book.setId(id);
                    book.setBookName(bookFactory().get(id));
                    return book;
                })
                .distinct()
                .collect(Collectors.toMap(Book::getId, x -> x));
    }
}
