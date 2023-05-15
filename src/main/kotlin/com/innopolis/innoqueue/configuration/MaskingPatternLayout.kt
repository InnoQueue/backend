package com.innopolis.innoqueue.configuration

import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.ILoggingEvent
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.IntStream


private const val VISIBLE_MASK_OFFSET = 7

class MaskingPatternLayout : PatternLayout() {
    private var multilinePattern: Pattern? = null
    private val maskPatterns = mutableListOf<String>()
    fun addMaskPattern(maskPattern: String) {
        maskPatterns.add(maskPattern)
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE)
    }

    override fun doLayout(event: ILoggingEvent): String {
        return maskMessage(super.doLayout(event))
    }

    private fun maskMessage(message: String): String {
        if (multilinePattern == null) {
            return message
        }
        val sb = StringBuilder(message)
        val matcher = multilinePattern!!.matcher(sb)
        while (matcher.find()) {
            maskGroup(matcher, sb)
        }
        return sb.toString()
    }

    private fun maskGroup(matcher: Matcher, sb: StringBuilder) {
        IntStream.rangeClosed(1, matcher.groupCount()).forEach { group: Int ->
            if (matcher.group(group) != null) {
                IntStream.range(matcher.start(group) + VISIBLE_MASK_OFFSET, matcher.end(group))
                    .forEach { i: Int -> sb.setCharAt(i, '*') }
            }
        }
    }
}
