package com.yunext.virtuals.ui.screen.devicedetail.deviceproperty

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yunext.kmp.mqtt.virtuals.protocol.tsl.property.StructPropertyValue
import com.yunext.kmp.ui.compose.Debug
import com.yunext.virtuals.ui.data.PropertyValueWrapper
import com.yunext.virtuals.ui.data.wrap
import com.yunext.virtuals.ui.theme.ItemDefaults

@Composable
internal fun ContentPartValueStruct(wrapperPropertyData: PropertyValueWrapper) {
    Debug("[recompose_test_02] ContentPartValueStruct ${wrapperPropertyData.hashCode()}/${wrapperPropertyData.value.key.identifier} ")
    val list by remember(wrapperPropertyData) {
        val propertyValue = wrapperPropertyData.value as StructPropertyValue
        mutableStateOf(propertyValue.itemValues.values.map {
            it.wrap()
        })
    }

    ContentPartCurrentValue {
        Box(modifier = ItemDefaults.borderModifier) {
            StructItemList(list)
        }
    }
}