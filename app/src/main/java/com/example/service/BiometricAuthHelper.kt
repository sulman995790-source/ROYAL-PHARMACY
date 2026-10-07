package com.example.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.util.Log

object BiometricAuthHelper {
  private const val TAG = "BiometricAuthHelper"

  fun isBiometricHardwareAvailable(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) ||
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
    } else {
      context.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }
  }

  fun authenticate(
    activity: Activity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      try {
        val cancellationSignal = CancellationSignal()
        val executor = activity.mainExecutor

        val prompt = BiometricPrompt.Builder(activity)
          .setTitle("ROYAL PHARMACY Security")
          .setSubtitle("Confirm identity to access pharmacy records")
          .setDescription("Scan fingerprint or use biometrics to unlock")
          .setNegativeButton("Use PIN", executor) { _, _ ->
            onError("Use PIN")
          }
          .build()

        prompt.authenticate(
          cancellationSignal,
          executor,
          object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
              super.onAuthenticationSucceeded(result)
              onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
              super.onAuthenticationError(errorCode, errString)
              onError(errString?.toString() ?: "Biometric error")
            }

            override fun onAuthenticationFailed() {
              super.onAuthenticationFailed()
              onError("Fingerprint not recognized. Try again or enter PIN.")
            }
          }
        )
      } catch (e: Exception) {
        Log.e(TAG, "Biometric prompt exception: ${e.message}", e)
        onError("Biometric authentication unavailable. Use PIN.")
      }
    } else {
      onError("Biometrics require Android 9.0+. Use PIN.")
    }
  }
}
