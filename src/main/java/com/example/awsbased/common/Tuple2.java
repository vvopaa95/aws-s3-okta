package com.example.awsbased.common;

import lombok.Value;

@Value
public class Tuple2<K, V> {
    K first;
    V second;
}
