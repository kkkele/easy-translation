package com.superkele.demo.paramhandler;

import com.superkele.translation.boot.annotation.Translator;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Type {

    private Integer typeId;

    private String typeValue;

    @Translator("getByTypeIdList")
    public static List<String> getByTypeIds(List<Integer> typeIds) {
        return typeIds.stream()
                .map(type -> {
                    switch (type) {
                        case 1:
                            return "飞禽";
                        case 2:
                            return "走兽";
                        default:
                            return "海鲜";
                    }
                })
                .collect(Collectors.toList());
    }

    @Translator("getByTypeIdArr")
    public static List<String> getByTypeIds(Integer[] typeIds) {
        return Arrays.stream(typeIds)
                .map(type -> {
                    switch (type) {
                        case 1:
                            return "飞禽";
                        case 2:
                            return "走兽";
                        default:
                            return "海鲜";
                    }
                })
                .collect(Collectors.toList());
    }

    @Translator("getByTypeIdSet")
    public static List<String> getByTypeIds(Set<Integer> typeIds) {
        return typeIds.stream()
                .map(type -> {
                    switch (type) {
                        case 1:
                            return "飞禽";
                        case 2:
                            return "走兽";
                        default:
                            return "海鲜";
                    }
                })
                .collect(Collectors.toList());
    }
}
