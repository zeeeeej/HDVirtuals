package com.yunext.virtuals.ui.demo

import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt

@Preview
@Composable
fun VelocityPreview() {

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val offsetXPx = remember { mutableIntStateOf(0) }
        Canvas(
            Modifier
                .fillMaxSize()
                .gestureMoveTapsDetector(
                    horizontalDrag = {
                        offsetXPx.value = it
                    },
                    tapDown = {

                    },
                    dragStart = {

                    },
                    animalStart = {},
                    lowerBound = -100f,
                    upperBound = 700f
                )
        ) {
            drawIntoCanvas {
                val canvas = it.nativeCanvas
                val paint = Paint().apply {
                    color = android.graphics.Color.RED
                    style = Paint.Style.FILL
                }
                canvas.translate(size.width / 2f + offsetXPx.value, size.height / 2f)
                canvas.drawCircle(0f, 0f, 100f, paint)
            }

        }
    }
}

fun Modifier.gestureMoveTapsDetector(
    horizontalDrag: (x: Int) -> Unit,
    tapDown: (position: Offset) -> Unit,
    dragStart: () -> Unit,
    animalStart: () -> Unit,
    lowerBound: Float? = null,
    upperBound: Float? = null,
): Modifier = composed {
    //Log.e("minTranslateX lowerBound=",lowerBound.toString())
    // 记录X偏移量
    val offsetX = remember { Animatable(0f) }
    if (offsetX.value.roundToInt() <= 0) {
        horizontalDrag.invoke(offsetX.value.roundToInt())
    }
    // 监听手势事件
    pointerInput(Unit) {
        // 计算偏移量衰变比率
        val decay = splineBasedDecay<Float>(this)
        coroutineScope {
            while (true) {
                awaitPointerEventScope {
                    val down = awaitFirstDown()
                    tapDown.invoke(down.position)
                    val pointerId = down.id
                    // 记录手指滑动速度
                    val velocityTracker = VelocityTracker()
                    // 停止任何正在进行的动画
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        offsetX.stop()
                    }
                    // 监听水平滑动
                    horizontalDrag(pointerId) { change ->
                        dragStart()
                        // 根据滑动事件更新动画值
                        val changeX = (offsetX.value + change.positionChange().x)
                        launch {
                            offsetX.snapTo(changeX)
                        }
                        // 重置速度跟踪器
                        if (changeX == 0f) {
                            velocityTracker.resetTracking()
                        } else {
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        }
                    }
                    // 滑动结束,准备启动动画
                    animalStart()
                    val velocity = velocityTracker.calculateVelocity().x
                    val targetOffsetX = decay.calculateTargetValue(
                        offsetX.value,
                        velocity,
                    )
//                    Log.e("targetOffsetX==", targetOffsetX.toString())
                    val mLowerBound = if(lowerBound == null) 0f else minOf(0f,lowerBound+size.width)
                    // 动画结束时停止
                    offsetX.updateBounds(
                        lowerBound = mLowerBound,
                        upperBound = upperBound?:0f
                    )
                    launch {
                        launch {
                            offsetX.animateDecay(velocity, decay)
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun RotatePreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val rotateAngle = remember { mutableStateOf(0f) }
        Canvas(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectRotaGestures {
                        rotateAngle.value += it
                    }
                }
        ) {
            drawIntoCanvas {
                val canvas = it.nativeCanvas
                val paint = Paint().apply {
                    color = android.graphics.Color.RED
                    style = Paint.Style.FILL
                }
                canvas.translate(size.width / 2f, size.height / 2f)
                canvas.rotate(rotateAngle.value)
                canvas.drawLine(-100f, 0f, 100f, 0f, paint)
            }

        }
    }
}

//旋转角度
suspend fun PointerInputScope.detectRotaGestures(
    panZoomLock: Boolean = false,
    onGesture: (rotation: Float) -> Unit
) {

    awaitEachGesture {
        var rotation = 0f
        var zoom = 1f
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var lockedToPanZoom = false

        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.fastAny { it.isConsumed }
            if (!canceled) {
                val zoomChange = event.calculateZoom()
                val rotationChange = event.calculateRotation()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    rotation += rotationChange

                    val centroidSize = event.calculateCentroidSize(useCurrent = false)
                    val zoomMotion = abs(1 - zoom) * centroidSize
                    val rotationMotion = abs(rotation * PI.toFloat() * centroidSize / 180f)

                    if (zoomMotion > touchSlop ||
                        rotationMotion > touchSlop
                    ) {
                        pastTouchSlop = true
                        lockedToPanZoom = panZoomLock && rotationMotion < touchSlop
                    }
                }

                if (pastTouchSlop) {
                    val effectiveRotation = if (lockedToPanZoom) 0f else rotationChange
                    if (effectiveRotation != 0f) {
                        onGesture(effectiveRotation)
                    }
                    event.changes.fastForEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    }
                }
            }
        } while (!canceled && event.changes.fastAny { it.pressed })
    }
}


