package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;

/**
 * 调整mapperKey的位置,使得调用方法参数时，按照 mapperKey0,mapperKey1,mapperKey2......other1,other2,other3的字段来传参
 */
public class ExecutorParamInvokeFactoryPostProcessor implements TranslatorFactoryPostProcessor {
    @Override
    public void postProcess(ConfigurableTransDefinitionExecutorFactory factory) {
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
            definition.setTranslateExecutor(args ->
                    definition.getTranslator().doTranslate(reWrapper(args, mapperIndex, otherIndex)));
        }
    }

    public Object[] reWrapper(Object[] arr, int[] keys, int[] others) {
        Object[] list = new Object[keys.length + others.length];
        // 复原原来的位置情况
        //arr的前几个位置全都是mapper，找到对应的key位置填充 ，others同理
        for (int i = 0; i < keys.length; i++) {
            list[keys[i]] = arr[i];
        }
        for (int i = 0; i < others.length; i++) {
            list[others[i]] = arr[i + keys.length];
        }
        return list;
    }
}
