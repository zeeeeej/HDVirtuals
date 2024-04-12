package com.yunext.virtuals.ui.screen.configwifi

import cafe.adriel.voyager.core.model.screenModelScope
import com.yunext.virtuals.ui.common.HDStateScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfigWiFiScreenViewModel(initialState: String) : HDStateScreenModel<String>(initialState) {

    init {
        screenModelScope.launch {
            Napier.v("start...")
            delay(5000)
            Napier.v("end...")
        }
    }
}