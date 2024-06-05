package com.example.currencyconversion.utils

import android.os.Build
import android.provider.Settings
import androidx.test.platform.app.InstrumentationRegistry

object AnimationUtils {
    fun disableAnimations() {
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        uiAutomation.executeShellCommand("settings put global window_animation_scale 0.0")
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 0.0")
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 0.0")
    }

    fun enableAnimations() {
        val uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        uiAutomation.executeShellCommand("settings put global window_animation_scale 1.0")
        uiAutomation.executeShellCommand("settings put global transition_animation_scale 1.0")
        uiAutomation.executeShellCommand("settings put global animator_duration_scale 1.0")
    }
}