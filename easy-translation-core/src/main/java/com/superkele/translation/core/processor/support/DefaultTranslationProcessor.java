package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.mapping.support.DefaultTranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationInfoFactory;

import java.util.concurrent.Executor;


public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {

    private TranslationInvoker translationInvoker = new DefaultTranslationInvoker();

    @Override
    protected boolean getAsyncEnabled() {
        return TransManager.getConfig().isAsyncEnabled();
    }

    @Override
    protected TranslationInvoker getTranslationInvoker() {
        return this.translationInvoker;
    }

    @Override
    protected Executor getExecutor() {
        return TransManager.getConfig().getThreadPoolExecutor();
    }

    @Override
    protected boolean getCacheEnabled() {
        return TransManager.getConfig().isCacheEnabled();
    }

    @Override
    public FieldTranslationInfoFactory getFieldTranslationInfoFactory() {
        return TransManager.getFieldTranslationInfoContext();
    }

}
