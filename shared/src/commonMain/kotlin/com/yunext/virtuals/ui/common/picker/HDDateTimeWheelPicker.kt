package com.yunext.virtuals.ui.common.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.kmp.common.util.datetimeFormat
import com.yunext.kmp.common.util.isLeapYear
import com.yunext.kmp.resource.color.app_textColor_333333
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import network.chaintech.ui.timepicker.ExperimentalSnapperApi
import network.chaintech.ui.timepicker.calculateAnimatedAlpha
import network.chaintech.ui.timepicker.calculateSnappedItemIndex
import network.chaintech.ui.timepicker.fadingEdge
import network.chaintech.ui.timepicker.rememberLazyListSnapperLayoutInfo
import network.chaintech.ui.timepicker.rememberSnapperFlingBehavior

private const val TAG = "HDDateTimeWheelPicker"

@Composable
fun HDDateTimeWheelPicker(
    modifier: Modifier = Modifier,
    state: HDDateTimeWheelPickerState = rememberHDDateTimeWheelPickerState(),
) {


    val perHeight = 44.dp
    val rowCount = 3
    val pickerHeight = perHeight * 3

    fun IntRange.indexOfDefault(value: Int, defaultIndex: Int = 0) =
        this.indexOf(value).run {
            if (this < 0) defaultIndex else this

        }


    fun toLocalDatetimeInternal(): LocalDateTime {
        val instant = Instant.fromEpochMilliseconds(state.dateTime)
        return datetimeFormat {
            instant.toLocalDateTimeCustom()
        }
    }

    val curLocalDateTime by remember(state.dateTime) {
        mutableStateOf(toLocalDatetimeInternal())
    }

    val yearTexts: List<String> by remember(state.yearRange, state.dateTime) {
        mutableStateOf(state.yearRange.map {
//            if (curLocalDateTime.year == it) {
//                "${it.toString().takeLast(2)}年"
//            } else "$it"
            "$it"
        })
    }

    val monthTexts: List<String> by remember(state.monthRange, state.dateTime) {
//        mutableStateOf(state.monthRange.map {
//            if (curLocalDateTime.monthNumber == it) {
//                "${it}月"
//            } else "$it"
//        })

        mutableStateOf(state.monthRange.map(Int::toString))
    }

    val dayTexts: List<String> by remember(state.dayRange, state.dateTime) {

//        mutableStateOf(state.dayRange.map {
//            if (curLocalDateTime.dayOfMonth == it) {
//                "${it}日"
//            } else "$it"
//        })
        mutableStateOf(state.dayRange.map(Int::toString))
    }
    val hourTexts: List<String> by remember(state.hourRange, state.dateTime) {
//        mutableStateOf(state.hourRange.map {
//            if (curLocalDateTime.hour == it) {
//                "${it}时"
//            } else "$it"
//        })
        mutableStateOf(state.hourRange.map(Int::toString))
    }

    val minuteTexts: List<String> by remember(state.minuteRange, state.dateTime) {
//        mutableStateOf(state.minuteRange.map {
//            if (curLocalDateTime.minute == it) {
//                "${it}分"
//            } else "$it"
//        })

        mutableStateOf(state.minuteRange.map(Int::toString))
    }



    Box(modifier.height(pickerHeight), contentAlignment = Alignment.Center) {
        HorizontalDivider(
            modifier = Modifier.padding(bottom = (pickerHeight / rowCount)),
            thickness = (0.5).dp,
            color = state.borderColor
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = (pickerHeight / rowCount)),
            thickness = (0.5).dp,
            color = state.borderColor
        )

        fun logDateInfo(): String {
            val year = curLocalDateTime.year
            val yearIndex = state.yearRange.indexOfDefault(year)
            val monthIndex =
                state.monthRange.indexOfDefault(curLocalDateTime.monthNumber)
            val dayIndex = state.dayRange.indexOfDefault(curLocalDateTime.dayOfMonth)
            val hourIndex = state.hourRange.indexOfDefault(curLocalDateTime.hour)
            val minuteIndex =
                state.minuteRange.indexOfDefault(curLocalDateTime.minute)
            return """
                    ${curLocalDateTime.year}[${yearIndex}] - ${curLocalDateTime.month.number}[$monthIndex] -${curLocalDateTime.dayOfMonth}[$dayIndex] ${curLocalDateTime.hour}[$hourIndex] : ${curLocalDateTime.minute}[$minuteIndex] 
             
                """.trimIndent()
        }

        val unit: @Composable (String) -> Unit = {

            Text(it, fontSize = 11.sp, color = app_textColor_333333)
        }

        val item: @Composable (String, String, Boolean) -> Unit = { unit, text, sel ->

            Row {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (sel) LocalContentColor.current else Color.Black,
                    maxLines = 1,
                    fontSize = if (sel) 16.sp else 14.sp
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = if (sel) unit else "",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (sel) LocalContentColor.current else Color.Black,
                    maxLines = 1,
                    fontSize = 14.sp
                )

            }

        }

        Row(
            Modifier.fillMaxSize().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 年
            HDWheelTextPicker(
                modifier = Modifier.weight(1f),
                startIndex = state.yearRange.indexOfDefault(curLocalDateTime.year),
                height = pickerHeight,
                texts = yearTexts,
                rowCount = rowCount,
                onScrollFinished = { index ->
                    val cur = state.yearRange[index]
                    state.applyDateTime(year = cur)
                    Napier.v {
                        "年 " +
                                logDateInfo()
                    }
                    index
                }

            ) { _, text, selected ->
                item("年", text, selected)
            }
//            unit("年")


            // 月
            SideEffect {
                Napier.e {
                    "SideEffect:" + logDateInfo()
                }
            }
            HDWheelTextPicker(
                modifier = Modifier.weight(1f),
                startIndex = state.monthRange.indexOfDefault(curLocalDateTime.monthNumber),
                height = pickerHeight,
                texts = monthTexts,
                rowCount = rowCount,
                onScrollFinished = { index ->
                    val cur = state.monthRange[index]
                    state.applyDateTime(month = cur)
                    Napier.v {
                        "月 " +
                                logDateInfo()
                    }
                    index
                }
            ) { _, text, selected ->
                item("月", text, selected)
            }
//            unit("月")

            // 天
            HDWheelTextPicker(
                modifier = Modifier.weight(1f),
                startIndex = state.dayRange.indexOfDefault(curLocalDateTime.dayOfMonth),
                height = pickerHeight,
                texts = dayTexts,
                rowCount = rowCount,
                onScrollFinished = { index ->
                    val cur = state.dayRange[index]
                    state.applyDateTime(day = cur)
                    Napier.v {
                        "天 " +
                                logDateInfo()
                    }
                    index
                }
            ) { _, text, selected ->
                item("日", text, selected)
            }
//            unit("日")
            // 小时
            HDWheelTextPicker(
                modifier = Modifier.weight(1f),
                startIndex = state.hourRange.indexOfDefault(curLocalDateTime.hour),
                height = pickerHeight,
                texts = hourTexts,
                rowCount = rowCount,
                onScrollFinished = { index ->
                    val cur = state.hourRange[index]
                    state.applyDateTime(hour = cur)
                    Napier.v {
                        "时 " +
                                logDateInfo()
                    }
                    index
                }
            ) { _, text, selected ->
                item("时", text, selected)
            }

//            unit("时")
            // 分钟
            HDWheelTextPicker(
                modifier = Modifier.weight(1f),
                startIndex = state.minuteRange.indexOfDefault(curLocalDateTime.minute),
                height = pickerHeight,
                texts = minuteTexts,
                rowCount = rowCount,
                onScrollFinished = { index ->

                    val cur = state.minuteRange[index]
                    state.applyDateTime(minute = cur)
                    Napier.v {
                        "分钟 " +
                                logDateInfo()
                    }
                    index
                }
            ) { _, text, selected ->
                item("分", text, selected)
            }
//            unit("分")
        }

