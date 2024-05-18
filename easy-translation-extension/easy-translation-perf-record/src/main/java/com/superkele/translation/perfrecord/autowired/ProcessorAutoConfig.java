package com.superkele.translation.perfrecord.autowired;

import com.superkele.translation.perfrecord.PerfRecordTranslatorFactoryPostProcessor;
import com.superkele.translation.perfrecord.PerfRecordTranslatorPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "easy-translation.debug", name = "debug", havingValue = "true")
public class ProcessorAutoConfig {

    @Bean
    public PerfRecordTranslatorPostProcessor performanceRecordTranslatorPostProcessor() {
        return new PerfRecordTranslatorPostProcessor();
    }

    @Bean
    public PerfRecordTranslatorFactoryPostProcessor performanceRecordTranslatorFactoryPostProcessor() {
        return new PerfRecordTranslatorFactoryPostProcessor();
    }
}
