package com.example;

public record Pair<L, R>(L left, R right) {

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    static <L, R> Pair<L, R> create(L left, R right) {
        return new Pair(left, right);
    }
}
