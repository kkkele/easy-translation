package com.superkele.translation.core.handler.support;

import com.superkele.translation.core.handler.TranslationProcessor;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractTranslationProcessor implements TranslationProcessor {

    public static int max_translator_param_len = 16;

    /**
     * 完整的翻译值过程
     *
     * @param source             翻译的根对象
     * @param translatorExecutor 翻译执行器
     * @param fieldName          翻译的字段
     * @param mapper             映射的字段
     * @param other              其他补充条件
     */
    protected void translateValue(Object source, TranslateExecutor translatorExecutor, String fieldName, String[] mapper, String[] other) {
        //将 mapper字段和 other字段调整位置
        int mapperLength = mapper.length;
        int otherLength = other.length;
        //组建参数
        Object[] args = new Object[max_translator_param_len];
        for (int i = 0; i < mapperLength; i++) {
            if (StringUtils.isNotBlank(mapper[i])) {
                args[i] = ReflectUtils.invokeGetter(source, mapper[i]);
            }
        }
        int j = 0;
        int i = mapperLength;
        while (i < mapperLength + otherLength) {
            args[i++] = other[j++];
        }
        //翻译值
        Object mappingValue = translatorExecutor.execute(args);
        //set注入
        ReflectUtils.invokeSetter(source, fieldName, mappingValue);
    }
}
