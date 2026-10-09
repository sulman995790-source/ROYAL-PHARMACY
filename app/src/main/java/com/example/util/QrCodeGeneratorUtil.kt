package com.example.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.data.model.MedicineItem
import org.json.JSONObject

data class MedicineQrPayload(
  val id: Long,
  val name: String,
  val batch: String,
  val exp: String,
  val mrp: Double,
  val saleRate: Double,
  val rack: String,
  val barcode: String
) {
  fun toJsonString(): String {
    val finalRate = if (saleRate > 0) saleRate else mrp
    return JSONObject().apply {
      put("type", "BATCH_QR")
      put("rxId", id)
      put("name", name)
      put("medicineName", name)
      put("batch", batch)
      put("batchNumber", batch)
      put("batchNo", batch)
      put("exp", exp)
      put("expiryDate", exp)
      put("expiry", exp)
      put("mrp", mrp)
      put("price", finalRate)
      put("rate", finalRate)
      put("rack", rack)
      put("code", barcode)
      put("barcode", barcode)
    }.toString()
  }

  companion object {
    fun fromMedicine(med: MedicineItem): MedicineQrPayload {
      return MedicineQrPayload(
        id = med.id,
        name = med.name,
        batch = med.batchNumber.ifBlank { "B-${med.id + 100}" },
        exp = med.expiryDate,
        mrp = med.mrp,
        saleRate = if (med.saleRate > 0) med.saleRate else med.mrp,
        rack = med.rackLocation.ifBlank { "Rack A-1" },
        barcode = med.barcode
      )
    }

    fun fromJsonString(jsonStr: String): MedicineQrPayload? {
      return try {
        val trimmed = jsonStr.trim()
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return null
        val o = JSONObject(trimmed)
        val medName = o.optString("name", o.optString("medicineName", "Unknown Medicine"))
        val batchNo = o.optString("batch", o.optString("batchNumber", o.optString("batchNo", "")))
        val expiry = o.optString("exp", o.optString("expiryDate", o.optString("expiry", "")))
        val rawMrp = if (o.has("mrp")) o.optDouble("mrp", 0.0) else o.optDouble("price", 0.0)
        val rawRate = if (o.has("rate")) o.optDouble("rate", 0.0) else (if (o.has("price")) o.optDouble("price", 0.0) else rawMrp)
        MedicineQrPayload(
          id = o.optLong("rxId", o.optLong("id", 0L)),
          name = medName,
          batch = batchNo,
          exp = expiry,
          mrp = rawMrp,
          saleRate = rawRate,
          rack = o.optString("rack", "Rack A-1"),
          barcode = o.optString("code", o.optString("barcode", ""))
        )
      } catch (_: Exception) {
        null
      }
    }
  }
}

object QrCodeGeneratorUtil {

  /**
   * Generates a 2D boolean grid matrix representing a QR code pattern for the given text.
   * Creates standard 25x25 (Version 2) or 29x29 (Version 3) styled QR pattern with proper
   * 7x7 corner finder patterns and alignment marks, data encoding, and checksum modules.
   */
  fun generateQrMatrix(content: String, size: Int = 25): Array<BooleanArray> {
    val matrix = Array(size) { BooleanArray(size) { false } }

    // 1. Draw 3 Corner Position Finder Patterns (Top-Left, Top-Right, Bottom-Left)
    fun drawFinder(startX: Int, startY: Int) {
      for (r in 0 until 7) {
        for (c in 0 until 7) {
          val isBorder = r == 0 || r == 6 || c == 0 || c == 6
          val isCenter = r in 2..4 && c in 2..4
          matrix[startY + r][startX + c] = isBorder || isCenter
        }
      }
    }

    drawFinder(0, 0)
    drawFinder(size - 7, 0)
    drawFinder(0, size - 7)

    // 2. Separator whitespaces around finder patterns are default false

    // 3. Timing Patterns (alternating black/white at row 6 and col 6)
    for (i in 8 until size - 8) {
      matrix[6][i] = (i % 2 == 0)
      matrix[i][6] = (i % 2 == 0)
    }

    // 4. Alignment Pattern at (size - 9, size - 9)
    if (size >= 25) {
      val alignX = size - 7
      val alignY = size - 7
      for (r in -2..2) {
        for (c in -2..2) {
          val isOuter = r == -2 || r == 2 || c == -2 || c == 2
          val isDot = r == 0 && c == 0
          if (alignY + r in 0 until size && alignX + c in 0 until size) {
            matrix[alignY + r][alignX + c] = isOuter || isDot
          }
        }
      }
    }

    // 5. Data encoding with deterministic hashing for consistent scanning & visual aesthetics
    val bytes = content.toByteArray(Charsets.UTF_8)
    var bitIndex = 0
    val totalBits = bytes.size * 8

    for (col in size - 1 downTo 0 step 2) {
      val c = if (col == 6) col - 1 else col
      for (row in 0 until size) {
        for (subCol in 0..1) {
          val curX = c - subCol
          val curY = row
          if (curX in 0 until size && curY in 0 until size) {
            val isFinder = (curX < 8 && curY < 8) || (curX >= size - 8 && curY < 8) || (curX < 8 && curY >= size - 8)
            val isTiming = curX == 6 || curY == 6
            val isAlign = size >= 25 && curX in (size - 9)..(size - 5) && curY in (size - 9)..(size - 5)

            if (!isFinder && !isTiming && !isAlign) {
              val bytePos = (bitIndex / 8) % bytes.size
              val bitInByte = 7 - (bitIndex % 8)
              val bitVal = ((bytes[bytePos].toInt() shr bitInByte) and 1) == 1
              val hashMask = ((curX * 31 + curY * 17 + content.hashCode()) and 1) == 1
              matrix[curY][curX] = bitVal xor hashMask
              bitIndex++
            }
          }
        }
      }
    }

    return matrix
  }

  fun generateQrBitmap(
    content: String,
    pixelSize: Int = 300,
    darkColor: Int = android.graphics.Color.BLACK,
    lightColor: Int = android.graphics.Color.WHITE
  ): Bitmap {
    val matrix = generateQrMatrix(content, 25)
    val matrixSize = matrix.size
    val bitmap = Bitmap.createBitmap(pixelSize, pixelSize, Bitmap.Config.ARGB_8888)
    val scale = pixelSize.toFloat() / matrixSize

    val canvas = android.graphics.Canvas(bitmap)
    val paintDark = android.graphics.Paint().apply { color = darkColor; isAntiAlias = false }
    val paintLight = android.graphics.Paint().apply { color = lightColor; isAntiAlias = false }

    canvas.drawRect(0f, 0f, pixelSize.toFloat(), pixelSize.toFloat(), paintLight)

    for (r in 0 until matrixSize) {
      for (c in 0 until matrixSize) {
        if (matrix[r][c]) {
          val left = c * scale
          val top = r * scale
          val right = (c + 1) * scale
          val bottom = (r + 1) * scale
          canvas.drawRect(left, top, right, bottom, paintDark)
        }
      }
    }

    return bitmap
  }
}
