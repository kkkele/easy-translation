package com.superkele.demo.processor_mapping_handler;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.MappingHandler;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.boot.annotation.Translator;
import com.superkele.translation.core.constant.TranslationConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DictVo2 {

    private String dictType;

    private Integer dictCode;

    @Mapping(translator = "getDictValues",
            mapper = {"dictType", "dictCode"},
            receive = "dictValue",
            mappingHandler = @MappingHandler(TranslationConstant.MANY_TO_MANY_MAPPING_HANDLER))
    private String dictValue;

    @Translator("getDictValues")
    public static List<DictVo2> convertToValue(@TransMapper List<String> dictType, @TransMapper List<Integer> dictCode) {
        List<DictVo2> res = new ArrayList<>();
        for (String d_t : dictType) {
            for (Integer d_code : dictCode) {
                DictVo2 dictVo = new DictVo2();
                res.add(dictVo);
                dictVo.setDictType(d_t);
                dictVo.setDictCode(d_code);
                String dictValue = DictVo.convertToValue(d_t, d_code);
                dictVo.setDictValue(dictValue);
            }
        }
        return res;
    }
}
