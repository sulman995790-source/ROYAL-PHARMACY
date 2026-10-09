package com.example.service

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AudioAlertService {
  private val _isAudioAlertsEnabled = MutableStateFlow(true)
  val isAudioAlertsEnabled: StateFlow<Boolean> = _isAudioAlertsEnabled.asStateFlow()

  private val _alertSoundStyle = MutableStateFlow("Beep") // "Beep", "Chime", "Alarm", "Siren"
  val alertSoundStyle: StateFlow<String> = _alertSoundStyle.asStateFlow()

  fun toggleAudioAlerts(enabled: Boolean) {
    _isAudioAlertsEnabled.value = enabled
  }

  fun setSoundStyle(style: String) {
    _alertSoundStyle.value = style
  }

  fun playLowStockAlert(context: Context) {
    if (!_isAudioAlertsEnabled.value) return
    try {
      val toneType = when (_alertSoundStyle.value) {
        "Alarm" -> ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK
        "Chime" -> ToneGenerator.TONE_DTMF_1
        "Siren" -> ToneGenerator.TONE_SUP_RINGTONE
        else -> ToneGenerator.TONE_PROP_BEEP
      }
      val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
      toneGenerator.startTone(toneType, 400)
      Handler(Looper.getMainLooper()).postDelayed({
        try {
          toneGenerator.release()
        } catch (_: Exception) {}
      }, 500)
    } catch (e: Exception) {
      // Ignore audio failure on emulators without audio routing
    }
  }
}