//        Column {
//            Text(
//                "当前选择：${curLocalDateTime.year}-${curLocalDateTime.monthNumber}-${curLocalDateTime.dayOfMonth} ${curLocalDateTime.hour}:${curLocalDateTime.minute}",
//                color = Color.Red
//            )
//            Text("当前选择：${datetimeFormat { state.dateTime.toStr() }}", color = Color.Red)
//        }

    }

}

//-----------------------------------------------------------------------

@Stable
interface HDDateTimeWheelPickerState {
    val dateTime: Long

    var borderColor: Color

    var maxYear: Int
    var minYear: Int

    val yearRange: IntRange
    val monthRange: IntRange
    val dayRange: IntRange
    val hourRange: IntRange
    val minuteRange: IntRange

    val textStyle: TextStyle
        @Composable
        get() = MaterialTheme.typography.titleSmall.copy(
        )

    fun applyDateTime(
        year: Int? = null,
        month: Int? = null,
        day: Int? = null,
        hour: Int? = null,
        minute: Int? = null,
    )
}

@Stable
private enum class DayMax {
    D31,
    D30,
    D29,
    D28
    ;
}

private val DayMax.day: Int
    get() = when (this) {
        DayMax.D31 -> 31
        DayMax.D30 -> 30
        DayMax.D29 -> 29
        DayMax.D28 -> 28
    }

