package com.innopolis.innoqueue.domain.util

import kotlin.random.Random.Default.nextInt

/**
 * Util class for generating random string
 */
class StringGenerator(
    private val stringLength: Int,
    private val excludedStrings: List<String> = emptyList()
) {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /**
     * Method for returning random string
     */
    fun generateString(): String {
        // TODO create quartz job since the pool of available strings might be empty
        var generatedString: String
        do {
            generatedString = getRandomString()
        } while (generatedString in excludedStrings)
        return generatedString
    }

    private fun getRandomString(): String = (1..stringLength)
        .map { nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}
