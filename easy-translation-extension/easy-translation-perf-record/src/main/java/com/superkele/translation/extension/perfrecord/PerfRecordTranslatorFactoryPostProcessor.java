package com.superkele.translation.extension.perfrecord;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


@Slf4j
public class PerfRecordTranslatorFactoryPostProcessor implements TranslatorFactoryPostProcessor {
    @Override
    public void postProcess(ConfigurableTranslatorFactory factory) {
        Arrays.stream(factory.getTranslatorNames())
                .forEach(name -> {
                    TranslatorDefinition translatorDefinition = factory.findTranslatorDefinition(name);
                    TranslateExecutor translateExecutor = translatorDefinition.getTranslateDecorator();
                    int length = translatorDefinition.getParameterTypes().length;
                    translatorDefinition.setTranslateDecorator(args -> {
                        LogUtils.info(log::debug,"{} 接收参数 {}", () -> name, () -> {
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
