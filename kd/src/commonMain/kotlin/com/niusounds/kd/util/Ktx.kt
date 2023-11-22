package com.niusounds.kd.util

fun <T> List<T>.infiniteIterator(): Iterator<T> {
    return object : Iterator<T> {
        var index = 0
        override fun hasNext(): Boolean = true

        override fun next(): T {
            val result = get(index)
            index = if (index < size - 1) {
                index + 1
            } else {
                0
            }
            return result
        }
    }
}
