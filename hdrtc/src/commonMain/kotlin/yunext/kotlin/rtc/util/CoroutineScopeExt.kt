package yunext.kotlin.rtc.util

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun createScope(name: String) =
    CoroutineScope(Dispatchers.Main + SupervisorJob() + CoroutineName(name))