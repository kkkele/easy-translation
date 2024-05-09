package com.superkele.demo.domain;


import lombok.Data;

@Data
public class R<T> {

    int code;

    T data;

    public R(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, data);
    }

    public static <T> R<T> error(T data) {
        return new R<>(500, data);
    }

    public static <T> R<T> error() {
        return new R<>(500, null);
    }

    public static <T> R<T> ok() {
        return new R<>(200, null);
    }
}