private class HDDateTimeWheelPickerStateImpl(
    dateTime: Long,
    yearMax: Int,
    yearMin: Int,
    borderColor: Color,
) : HDDateTimeWheelPickerState {
    private var _datTime by mutableStateOf(dateTime)
    private var _borderColor by mutableStateOf(borderColor)

    // range
    private var _maxYear: Int by mutableStateOf(yearMax)
    private var _minYear: Int by mutableStateOf(yearMin)
    private val _maxMonth: Int by mutableStateOf(12)
    private val _minMonth: Int by mutableStateOf(1)
    private var _maxDay: DayMax = DayMax.D31
    private val _minDay: Int by mutableStateOf(1)
    private val _maxHour by mutableStateOf(23)
    private val _minHour by mutableStateOf(0)
    private val _maxMinute by mutableStateOf(29)
    private val _minMinute by mutableStateOf(0)

    private val _yearRange: IntRange by mutableStateOf(_minYear.._maxYear)
    private val _monthRange: IntRange by mutableStateOf(_minMonth.._maxMonth)
    private var _dayRange: IntRange by mutableStateOf(_minDay.._maxDay.day)
    private val _hourRange: IntRange by mutableStateOf(_minHour.._maxHour)
    private val _minuteRange: IntRange by mutableStateOf(_minMinute.._maxMinute)

    override val dateTime: Long
        get() = _datTime

    init {
        // 检查更新
        datetimeFormat {
            val toLocalDateTimeCustom =
                Instant.fromEpochMilliseconds(dateTime).toLocalDateTimeCustom()
            val newDayMax = calDayMax(toLocalDateTimeCustom.year, toLocalDateTimeCustom.month)
            _maxDay = newDayMax
            _dayRange = (_minDay.._maxDay.day)
        }
    }

    override fun applyDateTime(
        year: Int?,
        month: Int?,
        day: Int?,
        hour: Int?,
        minute: Int?,
    ) {
        datetimeFormat {
            // 旧的时间
            val lastLocalDateTime =
                Instant.fromEpochMilliseconds(dateTime).toLocalDateTimeCustom()
            val lastDayRange = _dayRange
            val lastMaxDay = _maxDay
            // 检查year、month参数
            val lastYear = lastLocalDateTime.year
            val lastMonth = lastLocalDateTime.month
            val lastDay = lastLocalDateTime.dayOfMonth
            val editYear = year ?: lastYear
            val editMonth = month?.toMonth() ?: lastMonth
            var editDay = day ?: lastDay
            val lastDayMax = calDayMax(lastYear, lastMonth)

            val newDayMax = calDayMax(editYear, editMonth)
            // 当月份的最大天数不一样，更新_maxDay、_dayRange，并且更新day
            if (newDayMax != lastDayMax) {
                _maxDay = newDayMax
                _dayRange = (_minDay.._maxDay.day)
                editDay = if (editDay > _maxDay.day) _maxDay.day else editDay // 确保在范围中
            }

            // 生成新的时间
            val newLocalDateTime = lastLocalDateTime.modify(
                year = editYear,
                month = editMonth,
                day = editDay,
                hour = hour,
                minute = minute,
            )

            // 赋值
            _datTime = datetimeFormat {
                newLocalDateTime.toInstantCustom().toEpochMilliseconds()
            }
        }
    }


    override var borderColor: Color
        get() = _borderColor
        set(value) {
            this._borderColor = value
        }
    override var maxYear: Int
        get() = _maxYear
        set(value) {
            val localDateTime = datetimeFormat {
                Instant.fromEpochMilliseconds(dateTime).toLocalDateTimeCustom()
            }
            val curYear = localDateTime.year
            // 最大值改变时，正好当前时间是最大值
            if (curYear == maxYear) {
                applyDateTime(year = value)
            }
            _maxYear = value

        }


    override var minYear: Int
        get() = _minYear
        set(value) {
            val localDateTime = datetimeFormat {
                Instant.fromEpochMilliseconds(dateTime).toLocalDateTimeCustom()
            }
            val curYear = localDateTime.year
            // 最小值改变时，正好当前时间是最小值
            if (curYear == minYear) {
                applyDateTime(year = value)
            }
            _minYear = value
        }
    override val yearRange: IntRange
        get() = _yearRange
    override val monthRange: IntRange
        get() = _monthRange
    override val dayRange: IntRange
        get() = _dayRange
    override val hourRange: IntRange
        get() = _hourRange
    override val minuteRange: IntRange
        get() = _minuteRange


    private fun calDayMax(year: Int, month: Month): DayMax {
        return when {
            isLeapYear(year) && month == Month.FEBRUARY -> DayMax.D29
            !isLeapYear(year) && month == Month.FEBRUARY -> DayMax.D28
            (month == Month.APRIL) or (month == Month.JUNE) or (month == Month.SEPTEMBER) or (month == Month.NOVEMBER) -> DayMax.D30
            else -> DayMax.D31
        }
    }

}