@Preview
@Composable
fun VelocityOffsetPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val offsetXPx = remember { mutableStateOf(Offset(0f, 0f)) }
        Canvas(Modifier
            .fillMaxSize()
            .gestureMoveTapDetector(
                horizontalDrag = {
                    Log.e("offsetXPx=", it.toString())
                    offsetXPx.value = it
                },
                tapDown = {

                },
                dragStart = {

                },
                animalStart = {}
            )) {
            drawIntoCanvas {
                val canvas = it.nativeCanvas
                val paint = Paint().apply {
                    color = android.graphics.Color.RED
                    style = Paint.Style.FILL
                }
                canvas.translate(
                    size.width / 2f + offsetXPx.value.x,
                    size.height / 2f + offsetXPx.value.y
                )
                canvas.drawCircle(0f, 0f, 100f, paint)
            }

        }
    }
}

fun AnimatableOffset(
    initialValue: Offset,
    visibilityThreshold: Offset = Offset(
        Spring.DampingRatioHighBouncy,
        Spring.DampingRatioHighBouncy
    )
) = Animatable(
    initialValue,
    Offset.VectorConverter,
    visibilityThreshold
)

private fun Modifier.gestureMoveTapDetector(
    horizontalDrag: (offset: Offset) -> Unit,
    tapDown: (position: Offset) -> Unit,
    dragStart: () -> Unit,
    animalStart: () -> Unit,
): Modifier = composed {
    // 记录Y偏移量
    val offsetX = remember { AnimatableOffset(Offset(0f, 0f)) }
    horizontalDrag.invoke(offsetX.value)
    // 监听手势事件
    pointerInput(Unit) {
        // 计算偏移量衰变比率
        val decay = splineBasedDecay<Offset>(this)
        coroutineScope {
            while (true) {
                awaitPointerEventScope {
                    val down = awaitFirstDown()
                    tapDown.invoke(down.position)
                    val pointerId = down.id
                    // 记录手指滑动速度
                    val velocityTracker = VelocityTracker()
                    // 停止任何正在进行的动画
                    launch(start = CoroutineStart.UNDISPATCHED) {
                        offsetX.stop()
                    }
                    // 监听水平滑动
                    drag(pointerId) { change ->
                        dragStart()
                        // 根据滑动事件更新动画值
                        val changeX = (offsetX.value + change.positionChange())
                        launch {
                            offsetX.snapTo(changeX)
                        }
                        // 重置速度跟踪器
                        if (changeX.x == 0f || changeX.y == 0f) {
                            velocityTracker.resetTracking()
                        } else {
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position,
                            )
                        }
                    }
                    // 滑动结束,准备启动动画
                    animalStart()
                    val velocitx = velocityTracker.calculateVelocity().x
                    val velocity = velocityTracker.calculateVelocity().y
                    val velocitOffset = Offset(velocitx, velocity)
                    // 动画结束时停止
                    launch {
                        launch {
                            offsetX.animateDecay(velocitOffset, decay)
                        }
                    }
                }
            }
        }
    }
}