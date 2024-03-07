package com.yunext.kmp.common.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

suspend fun hdVibratorMulti(vararg blocks: (suspend VibratorHelper.() -> Unit)) {
    blocks.forEach {
        it(VibratorHelper.hdVibrator)
    }
}

suspend fun hdVibrator(block: (suspend VibratorHelper.() -> Unit)) {
    hdVibratorMulti(block)
}

private suspend fun test() {
    hdVibratorMulti({ vibrateOnClear() }, { vibrateOnClear() })
    hdVibrator {
        vibrateOnClear()
    }
}

class VibratorHelper {
    private var vibrator: Vibrator? = null

    companion object {
        @JvmStatic
        val hdVibrator by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            VibratorHelper()
        }

        private val P1 by lazy {
            longArrayOf(
                10,
                180,
                10,
                90,
                4,
                90,
                7,
                80,
                2,
                120,
                4,
                50,
                2,
                40,
                1,
                40,
                4,
                50,
                2,
                40,
                1,
                40,
                4,
                50,
                2,
                40,
                1,
                40
            ) to intArrayOf(255, 0, 255, 0, 240, 0, 240, 0, 240, 0)
        }
        private val P2 by lazy {
            longArrayOf(10, 180, 10, 90, 4, 90, 7, 80, 2, 120) to intArrayOf(
                255,
                0,
                255,
                0,
                240,
                0,
                240,
                0,
                240,
                0
            )
        }


    }

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            this.vibrator = vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            this.vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun init(vibrator: Vibrator) {
        this.vibrator = vibrator
    }

    fun cancel() {
        vibrator?.cancel()
    }

    fun hasVibrator(): Boolean {
        return vibrator?.hasVibrator() == true
    }

    fun hasAmplitudeControl(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }
        return vibrator?.hasAmplitudeControl() == true
    }

    fun vibrate(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat)
            vibrator?.vibrate(vibrationEffect)
        } else {
            val pattern = mutableListOf<Long>()
            var isCloseMotor = false
            var duration = 0L
            for (i in amplitudes.indices) {
                if ((amplitudes[i] > 0) == isCloseMotor) {
                    duration += timings[i]
                } else {
                    pattern.add(duration)
                    isCloseMotor = amplitudes[i] > 0
                    duration = timings[i]
                }
            }
            pattern.add(duration)

            val patternA = pattern.toLongArray()
            @Suppress("DEPRECATION")
            vibrator?.vibrate(patternA, repeat)
        }
    }

    fun vibrateOneShot(milliseconds: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator?.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(milliseconds)
        }
    }

    fun vibratePredefined(predefined: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val vibrationEffect = VibrationEffect.createPredefined(predefined)
            vibrator?.vibrate(vibrationEffect)
        } else {
            // 系统预设效果就暂时不适配了
            throw UnsupportedOperationException("该系统不支持系统预设振动")
        }
    }

    /**
     * 计算错误时的振动效果
     * */
    fun vibrateOnError() {
        val (timings, amplitudes) = P1
        hdVibrator.vibrate(timings, amplitudes, -1)
    }

    /**
     * 清除时的振动效果
     * */
    fun vibrateOnClear() {
        val (timings, amplitudes) = P2
        hdVibrator.vibrate(timings, amplitudes, -1)
    }

    /**
     * 开始计算时的振动效果
     * */
    fun vibrateOnEqual() {
        hdVibrator.vibrateOneShot(50, 150)
    }

    /**
     * 按下按键时的振动效果
     * */
    fun vibrateOnClick() {
        hdVibrator.vibrateOneShot(5, 255)
    }
}