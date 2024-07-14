package com.superkele.translation.extension.perfrecord;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.decorator.TranslatorDecorator;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;


@Slf4j
public class PerfRecordTranslatorFactoryPostProcessor implements TranslatorFactoryPostProcessor {
    @Override
    public void postProcess(ConfigurableTranslatorDefinitionFactory factory) {
        Arrays.stream(factory.getTranslatorNames())
                .forEach(translatorName -> {
                    TranslatorDefinition translatorDefinition = factory.findTranslatorDefinition(translatorName);
                    TranslatorDecorator originTranslatorDecorator = translatorDefinition.getTranslateDecorator();
                    translatorDefinition.setTranslateDecorator(translator -> {
                        Translator preTranslator = originTranslatorDecorator.decorate(translator);
                        Translator res = args -> {
                            TransLog transLog = TransManager.getTransLog();
                            transLog.info("{}接收参数 {}", () -> translatorName, () -> {
                                StringBuilder sb = new StringBuilder();
                                sb.append("[");
                                sb.append(StrUtil.join(",", args));
                                sb.append("]");
                                return sb;
                            });
                            transLog.info("{}开始执行", () -> translatorName);
                            long start = System.currentTimeMillis();
                            Object result = preTranslator.doTranslate(args);
                            long end = System.currentTimeMillis();
                            transLog.info("{}执行完成，耗时{}ms，翻译结果为=> {}", () -> translatorName, () -> end - start, () -> {
                                if (result instanceof Iterable) {
                                    Iterable<Object> iterable = ((Iterable) result);
                                    String sb = "\n{\n" +
                                            StrUtil.join(",\n", iterable) +
                                            "\n}\n";
                                    return sb;
                                }
                                return "{"+result+"}";
                            });
                            return result;
                        };
                        return res;
                    });
                });
    }
}