package com.example.service

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

/**
 * ImageOcrPreprocessor
 *
 * Performs local image pre-processing on medicine packaging photos before OCR and Gemini analysis:
 * 1. Grayscale & Luminance extraction
 * 2. High-contrast dynamic range stretching (enhances faint ink-jet / embossed dot matrix prints)
 * 3. 3x3 Edge Sharpening convolution (clarifies fine batch codes, expiry dates, and MRP stamps on blister foils)
 * 4. Image scaling for optimal neural/OCR reception
 */
object ImageOcrPreprocessor {

  /**
   * Prepares and enhances a bitmap for maximum OCR & Gemini Vision recognition accuracy.
   *
   * @param source The raw captured or selected packaging photo bitmap
   * @param contrastGain Contrast multiplier (default 1.45f)
   * @param brightnessOffset Brightness adjustment (default 5.0f)
   * @param applySharpening Whether to apply 3x3 sharpening convolution
   * @param applyAdaptiveThreshold Whether to apply local adaptive thresholding for text extraction
   * @param applyDeskew Whether to attempt automatic deskewing of text lines
   */
  fun preprocessForOcr(
    source: Bitmap,
    contrastGain: Float = 1.45f,
    brightnessOffset: Float = 5.0f,
    applySharpening: Boolean = true,
    applyAdaptiveThreshold: Boolean = true,
    applyDeskew: Boolean = true
  ): Bitmap {
    val width = source.width
    val height = source.height
    if (width <= 0 || height <= 0) return source

    // Step 1: Normalize dimension if overly huge
    val maxDim = 1400
    var workingBitmap = if (width > maxDim || height > maxDim) {
      val ratio = width.toFloat() / height.toFloat()
      val targetW = if (width > height) maxDim else (maxDim * ratio).toInt()
      val targetH = if (height > width) maxDim else (maxDim / ratio).toInt()
      Bitmap.createScaledBitmap(source, targetW.coerceAtLeast(100), targetH.coerceAtLeast(100), true)
    } else {
      source
    }

    // Step 2: Automatic Deskewing
    if (applyDeskew) {
      workingBitmap = deskewBitmap(workingBitmap)
    }

    // Step 3: Grayscale + High-Contrast Dynamic Range Stretching via ColorMatrix
    val contrastBitmap = Bitmap.createBitmap(
      workingBitmap.width,
      workingBitmap.height,
      Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(contrastBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val grayMatrix = ColorMatrix().apply { setSaturation(0f) }
    val scale = contrastGain
    val translate = (-0.5f * scale + 0.5f) * 255f + brightnessOffset

    val contrastMatrix = ColorMatrix(floatArrayOf(
      scale, 0f, 0f, 0f, translate,
      0f, scale, 0f, 0f, translate,
      0f, 0f, scale, 0f, translate,
      0f, 0f, 0f, 1f, 0f
    ))

    contrastMatrix.preConcat(grayMatrix)
    paint.colorFilter = ColorMatrixColorFilter(contrastMatrix)
    canvas.drawBitmap(workingBitmap, 0f, 0f, paint)

    // Step 4: Adaptive Thresholding (Local background normalization)
    var processed = if (applyAdaptiveThreshold) {
      applyLocalAdaptiveThreshold(contrastBitmap)
    } else {
      contrastBitmap
    }

    // Step 5: Fast 3x3 Edge Sharpening Convolution Filter
    if (applySharpening && processed.width > 10 && processed.height > 10) {
      processed = applyConvolutionSharpen(processed)
    }

    return processed
  }

  /**
   * Simple Adaptive Thresholding using a box-blur subtraction technique.
   * This enhances text against uneven lighting conditions common in pharmacy blister packs.
   */
  private fun applyLocalAdaptiveThreshold(src: Bitmap): Bitmap {
    val w = src.width
    val h = src.height
    val pixels = IntArray(w * h)
    src.getPixels(pixels, 0, w, 0, 0, w, h)

    val outputPixels = IntArray(w * h)
    val radius = 15 // Window size for local average
    
    // Grayscale values already present (R=G=B)
    val grayValues = IntArray(w * h) { i -> pixels[i] and 0xFF }

    for (y in 0 until h) {
      for (x in 0 until w) {
        // Calculate local average in a crude way for efficiency
        var sum = 0
        var count = 0
        val yMin = max(0, y - radius)
        val yMax = min(h - 1, y + radius)
        val xMin = max(0, x - radius)
        val xMax = min(w - 1, x + radius)
        
        // Sample few pixels around for local mean
        val step = 4
        for (ly in yMin..yMax step step) {
          for (lx in xMin..xMax step step) {
            sum += grayValues[ly * w + lx]
            count++
          }
        }
        
        val localMean = sum / count
        val current = grayValues[y * w + x]
        
        // If current pixel is significantly darker than local mean, make it black, else white
        val thresholded = if (current < localMean - 15) 0 else 255
        outputPixels[y * w + x] = (0xFF shl 24) or (thresholded shl 16) or (thresholded shl 8) or thresholded
      }
    }

    val result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    result.setPixels(outputPixels, 0, w, 0, 0, w, h)
    return result
  }

  /**
   * Detects skew angle by analyzing horizontal projection variance and rotates the bitmap.
   */
  private fun deskewBitmap(src: Bitmap): Bitmap {
    // For simplicity, we sample a smaller version to find angle
    val sampleW = 300
    val sampleH = (src.height.toFloat() / src.width * sampleW).toInt()
    val small = Bitmap.createScaledBitmap(src, sampleW, sampleH, false)
    
    val pixels = IntArray(sampleW * sampleH)
    small.getPixels(pixels, 0, sampleW, 0, 0, sampleW, sampleH)
    val gray = IntArray(sampleW * sampleH) { i ->
      val p = pixels[i]
      ((p shr 16 and 0xFF) * 0.299 + (p shr 8 and 0xFF) * 0.587 + (p and 0xFF) * 0.114).toInt()
    }

    var bestAngle = 0f
    var maxVariance = 0.0

    // Test angles from -15 to 15 degrees
    for (angle in -15..15 step 2) {
      val rad = Math.toRadians(angle.toDouble())
      val sin = Math.sin(rad)
      val cos = Math.cos(rad)
      
      val projections = DoubleArray(sampleH)
      val counts = IntArray(sampleH)

      for (y in 0 until sampleH) {
        for (x in 0 until sampleW) {
          val newY = (-(x - sampleW / 2) * sin + (y - sampleH / 2) * cos + sampleH / 2).toInt()
          if (newY in 0 until sampleH) {
            projections[newY] += gray[y * sampleW + x].toDouble()
            counts[newY]++
          }
        }
      }

      var mean = 0.0
      for (i in 0 until sampleH) {
        if (counts[i] > 0) projections[i] /= counts[i]
        mean += projections[i]
      }
      mean /= sampleH

      var variance = 0.0
      for (i in 0 until sampleH) {
        variance += (projections[i] - mean) * (projections[i] - mean)
      }

      if (variance > maxVariance) {
        maxVariance = variance
        bestAngle = angle.toFloat()
      }
    }

    if (Math.abs(bestAngle) < 1f) return src

    val matrix = android.graphics.Matrix()
    matrix.postRotate(-bestAngle)
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
  }

  /**
   * Applies an unsharp mask / 3x3 sharpening kernel:
   *  [  0, -1,  0 ]
   *  [ -1,  5, -1 ]
   *  [  0, -1,  0 ]
   */
  private fun applyConvolutionSharpen(src: Bitmap): Bitmap {
    val w = src.width
    val h = src.height
    val pixels = IntArray(w * h)
    src.getPixels(pixels, 0, w, 0, 0, w, h)

    val outputPixels = IntArray(w * h)

    // Copy borders
    for (x in 0 until w) {
      outputPixels[x] = pixels[x]
      outputPixels[(h - 1) * w + x] = pixels[(h - 1) * w + x]
    }
    for (y in 0 until h) {
      outputPixels[y * w] = pixels[y * w]
      outputPixels[y * w + (w - 1)] = pixels[y * w + (w - 1)]
    }

    // Apply convolution to inner area
    for (y in 1 until h - 1) {
      val rowAbove = (y - 1) * w
      val rowCenter = y * w
      val rowBelow = (y + 1) * w

      for (x in 1 until w - 1) {
        val pCenter = pixels[rowCenter + x]
        val pTop = pixels[rowAbove + x]
        val pBottom = pixels[rowBelow + x]
        val pLeft = pixels[rowCenter + x - 1]
        val pRight = pixels[rowCenter + x + 1]

        // Extract grayscale value (since image is already gray, R=G=B)
        val valCenter = pCenter and 0xFF
        val valTop = pTop and 0xFF
        val valBottom = pBottom and 0xFF
        val valLeft = pLeft and 0xFF
        val valRight = pRight and 0xFF

        val sharpenedVal = (5 * valCenter - valTop - valBottom - valLeft - valRight).coerceIn(0, 255)
        outputPixels[rowCenter + x] = (0xFF shl 24) or (sharpenedVal shl 16) or (sharpenedVal shl 8) or sharpenedVal
      }
    }

    val sharpenedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    sharpenedBitmap.setPixels(outputPixels, 0, w, 0, 0, w, h)
    return sharpenedBitmap
  }

  /**
   * Converts a bitmap into an enhanced high-contrast base64 JPEG string for Gemini Vision payload.
   */
  suspend fun toEnhancedBase64(bitmap: Bitmap): String = withContext(Dispatchers.Default) {
    val enhanced = preprocessForOcr(bitmap)
    val outputStream = ByteArrayOutputStream()
    enhanced.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  /**
   * Batch pre-processes multiple angle photos.
   */
  suspend fun preprocessMultiple(bitmaps: List<Bitmap>): List<Bitmap> = withContext(Dispatchers.Default) {
    bitmaps.map { preprocessForOcr(it) }
  }
}
