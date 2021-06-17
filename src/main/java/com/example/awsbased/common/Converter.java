package com.example.awsbased.common;

public interface Converter<S, R> {
    R convert(S source);
}
