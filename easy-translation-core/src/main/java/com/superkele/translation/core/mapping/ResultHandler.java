package com.superkele.translation.core.mapping;

public interface ResultHandler<T, R, S> {


    R handle(T result, String[] groupKey);

    S map(R processResult, Object translationObj, Object[] mapperKey);
}
