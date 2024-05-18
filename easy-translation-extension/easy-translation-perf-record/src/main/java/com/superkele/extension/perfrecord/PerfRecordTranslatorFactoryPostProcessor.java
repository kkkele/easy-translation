package com.superkele.extension.perfrecord;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.LogUtils;

import java.util.Arrays;

public class PerfRecordTranslatorFactoryPostProcessor implements TranslatorFactoryPostProcessor {
    @Override
    public void postProcess(ConfigurableTransDefinitionExecutorFactory factory) {
        Arrays.stream(factory.getTranslatorNames())
                .forEach(name -> {
                    TranslatorDefinition translatorDefinition = factory.findTranslatorDefinition(name);
                    TranslateExecutor translateExecutor = translatorDefinition.getTranslateExecutor();
                    int length = translatorDefinition.getParameterTypes().length;
                    translatorDefinition.setTranslateExecutor(args -> {
                        LogUtils.debug("{} 接收参数 {}", () -> name, () -> {
                            StringBuilder sb = new StringBuilder();
                            sb.append("[");
                            Object[] arr = Arrays.copyOf(args,length);
                            sb.append(StrUtil.join(",", arr));
                            sb.append("]");
                            return sb;
                        });
                        return translateExecutor.execute(args);
                    });
                });
    }
}
