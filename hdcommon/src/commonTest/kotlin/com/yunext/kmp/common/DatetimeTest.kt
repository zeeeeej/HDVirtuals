package com.yunext.kmp.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.test.Test

// https://github.com/Kotlin/kotlinx-datetime
class DatetimeTest {

    @Test
    fun t1() {
        println("|------")
        val now = Clock.System.now()
        println("now : $now")
        println("------|")
    }

    @Test
    fun t2() {
        println("|------")
        val now = Clock.System.now()
        println("now : $now")
        val toLocalDateTime = now.toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
        val toLocalDateTimeSystemDefault = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val toLocalDateTimeShangHai = now.toLocalDateTime(TimeZone.of("Asia/Shanghai"))

        println("TimeZone.currentSystemDefault() : ${TimeZone.currentSystemDefault()}")
        println("toLocalDateTime : $toLocalDateTime")
        println("toLocalDateTime : $toLocalDateTimeSystemDefault")
        println("toLocalDateTimeShangHai : $toLocalDateTimeShangHai")
        println("------|")
    }

    @Test
    fun t3() = testWith {
        val localDateTime = LocalDateTime(2021, 2, 2, 11, 22, 33, 44)
        println("localDateTime:$localDateTime")
        val toInstant = localDateTime.toInstant(TimeZone.currentSystemDefault())
        println("toInstant:$toInstant")
        val localDateTime2 = LocalDateTime(2021, 2, 29, 11, 22, 33, 44)
        println("localDateTime2:$localDateTime2")
    }

    @Test
    fun t4() = testWith {
        val now: Instant = Clock.System.now()
        val today: LocalDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
// or shorter
        val today2: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        println("today:$today")
        println("today2:$today2")
        val parse = LocalDate.parse("2024-04-04")
        println("parse:$parse")
        val parse2 = LocalDate.parse("2024/04/04") // ?
        println("parse2:$parse2")
    }

    @Test
    fun t5() {
        testWith {
            val now: Instant = Clock.System.now()
            val thisTime: LocalTime = now.toLocalDateTime(TimeZone.currentSystemDefault()).time
            println("thisTime:$thisTime")
            val knownTime = LocalTime(hour = 23, minute = 59, second = 12)
            val timeWithNanos = LocalTime(hour = 23, minute = 59, second = 12, nanosecond = 999)
            val hourMinute = LocalTime(hour = 12, minute = 13)
            println("knownTime:$knownTime")
            println("timeWithNanos:$timeWithNanos")
            println("hourMinute:$hourMinute")
        }

    }

    @Test
    fun t6() = testWith {
        LocalDateTime.parse("2010-06-01T22:19:44")
        LocalDate.parse("2010-06-01")
        LocalTime.parse("12:01:03")
        LocalTime.parse("12:00:03.999")
        LocalTime.parse("12:0:03.999") // fails with an IllegalArgumentException
    }

    @Test
    fun t7() {
        testWith {
            // import kotlinx.datetime.format.*

            val dateFormat = LocalDate.Format {
                monthNumber(padding = Padding.SPACE)
                char('/')
                dayOfMonth()
                char(' ')
                year()
            }

            val date = dateFormat.parse("12/24 2023")
            println(date.format(LocalDate.Formats.ISO_BASIC)) // "20231224"

            val dateFormat2 = LocalDate.Format {
                year()
                char('/')
                monthNumber()
                char('/')
                dayOfMonth()
            }
            val dateFormat2Result = dateFormat2.parse("2012/12/12")
            println("dateFormat2Result:$dateFormat2Result")

            val format = LocalTime.Format {
                hour()
                char(':')
                minute()
                char(':')
                second()

            }
            val formatResult = format.parse("23:31:13")
            println("formatResult:$formatResult")

            val now =
                Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            val nowStr = "${now.date.format(dateFormat2)} ${now.time.format(format)}"
            println("nowStr:$nowStr")
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    @Test
    fun t8() = testWith {
        // import kotlinx.datetime.format.*
        val pattern = DateTimeComponents.Format {
            byUnicodePattern("uuuu-MM-dd'T'HH:mm:ss[.SSS]Z")
        }
        val patternStr = DateTimeFormat.formatAsKotlinBuilderDsl(pattern)
        println(patternStr)

// will print:
        /*
        date(LocalDate.Formats.ISO)
        char('T')
        hour()
        char(':')
        minute()
        char(':')
        second()
        alternativeParsing({
        }) {
            char('.')
            secondFraction(3)
        }
        offset(UtcOffset.Formats.FOUR_DIGITS)
         */
        val now = Clock.System.now()
        println("now = $now")
        val parse = pattern.parse(now.toString())
        println("parse = $parse")
    }

    /**
     * 格式化
     */
    @Test
    fun t9() {
        val formatPattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]"

        @OptIn(FormatStringsInDatetimeFormats::class)
        val dateTimeFormat = LocalDateTime.Format {
            byUnicodePattern(formatPattern)
        }

        println("->"+dateTimeFormat.parse("2023-12-24T23:59:59"))

        val formatPattern2 = "yyyy/MM/dd HH:mm:ss[.SSS]"
        @OptIn(FormatStringsInDatetimeFormats::class)
        val dateTimeFormat2 = LocalDateTime.Format {
            byUnicodePattern(formatPattern2)
        }
        println("->"+dateTimeFormat2.parse("2023/12/24 23:59:58.110"))
        val format =
            Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .format(dateTimeFormat2)
        println("->$format")
    }
}

private fun testWith(block: () -> Unit) {
    println("|------")
    block()
    println("------|")

}

//internal typealias Parser<T> = (String, Int) -> Pair<Int, T>
//internal val testDateParser: Parser<LocalDate>
//    get() = intParser(4, 10, SignStyle.EXCEEDS_PAD)
//        .chainIgnoring(concreteCharParser('-'))
//        .chain(intParser(2, 2))
//        .chainIgnoring(concreteCharParser('-'))
//        .chain(intParser(2, 2))
//        .map {
//            val (yearMonth, day) = it
//            val (year, month) = yearMonth
//            try {
//                LocalDate(year, month, day)
//            } catch (e: IllegalArgumentException) {
//                throw DateTimeFormatException(e)
//            }
//        }