@Composable
fun rememberHDDateTimeWheelPickerState(
    dateTime: Long = 0L,
    yearMax: Int = 2099,
    yearMin: Int = 2000,
    borderColor: Color = Color(0xFF007AFF).copy(0.7f),
): HDDateTimeWheelPickerState {
    return remember {
        val realDateTime = if (dateTime <= 0) Clock.System.now().toEpochMilliseconds() else dateTime
        HDDateTimeWheelPickerStateImpl(
            realDateTime,
            yearMax = yearMax,
            yearMin = yearMin,
            borderColor
        )
    }
}

private fun Int.toMonth() = when (this) {
    Month.JANUARY.number -> Month.JANUARY
    Month.FEBRUARY.number -> Month.FEBRUARY
    Month.MARCH.number -> Month.MARCH
    Month.APRIL.number -> Month.APRIL
    Month.MAY.number -> Month.MAY
    Month.JUNE.number -> Month.JUNE
    Month.JULY.number -> Month.JULY
    Month.AUGUST.number -> Month.AUGUST
    Month.SEPTEMBER.number -> Month.SEPTEMBER
    Month.OCTOBER.number -> Month.OCTOBER
    Month.NOVEMBER.number -> Month.NOVEMBER
    Month.DECEMBER.number -> Month.DECEMBER
    else -> throw IllegalArgumentException("不匹配的月份:$this")
}


@Composable
fun HDWheelTextPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    height: Dp,
    texts: List<String>,
    rowCount: Int,
    contentAlignment: Alignment = Alignment.Center,
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
    content: @Composable (Int, String, Boolean) -> Unit,
) {
    HDWheelPicker(
        modifier = modifier,
        startIndex = startIndex,
        count = texts.size,
        rowCount = rowCount,
        height = height,
        onScrollFinished = onScrollFinished,
        texts = texts,
        contentAlignment = contentAlignment,
        content = content
    )
}


@OptIn(ExperimentalSnapperApi::class)
@Composable
internal fun HDWheelPicker(
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
    count: Int,
    rowCount: Int,
    height: Dp,
    onScrollFinished: (snappedIndex: Int) -> Int? = { null },
    texts: List<String>,
    content: @Composable (Int, String, Boolean) -> Unit,
    contentAlignment: Alignment = Alignment.Center,
) {
    val lazyListState = rememberLazyListState(startIndex)
    val snapperLayoutInfo = rememberLazyListSnapperLayoutInfo(lazyListState = lazyListState)
    val isScrollInProgress = lazyListState.isScrollInProgress

    LaunchedEffect(isScrollInProgress, count) {
        if (!isScrollInProgress) {
            onScrollFinished(calculateSnappedItemIndex(snapperLayoutInfo) ?: startIndex)?.let {
                lazyListState.scrollToItem(it)
            }
        }
    }

    val topBottomFade = Brush.verticalGradient(
        0f to Color.Transparent,
        0.3f to Color.Black,
        0.7f to Color.Black,
        1f to Color.Transparent
    )

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .height(height)
                .fadingEdge(topBottomFade),
            state = lazyListState,
            contentPadding = PaddingValues(vertical = height / rowCount * ((rowCount - 1) / 2)),
            flingBehavior = rememberSnapperFlingBehavior(
                lazyListState = lazyListState
            )
        ) {
            items(count) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height / rowCount)
                        .alpha(
                            calculateAnimatedAlpha(
                                lazyListState = lazyListState,
                                snapperLayoutInfo = snapperLayoutInfo,
                                index = index,
                                rowCount = rowCount
                            )
                        ),
                    contentAlignment = contentAlignment
                ) {
//                    Text(
//                        text = texts[index],
//                        style = style,
//                        color = if (calculateSnappedItemIndex(snapperLayoutInfo) == index) color else Color.Black,
//                        maxLines = 1,
//                        fontSize = if (calculateSnappedItemIndex(snapperLayoutInfo) == index) 20.sp else 18.sp
//                    )
                    val selected = calculateSnappedItemIndex(snapperLayoutInfo) == index
                    content(index, texts[index], selected)
                }
            }
        }
    }
}

private operator fun IntRange.get(index: Int): Int {
    val list = this.toList()
//    require(index in list.indices) {
//        "error index = $index"
//    }
    val checkIndex = when {
        index < 0 -> 0
        index > list.size - 1 -> list.size - 1
        else -> index
    }
    return list[checkIndex]
}









