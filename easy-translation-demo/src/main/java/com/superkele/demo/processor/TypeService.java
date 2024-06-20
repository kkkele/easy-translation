package com.superkele.demo.processor;

import com.superkele.translation.boot.annotation.Translator;
import org.springframework.stereotype.Service;


@Service
public class TypeService {

    @Translator(value = "getTypeById")
    public Type getTypeById(Integer id) {
        switch (id) {
            case 1:
                return new Type(1, "体育器械");
            case 2:
                return new Type(2, "食品");
            case 3:
                return new Type(3, "书籍");
            default:
                return null;
        }
    }

    @Translator(value = "getTypeId")
    public Integer getTypeId(Integer productId) {
        return productId % 3 + 1;
    }
}
