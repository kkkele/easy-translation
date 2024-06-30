package com.superkele.demo.jackson_serializer;


import com.superkele.translation.annotation.Mapper;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class JsonVo {

    private Integer id = 0;

    private Integer cardType;

    @Mapping(translator = "getCardTypeNames", strategy = MappingStrategy.BATCH,timing = TranslateTiming.JSON_SERIALIZE,mappers = @Mapper("cardType"))
    private String cardTypeName;

    @Translator("getCardTypeNames")
    public static List<String> getCardTypeNames(List<Integer> cardTypes) {
        return cardTypes.stream()
                .map(cardType -> {
                    switch (cardType) {
                        case 1:
                            return "A";
                        case 2:
                            return "B";
                        case 3:
                            return "C";
                        default:
                            return "D";
                    }
                })
                .collect(Collectors.toList());
    }
}
