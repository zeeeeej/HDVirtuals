package com.yunext.virtuals.ui.demo.rememberstate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

@Composable
fun TestRememberState() {
    Column {
        val state = rememberCatState()
        MyCat(Modifier.background(Color.LightGray), state)

        MyCat2(Modifier.background(Color.Yellow), state)
    }

}

@Composable
fun MyCat(modifier: Modifier = Modifier, state: CatState = rememberCatState()) {
    Column(modifier) {
        Text("我的猫")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.name = "mimi-${Random.nextInt()}"
        }) {
            Text("芳名")
            Spacer(Modifier.height(4.dp))
            Text(state.name)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.age = Random.nextInt(1, 99)
        }) {
            Text("芳龄")
            Spacer(Modifier.height(4.dp))
            Text("${state.age}岁")
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.lastEat = Random.nextFloat() * 100
        }) {
            Text("上次吃了")
            Spacer(Modifier.height(4.dp))
            Text("${state.lastEat * 100}kg")
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.update = Instant.fromEpochMilliseconds(state.update)
                .toLocalDateTime(TimeZone.currentSystemDefault()).run {
                this.date.plus(1, DateTimeUnit.DAY).atTime(this.time)
            }.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }) {
            Text("更新时间")
            Spacer(Modifier.height(4.dp))
            Text(state.updateStr)
        }

        Spacer(Modifier.height(24.dp))
        Text(state.toString(), color = Color.Red)
    }
}

@Composable
fun MyCat2(modifier: Modifier = Modifier, state: CatState = rememberCatState()) {
//    val name by remember(state.name) {
//        mutableStateOf(state.name)
//    }
//
//    val age by remember(state.age) {
//        mutableStateOf(state.age)
//    }
//
//    val lastEat by remember(state.lastEat) {
//        mutableStateOf(state.lastEat*100)
//    }
    val name = state.name
    val age = state.age
    val lastEat = state.lastEat
    val updateLocalDateTime by remember(state.update) {
        mutableStateOf(
            Instant.fromEpochMilliseconds(state.update)
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()))
    }
    Column(modifier) {
        Text("我的猫")
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.name = "mimi-${Random.nextInt()}"
        }) {
            Text("芳名")
            Spacer(Modifier.height(4.dp))
            Text(name)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.age = Random.nextInt(1, 99)
        }) {
            Text("芳龄")
            Spacer(Modifier.height(4.dp))
            Text("${age}岁")
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.lastEat = Random.nextFloat() * 100
        }) {
            Text("上次吃了")
            Spacer(Modifier.height(4.dp))
            Text("${lastEat}kg")
        }

        Spacer(Modifier.height(12.dp))
        Row(Modifier.clickable {
            state.update = updateLocalDateTime.run {
                this.date.plus(1, DateTimeUnit.DAY).atTime(this.time)
            }.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }) {
            Text("更新时间")
            Spacer(Modifier.height(4.dp))
            Text(state.updateStr)
        }

        Spacer(Modifier.height(24.dp))
        Text(state.toString(), color = Color.Red)
        Text("$name $age $lastEat @$updateLocalDateTime", color = Color.Red)
    }
}

@Composable
fun rememberCatState(
    name: String = "mimi",
    age: Int = 1,
    lastEat: Float = 0f,
    update: Long = 0L,
): CatState {
    return remember {
        CatStateImpl(name = name, age = age, lastEat = lastEat, update = update)
    }
}

@Stable
interface CatState {
    var name: String
    var age: Int
    var lastEat: Float
    var update: Long
}

val CatState.updateStr: String
    get() = Instant.fromEpochMilliseconds(update).toString()

private class CatStateImpl(
    name: String,
    age: Int,
    lastEat: Float,
    update: Long,
) : CatState {
    init {
        require(name.isNotEmpty()) {
            "name is empty"
        }

        require(age in 1..99) {
            "age in 1..99"
        }

        require(lastEat >= 0) {
            "lastEat >= 0"
        }
    }

    private var _name by mutableStateOf(name)
    private var _age by mutableStateOf(age)
    private var _lastEat by mutableStateOf(lastEat)
    private var _update by mutableStateOf(update)


    override var name: String
        get() = _name
        set(value) {
            require(name.isNotEmpty()) {
                "name is empty"
            }
            this._name = value
        }
    override var age: Int
        get() = _age
        set(value) {
            require(age in 1..99) {
                "age in 1..99"
            }
            this._age = value
        }
    override var lastEat: Float
        get() = _lastEat
        set(value) {
            require(lastEat >= 0) {
                "lastEat >= 0"
            }
            this._lastEat = value
        }
    override var update: Long
        get() = _update
        set(value) {
            this._update = if (value < 0) 0 else value
        }

    override fun toString(): String {
        return "[$name],[$age],[$lastEat],[$update]"
    }

}

