package com.superkele.translation.core.util;

import org.reflections.scanners.AbstractScanner;

import java.util.function.Supplier;

public enum ScannerEnum {

    FILED(ReflectionsPlus.FieldMergedAnnotationsScanner::new),
    METHOD(ReflectionsPlus.MethodMergedAnnotationsScanner::new),
    TYPE(ReflectionsPlus.TypeMergedAnnotationsScanner::new);

    public final Supplier<AbstractScanner> scanner;

    ScannerEnum(Supplier<AbstractScanner> scanner) {
        this.scanner = scanner;
    }
}
