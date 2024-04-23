package com.superkele.translation.core.function;


public interface Translator<T, R> {

    default R translate() {
        return translate(null);
    }

    default R translate(T mapper) {
        return translate(mapper, null);
    }

    default R translate(T mapper, Object other){
        return translate(mapper);
    }
}
