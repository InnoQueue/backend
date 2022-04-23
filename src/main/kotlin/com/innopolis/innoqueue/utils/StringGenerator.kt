package com.innopolis.innoqueue.utils

class StringGenerator(private val stringLength: Int) {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generateString(): String {
        return (1..stringLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
