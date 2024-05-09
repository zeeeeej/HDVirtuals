package com.yunext.kmp.ui.compose

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yunext.kmp.resource.color.app_background_brush
import kotlinx.coroutines.CoroutineScope


fun Modifier.hdBorder(debug: Boolean = false,width: Dp = 4.dp, ) =
    if (debug) this.border(width, hdRandomColor()) else this

@Stable
fun Modifier.hdBackground(color: () -> Color = { hdRandomColor() }): Modifier {
//    return this then BackgroundModifier(
//        color = color,
//        rtlAware = true,
//        inspectorInfo = debugInspectorInfo {
//            name = "color"
//            properties["color"] = color
//        }
//    )
    return this.drawWithContent {
        drawRect(color())
        drawContent()
    }
}

@Stable
fun Modifier.hdBackgroundBrush(brush: () -> Brush ): Modifier {
//    return this then BackgroundModifier(
//        color = color,
//        rtlAware = true,
//        inspectorInfo = debugInspectorInfo {
//            name = "color"
//            properties["color"] = color
//        }
//    )
    return this.drawWithContent {
        drawRect(brush())
        drawContent()
    }
}

//private class BackgroundModifier(
//    val color: () -> Color,
//    val rtlAware: Boolean,
//    inspectorInfo: InspectorInfo.() -> Unit,
//) : LayoutModifier, InspectorValueInfo(inspectorInfo) {
//    override fun MeasureScope.measure(
//        measurable: Measurable,
//        constraints: Constraints,
//    ): MeasureResult {
//        val placeable = measurable.measure(constraints)
//
//
//        return layout(placeable.width, placeable.height) {
//            placeable.placeRelative(0,0)
//        }
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        val otherModifier = other as? BackgroundModifier ?: return false
//
//        return color == otherModifier.color
//    }
//
//    override fun hashCode(): Int {
//        var result = color.hashCode()
//        result = 31 * result + rtlAware.hashCode()
//        return result
//    }
//
//    override fun toString(): String = "BackgroundModifier(color=$color)"
//}


fun Modifier.hdClip(shape: Shape) = graphicsLayer(shape = shape, clip = true)

@Composable
fun Modifier.clickablePure(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
//) = this then this.clickable(
) = this then Modifier.clickable(
//) = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    onClick = onClick,
    role = role,
    onClickLabel = onClickLabel,
    enabled = enabled
)

@Composable
fun Modifier.hdClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha = if (isPressed) .5f else 1f
    val scale = if (isPressed) 1.2f else 1f
    return Modifier.clickable(
        interactionSource = interactionSource,
        indication = null,//LocalIndication.current,
        onClick = onClick,
        role = role,
        onClickLabel = onClickLabel,
        enabled = enabled
    )
        .alpha(alpha = alpha)
        .scale(scale)
    //.indication(interactionSource, if (isPressed) MyIndication() else null) // todo
}

fun Modifier.clickableX(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
) = composed(inspectorInfo = debugInspectorInfo {
    name = "pressed"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["role"] = role
    properties["onClick"] = onClick
}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha = if (isPressed) .5f else 1f
    val scale = if (isPressed) 1.2f else 1f
    Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null,//LocalIndication.current,
            onClick = onClick,
            role = role,
            onClickLabel = onClickLabel,
            enabled = enabled
        )
        .alpha(alpha = alpha)
        .scale(scale)
    //.indication(interactionSource, if (isPressed) MyIndication() else null) // todo
}


/**
 * 详细见CommonRipple
 */
private open class MyIndication : Indication {
    open class Inner : IndicationInstance {


        override fun ContentDrawScope.drawIndication() {
            drawCircle(app_background_brush)
            draw()
            drawContent()
        }

        open fun add(interaction: PressInteraction.Press, scope: CoroutineScope) {

        }

        open fun remove(interaction: PressInteraction.Press) {

        }

        open fun ContentDrawScope.draw() {

        }


    }

    @Composable
    protected open fun rememberUpdateIndicationInstance(interactionSource: InteractionSource): Inner {
        return remember(interactionSource) {
            Inner()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val myIndication = rememberUpdateIndicationInstance(interactionSource)
        LaunchedEffect(key1 = myIndication) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        myIndication.add(interaction, this)
                    }

                    is PressInteraction.Release -> {
                        myIndication.remove(interaction.press)
                    }

                    is PressInteraction.Cancel -> {
                        myIndication.remove(interaction.press)
                    }
                }
            }
        }
        return myIndication
    }

}