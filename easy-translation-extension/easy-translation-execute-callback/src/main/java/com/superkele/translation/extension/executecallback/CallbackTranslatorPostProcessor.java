package com.superkele.translation.extension.executecallback;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReUtil;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CallbackTranslatorPostProcessor implements TranslatorPostProcessor {

    private final List<CallBackRegister> callBackRegisters;

    @Override
    public TranslateExecutor postProcessorBeforeInit(TranslateExecutor translator, String translatorName) {
        if (CollectionUtil.isEmpty(callBackRegisters)) {
            return translator;
        }
        TranslateExecutor translatorPlus = translator;
        for (CallBackRegister callBackRegister : callBackRegisters) {
            TranslateExecuteCallBack translateExecuteCallBack = callBackRegister.callBack();
            if (ReUtil.isMatch(callBackRegister.match(), translatorName)) {
                TranslateExecutor copyTranslator = translatorPlus;
                translatorPlus = args -> {
                    Object result = copyTranslator.execute(args);
                    translateExecuteCallBack.onSuccess(result);
                    return result;
                };
            }
        }
        return translatorPlus;
    }

    @Override
    public TranslateExecutor postProcessorAfterInit(TranslateExecutor translator, String translatorName) {
        return translator;
    }
}
