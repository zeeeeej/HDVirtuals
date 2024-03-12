package com.yunext.virtuals.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.yunext.kmp.common.util.hdMD5
import com.yunext.kmp.common.util.hdUUID
import com.yunext.kmp.context.hdContext
import com.yunext.kmp.db.datasource.DemoDataSource
import com.yunext.kmp.db.datasource.impl.DemoDataSourceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.yunext.kmp.http.testKtor

@Composable
fun DemoScreen() {
    Column(Modifier.height(256.dp).fillMaxWidth().drawBehind {
        drawRect(Color.Black)
    }) {
        var text by remember { mutableStateOf("DemoScreen") }
        val demoDataSource: DemoDataSource by remember {
            mutableStateOf(DemoDataSourceImpl())
        }
        val coroutineScope = rememberCoroutineScope()
        Text(
            text,
            style = TextStyle(color = Color.White),
            modifier = Modifier.padding(12.dp, 8.dp)
                .verticalScroll(rememberScrollState())
                .weight(1f)
        )
        LibModuleDemo(listLibModules) { module ->
            when (module) {
                HDBle -> {
                    text = "todo ${module.moduleName}"
                }

                HDCommon -> {
                    coroutineScope.launch {
                        text = "md:${hdMD5(text)} ,random:${hdUUID(4)}"
                    }
                }

                HDContext -> {
                    coroutineScope.launch {
                        runCatching {
                            hdContext.context
                        }.onFailure {
                            text = "hdContext:${it}"
                        }.onSuccess {
                            text = it.toString()
                        }
                    }


                }

                HDDb -> {
                    coroutineScope.launch {
                        text = "start test db"
                        delay(2000)
                        text = "db ..."
                        demoDataSource.add()
                        val all = demoDataSource.findAll()
                        delay(2000)
                        text = "db result[${all.size}]:$all"
                    }
                }

                HDHttp -> {
                    coroutineScope.launch {
                        text = "start test http"
                        delay(2000)
                        text = "http ..."
                        val http = testKtor()
                        delay(2000)
                        text = "http result:${http}"
                    }
                }

                HDMqtt -> {
                    text = "todo ${module.moduleName}"
                }

                HDResource -> {
                    text = "todo ${module.moduleName}"
                }

                HDSerial -> {
                    text = "todo ${module.moduleName}"
                }
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {


            Button(onClick = {
                coroutineScope.launch {
                    text = "md:${hdMD5(text)} ,random:${hdUUID(4)}"
                }
            }) {
                Text("hdcommon")
            }

        }

    }
}

@Composable
private fun LibModuleDemo(list: List<LibModule>, onClick: (LibModule) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = list, key = { it.moduleName }) {
            LibModuleItem(it) {
                onClick(it)
            }
        }
    }
}

@Composable
private fun LibModuleItem(module: LibModule, onClick: () -> Unit) {
    Button(onClick = {
        onClick()
    }) {
        Text(module.moduleName)
    }
}

private val LibModules: Set<LibModule> by lazy {
    setOf(
        HDBle, HDCommon, HDContext, HDHttp, HDMqtt, HDResource, HDDb, HDSerial
    )
}

val listLibModules: List<LibModule>
    get() = LibModules.toList()

sealed interface LibModule {
    val moduleName: String
}

private data object HDCommon : LibModule {
    override val moduleName: String
        get() = ":hdcommon"
}

private data object HDContext : LibModule {
    override val moduleName: String
        get() = ":hdcontext"
}

private data object HDBle : LibModule {
    override val moduleName: String
        get() = ":hdble"
}

private data object HDHttp : LibModule {
    override val moduleName: String
        get() = ":hdhttp"
}

private data object HDMqtt : LibModule {
    override val moduleName: String
        get() = ":hdmqtt"
}

private data object HDSerial : LibModule {
    override val moduleName: String
        get() = ":hbserial"
}

private data object HDResource : LibModule {
    override val moduleName: String
        get() = ":hdsource"
}

private data object HDDb : LibModule {
    override val moduleName: String
        get() = ":hddb"
}