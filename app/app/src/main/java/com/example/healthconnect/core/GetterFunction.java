package com.example.healthconnect.core;

@FunctionalInterface
public interface GetterFunction<T, R> {
    R apply(T t);
}