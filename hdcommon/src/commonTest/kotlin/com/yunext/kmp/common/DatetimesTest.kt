package com.yunext.kmp.common

import com.yunext.kmp.common.util.datetimeFormat
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class DatetimesTest {

    @Test
    fun t1() {
        val now = Clock.System.now()
        val localDateTime = datetimeFormat {
            now.toLocalDateTimeCustom()
        }
        println("原始：$localDateTime")

        var changedLocalDateTime: LocalDateTime = localDateTime

        changedLocalDateTime = datetimeFormat {
            changedLocalDateTime.modifyYear(2008)
        }
        println("修改年：$changedLocalDateTime")
        assertEquals(changedLocalDateTime.year, 2008)

        changedLocalDateTime = datetimeFormat {
            changedLocalDateTime.modifyMonth(11)
        }
        println("修改月：$changedLocalDateTime")
        assertEquals(changedLocalDateTime.month.number, 11)

        changedLocalDateTime = datetimeFormat {
            changedLocalDateTime.modifyDay(22)
        }
        println("修改日：$changedLocalDateTime")
        assertEquals(changedLocalDateTime.dayOfMonth, 22)


        changedLocalDateTime = datetimeFormat {
            changedLocalDateTime.modifyHour(3)
        }
        println("修改时：$changedLocalDateTime")
        assertEquals(changedLocalDateTime.hour, 3)


        changedLocalDateTime = datetimeFormat {
            changedLocalDateTime.modifyMinute(4)
        }
        println("修改分：$changedLocalDateTime")
        assertEquals(changedLocalDateTime.minute, 4)

        val format = datetimeFormat {
            changedLocalDateTime.toInstantCustom().toEpochMilliseconds().toStr()
        }
        assertContains(format, "2008-11-22 03:04")
    }
}