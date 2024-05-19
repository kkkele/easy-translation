package com.superkele.translation.extension.perfrecord;

import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PerfRecordTranslatorPostProcessor implements TranslatorPostProcessor {
    @Override
    public TranslateExecutor postProcessorBeforeInit(TranslateExecutor translator, String translatorName) {
        TranslateExecutor executor = (parameters) -> {
            LogUtils.debug(log::debug,"{}开始执行", () -> translatorName);
            long start = System.currentTimeMillis();
            Object result = translator.execute(parameters);
            long end = System.currentTimeMillis();
            LogUtils.debug(log::debug,"{}执行完成，耗时{}ms", () -> translatorName, () -> end - start);
            return result;
        };
        return executor;
    }

    @Override
    public TranslateExecutor postProcessorAfterInit(TranslateExecutor translator, String translatorName) {
        return translator;
    }
}
