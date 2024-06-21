package com.superkele.translation.core.mapping.support;

import com.superkele.translation.core.mapping.PropertyCopier;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


public class DefaultPropertyCopier implements PropertyCopier {


    @Override
    public void copy(Object source, String receive, Object target, String properties, Function handle) {
        Optional.ofNullable(source)
                .map(s -> {
                    try {
                        return PropertyUtils.invokeGetter(s, receive);
                    } catch (Exception ex) {
                        return handle.apply(ex);
                    }
                })
                .map(s -> {
                    try {
                        PropertyUtils.invokeSetter(target, properties, s);
                        return s;
                    } catch (Exception ex) {
                        return handle.apply(ex);
                    }
                })
                .filter(Objects::nonNull)
                .ifPresent(finalVal -> PropertyUtils.invokeSetter(target, properties, finalVal));
    }
}
