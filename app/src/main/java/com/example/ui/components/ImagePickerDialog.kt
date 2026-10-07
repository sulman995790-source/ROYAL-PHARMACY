package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.io.File
import java.io.FileOutputStream

enum class ImagePickerType {
  STORE_FRONT,
  LICENSE_DOCUMENT,
  PROFILE_AVATAR
}

@Composable
fun ImagePickerDialog(
  title: String,
  pickerType: ImagePickerType = ImagePickerType.STORE_FRONT,
  onImageSelected: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  // 1. Gallery Photo Picker
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val savedPath = saveUriToInternalStorage(context, uri, pickerType.name.lowercase())
      onImageSelected(savedPath)
      Toast.makeText(context, "$title updated from gallery", Toast.LENGTH_SHORT).show()
      onDismiss()
    }
  }

  // 2. Camera Capture
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap: Bitmap? ->
    if (bitmap != null) {
      val savedPath = saveBitmapToInternalStorage(context, bitmap, pickerType.name.lowercase())
      onImageSelected(savedPath)
      Toast.makeText(context, "$title captured with camera", Toast.LENGTH_SHORT).show()
      onDismiss()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp),
      shape = RoundedCornerShape(16.dp),
      color = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE9FE)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = RoyalMagenta,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Choose camera, gallery, or sample photo", fontSize = 11.5.sp, color = TextMuted)
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Camera & Gallery
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Take Photo
          Button(
            onClick = { cameraLauncher.launch(null) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            modifier = Modifier.weight(1f).height(48.dp)
          ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Take Photo", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          // Gallery Pick
          OutlinedButton(
            onClick = {
              galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalMagenta),
            modifier = Modifier.weight(1f).height(48.dp)
          ) {
            Icon(Icons.Default.Collections, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("From Gallery", fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preset Samples Section (works instantly even in emulator with no gallery photos)
        Text(
          text = "Or Pick High-Resolution Sample Photo:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(10.dp))

        val presetItems = getPresetImages(pickerType)

        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(presetItems) { preset ->
            Card(
              onClick = {
                val savedPath = generateAndSavePresetBitmap(context, preset, pickerType)
                onImageSelected(savedPath)
                Toast.makeText(context, "${preset.label} applied", Toast.LENGTH_SHORT).show()
                onDismiss()
              },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(containerColor = GrayBackground),
              border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
              modifier = Modifier.width(130.dp)
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(preset.bgColorHex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = preset.iconEmoji,
                    fontSize = 24.sp
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = preset.label,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark,
                  maxLines = 1
                )
                Text(
                  text = preset.subtext,
                  fontSize = 9.5.sp,
                  color = TextMuted,
                  maxLines = 1
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

data class SamplePreset(
  val label: String,
  val subtext: String,
  val iconEmoji: String,
  val bgColorHex: Long,
  val titleText: String
)

private fun getPresetImages(type: ImagePickerType): List<SamplePreset> {
  return when (type) {
    ImagePickerType.STORE_FRONT -> listOf(
      SamplePreset("Royal Chemist", "Main Nameboard", "🏥", 0xFFE0F2FE, "ROYAL PHARMACY & SURGICALS\nLicensed Chemist & Druggist"),
      SamplePreset("Modern Rx Store", "Glass Entrance", "🏪", 0xFFFCE4EC, "ROYAL HEALTH PHARMACY\n24x7 Emergency Dispensary"),
      SamplePreset("Clinic Pharmacy", "Hospital Wing", "💊", 0xFFEDE9FE, "ROYAL CLINICAL DISPENSARY\nCivil Hospital Road Hub")
    )
    ImagePickerType.LICENSE_DOCUMENT -> listOf(
      SamplePreset("Form 20 Certified", "Drug Authority Seal", "📜", 0xFFFEF3C7, "GOVT OF ASSAM - DRUGS CONTROL\nFORM 20: RETAIL DRUG LICENSE\nLIC NO: DL-DAR-2024-8849"),
      SamplePreset("Form 21 Schedule", "Valid Till 2029", "📋", 0xFFDCFCE7, "GOVT OF ASSAM - DRUGS CONTROL\nFORM 21: SPECIFIED SCHEDULE C/C1\nLIC NO: DL-DAR-2024-8850"),
      SamplePreset("Pharmacist Reg", "State Pharmacy Council", "🔖", 0xFFE0E7FF, "PHARMACY COUNCIL OF INDIA\nREGISTERED PHARMACIST CERTIFICATE\nREG NO: AS-PHARM-44021")
    )
    ImagePickerType.PROFILE_AVATAR -> listOf(
      SamplePreset("Chief Pharmacist", "Dr. Sulman (MD)", "👨‍⚕️", 0xFFE0F2FE, "Chief Clinical Pharmacist"),
      SamplePreset("Store Manager", "Licensed Chemist", "👩‍⚕️", 0xFFFCE4EC, "Senior Registered Chemist"),
      SamplePreset("Pharmacy Emblem", "Caduceus Logo", "⚕️", 0xFFDCFCE7, "Royal Pharmacy Official Crest")
    )
  }
}

private fun generateAndSavePresetBitmap(context: Context, preset: SamplePreset, type: ImagePickerType): String {
  val width = 600
  val height = if (type == ImagePickerType.PROFILE_AVATAR) 600 else 400
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  // Background
  val bgPaint = Paint().apply {
    color = preset.bgColorHex.toInt()
    style = Paint.Style.FILL
  }
  canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

  // Border & Header
  val headerPaint = Paint().apply {
    color = android.graphics.Color.rgb(15, 23, 42) // Dark Navy
    style = Paint.Style.FILL
  }
  canvas.drawRect(0f, 0f, width.toFloat(), (height * 0.22f), headerPaint)

  // Header Title
  val textPaint = Paint().apply {
    color = android.graphics.Color.WHITE
    textSize = 28f
    isAntiAlias = true
    isFakeBoldText = true
    textAlign = Paint.Align.CENTER
  }
  canvas.drawText("ROYAL PHARMACY VERIFIED", (width / 2).toFloat(), (height * 0.14f), textPaint)

  // Center Content
  val centerPaint = Paint().apply {
    color = android.graphics.Color.rgb(30, 41, 59)
    textSize = 24f
    isAntiAlias = true
    textAlign = Paint.Align.CENTER
  }

  val lines = preset.titleText.split("\n")
  var startY = height * 0.45f
  for (line in lines) {
    canvas.drawText(line, (width / 2).toFloat(), startY, centerPaint)
    startY += 36f
  }

  // Stamp Box at bottom
  val stampPaint = Paint().apply {
    color = android.graphics.Color.rgb(225, 29, 72) // Magenta stamp
    style = Paint.Style.STROKE
    strokeWidth = 4f
    isAntiAlias = true
  }
  val stampRect = RectF(width * 0.2f, height * 0.76f, width * 0.8f, height * 0.92f)
  canvas.drawRoundRect(stampRect, 12f, 12f, stampPaint)

  val stampTextPaint = Paint().apply {
    color = android.graphics.Color.rgb(225, 29, 72)
    textSize = 20f
    isAntiAlias = true
    isFakeBoldText = true
    textAlign = Paint.Align.CENTER
  }
  canvas.drawText("OFFICIALLY ATTESTED & VERIFIED", (width / 2).toFloat(), height * 0.86f, stampTextPaint)

  return saveBitmapToInternalStorage(context, bitmap, type.name.lowercase())
}

private fun saveUriToInternalStorage(context: Context, uri: Uri, prefix: String): String {
  return try {
    val file = File(context.filesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
      FileOutputStream(file).use { output ->
        input.copyTo(output)
      }
    }
    file.absolutePath
  } catch (_: Exception) {
    uri.toString()
  }
}

private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String): String {
  return try {
    val file = File(context.filesDir, "${prefix}_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    file.absolutePath
  } catch (_: Exception) {
    ""
  }
}
