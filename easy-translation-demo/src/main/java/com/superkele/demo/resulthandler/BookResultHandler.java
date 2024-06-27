package com.superkele.demo.resulthandler;

import com.superkele.translation.core.mapping.ResultHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BookResultHandler implements ResultHandler<List<Book>, Map<Integer, Book>, Book> {
    @Override
    public Map<Integer, Book> handle(List<Book> result, String[] groupKey) {
        Map<Integer, Book> map = result.stream()
                .distinct()
                .collect(Collectors.toMap(Book::getId, x -> x));
        return map;
    }

    @Override
    public Book map(Map<Integer, Book> processResult, int index, Object source, Object[] mapperKey) {
        return processResult.get(mapperKey[0]);
    }
}
