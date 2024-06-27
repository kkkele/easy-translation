package com.superkele.translation.extension.executecallback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CallbackTranslatorPostProcessor implements TranslatorPostProcessor {

    private final List<TranslationCallBack> callBackRegisters;

    @Override
    public Translator postProcessorBeforeInit(Translator translator, String translatorName) {
        if (CollectionUtil.isEmpty(callBackRegisters)) {
            return translator;
        }
        Translator translatorPlus = translator;
        for (TranslationCallBack callBackRegister : callBackRegisters) {
            if (ReUtil.isMatch(callBackRegister.match(), translatorName)) {
                Translator copyTranslator = translatorPlus;
                translatorPlus = args -> {
                    Object result = copyTranslator.doTranslate(args);
                    callBackRegister.onSuccess(result);
                    return result;
                };
            }
        }
        return translatorPlus;
    }

    @Override
    public Translator postProcessorAfterInit(Translator translator, String translatorName) {
        return translator;
    }
}
