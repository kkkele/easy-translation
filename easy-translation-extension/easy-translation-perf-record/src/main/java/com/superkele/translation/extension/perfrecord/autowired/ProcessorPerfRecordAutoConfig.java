package com.superkele.translation.extension.perfrecord.autowired;

import com.superkele.translation.extension.perfrecord.PerfRecordTranslatorFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(prefix = "easy-translation.debug", havingValue = "true")
public class ProcessorPerfRecordAutoConfig {

    @Bean
    public PerfRecordTranslatorFactoryPostProcessor performanceRecordTranslatorFactoryPostProcessor() {
        return new PerfRecordTranslatorFactoryPostProcessor();
    }
}
