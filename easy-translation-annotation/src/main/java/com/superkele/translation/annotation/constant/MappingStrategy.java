package com.superkele.translation.annotation.constant;

/**
 * 映射策略
 */
public enum MappingStrategy {
    /**
     * 单条处理
     */
    SINGLE_MAPPING,
    /**
     * 批量处理
     */
    BATCH_MAPPING,
    /**
     * 自定义处理
     */
    DIY;

}
