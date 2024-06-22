package com.superkele.translation.core.translator.support;

import cn.hutool.core.convert.Convert;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;

import java.util.Arrays;
import java.util.Optional;

/**
 * 调整mapperKey的位置,使得调用方法参数时，按照 mapperKey0,mapperKey1,mapperKey2......other1,other2,other3的字段来传参
 */
public class ExecutorParamInvokeFactoryPostProcessor implements TranslatorFactoryPostProcessor {
    @Override
    public void postProcess(ConfigurableTranslatorDefinitionFactory factory) {
        String[] translatorNames = factory.getTranslatorNames();
        for (String translatorName : translatorNames) {
            TranslatorDefinition definition = factory.findTranslatorDefinition(translatorName);
            int[] mapperIndex = definition.getMapperIndex();
            Class<?>[] parameterTypes = definition.getParameterTypes();
            int[] otherIndex = new int[parameterTypes.length - mapperIndex.length];
            boolean[] flag = new boolean[parameterTypes.length];
            for (int index : mapperIndex) {
                flag[index] = true;
            }
            int i = 0;
            int j = 0;
            while (i < parameterTypes.length) {
                if (!flag[i]) {
                    otherIndex[j++] = i;
                }
                i++;
            }
            definition.setTranslateDecorator(translator ->
                    args -> {
                        Object[] reWrapper = reWrapper(args, mapperIndex, otherIndex, parameterTypes);
                        try {
                            return translator.doTranslate(reWrapper);
                        } catch (ClassCastException e) {
                            String params = Arrays.stream(reWrapper)
                                    .map(param -> Optional.ofNullable(param)
                                            .map(x ->   x + " : " + x.getClass().getSimpleName() )
                                            .orElse("NULL"))
                                    .reduce((x, y) -> x + "," + y)
                                    .orElse("");
                            System.err.println(translatorName +" | params=> ("+params+")");
                            throw new TranslationException("请确认mapper顺序与@TransMapper参数顺序一致，且类型相同", e);
                        }
                    });
        }
    }

    public Object[] reWrapper(Object[] args, int[] keys, int[] others, Class<?>[] parameterTypes) {
        Object[] reWrapperArgs = new Object[keys.length + others.length];
        // 复原原来的位置情况
        //arr的前几个位置全都是mapper，找到对应的key位置填充 ，others同理
        for (int i = 0; i < keys.length; i++) {
            reWrapperArgs[keys[i]] = args[i];
        }
        for (int i = 0; i < others.length; i++) {
            reWrapperArgs[others[i]] = Convert.convert(parameterTypes[others[i]], args[i + keys.length]);
        }
        return reWrapperArgs;
    }
}
