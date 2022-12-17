package com.innopolis.innoqueue.utils

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
        var generatedString: String
        do {
            generatedString = getRandomString()
        } while (generatedString in excludedStrings)
        return generatedString
    }

    private fun getRandomString(): String {
        return (1..stringLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
