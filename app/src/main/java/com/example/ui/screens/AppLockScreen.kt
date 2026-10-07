package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.BiometricAuthHelper
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.viewmodel.PharmacyViewModel

@Composable
fun AppLockScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity
  var enteredPin by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val businessProfile by viewModel.businessProfile.collectAsState()

  // Auto trigger biometrics on lock screen display
  LaunchedEffect(Unit) {
    if (activity != null) {
      BiometricAuthHelper.authenticate(
        activity = activity,
        onSuccess = {
          viewModel.unlockAppBiometric()
        },
        onError = { err ->
          // Graceful fallback to PIN
          if (err != "Use PIN") {
            errorMessage = err
          }
        }
      )
    }
  }

  fun onKeyClick(digit: String) {
    if (enteredPin.length < 4) {
      val next = enteredPin + digit
      enteredPin = next
      errorMessage = null
      if (next.length == 4) {
        val success = viewModel.unlockAppWithPin(next)
        if (!success) {
          errorMessage = "Incorrect PIN. Try again (Default: 1234)"
          enteredPin = ""
        }
      }
    }
  }

  fun onBackspace() {
    if (enteredPin.isNotEmpty()) {
      enteredPin = enteredPin.dropLast(1)
      errorMessage = null
    }
  }

  fun triggerBiometricPrompt() {
    if (activity != null) {
      BiometricAuthHelper.authenticate(
        activity = activity,
        onSuccess = {
          viewModel.unlockAppBiometric()
        },
        onError = { err ->
          errorMessage = err
        }
      )
    } else {
      errorMessage = "Biometrics unavailable on this device. Enter PIN."
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF0F172A), // Dark Slate Navy
            Color(0xFF1E1B4B), // Deep Indigo
            Color(0xFF3B0764)  // Royal Dark Magenta
          )
        )
      )
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Security Shield Icon
      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(CircleShape)
          .background(RoyalMagenta.copy(alpha = 0.25f))
          .border(2.dp, RoyalMagenta, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "Security Shield",
          tint = Color(0xFFFFD54F),
          modifier = Modifier.size(42.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = businessProfile.businessName,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.White,
        letterSpacing = 1.sp
      )

      Text(
        text = "SECURITY LOCK & BIOMETRIC AUTHENTICATION",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 4 PIN Dots
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (i in 0 until 4) {
          val isFilled = i < enteredPin.length
          Box(
            modifier = Modifier
              .size(18.dp)
              .clip(CircleShape)
              .background(if (isFilled) Color(0xFFFFD54F) else Color.Transparent)
              .border(
                width = 2.dp,
                color = if (isFilled) Color(0xFFFFD54F) else Color(0xFF64748B),
                shape = CircleShape
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Error message or prompt
      AnimatedVisibility(visible = errorMessage != null) {
        Text(
          text = errorMessage ?: "",
          fontSize = 12.sp,
          color = Color(0xFFF87171),
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Numeric Keypad
      Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Rows 1-3
        val rows = listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9")
        )

        rows.forEach { row ->
          Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            row.forEach { digit ->
              PinKeyButton(text = digit, onClick = { onKeyClick(digit) })
            }
          }
        }

        // Bottom Row: Biometric, 0, Backspace
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
          // Biometric Scan Action Button
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(Color(0xFF334155).copy(alpha = 0.6f))
              .clickable { triggerBiometricPrompt() }
              .testTag("btn_biometric_scan"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Fingerprint,
              contentDescription = "Scan Fingerprint",
              tint = Color(0xFF38BDF8),
              modifier = Modifier.size(32.dp)
            )
          }

          // Digit 0
          PinKeyButton(text = "0", onClick = { onKeyClick("0") })

          // Backspace
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(Color(0xFF334155).copy(alpha = 0.6f))
              .clickable { onBackspace() }
              .testTag("btn_pin_backspace"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Backspace,
              contentDescription = "Backspace",
              tint = Color(0xFFCBD5E1),
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Biometric quick trigger button
      Button(
        onClick = { triggerBiometricPrompt() },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("btn_touch_biometric")
      ) {
        Icon(
          imageVector = Icons.Default.Fingerprint,
          contentDescription = null,
          tint = Color(0xFF38BDF8),
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Scan Biometric / Fingerprint",
          fontSize = 12.sp,
          color = Color(0xFFE2E8F0),
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Default PIN: 1234 • Pharmacist Authorization Required",
        fontSize = 11.sp,
        color = Color(0xFF64748B)
      )
    }
  }
}

@Composable
fun PinKeyButton(
  text: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(68.dp)
      .clip(CircleShape)
      .background(Color(0xFF1E293B))
      .border(1.dp, Color(0xFF334155), CircleShape)
      .clickable(onClick = onClick)
      .testTag("btn_pin_key_$text"),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}
