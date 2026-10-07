package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.CartItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DistributorExportService {
  private const val TAG = "DistributorExportService"

  /**
   * Generates a standard Excel/CSV compatible file (.xls / .csv) for distributor ordering / expiry return.
   */
  fun generateAndShareXls(
    context: Context,
    cartItems: List<CartItem>,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY"
  ) {
    try {
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val fileName = "Order_${distributorName.replace(" ", "_")}_$timeStamp.csv"
      val file = File(context.cacheDir, fileName)

      val writer = file.bufferedWriter()
      // CSV BOM for Excel UTF-8 compatibility
      writer.write("\uFEFF")
      writer.write("PHARMACY DISTRIBUTOR PURCHASE ORDER & EXPIRY RETURN DEBIT NOTE\n")
      writer.write("Pharmacy Name,$pharmacyName\n")
      writer.write("Distributor Name,$distributorName\n")
      writer.write("Generated Date,${SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())}\n\n")

      // Column Headers
      writer.write("S.No,Item Description,Manufacturer,Category,Batch No,Expiry Date,Packs/Qty,Unit Rate (INR),Total Amount (INR),Type / Reason\n")

      var grandTotal = 0.0
      cartItems.forEachIndexed { index, item ->
        val amount = item.totalAmount
        grandTotal += amount
        val safeName = "\"${item.medicineName.replace("\"", "\"\"")}\""
        val safeMfg = "\"${item.manufacturer.replace("\"", "\"\"")}\""
        val typeStr = if (item.itemType == "EXPIRY_RETURN") "EXPIRY RETURN (Short Expiry)" else "PURCHASE REORDER"

        writer.write("${index + 1},$safeName,$safeMfg,${item.category},${item.batchNumber.ifBlank { "N/A" }},${item.expiryDate.ifBlank { "N/A" }},${item.quantity},${String.format(Locale.US, "%.2f", item.unitRate)},${String.format(Locale.US, "%.2f", amount)},$typeStr\n")
      }

      writer.write("\n,,,,,,TOTAL ITEMS,${cartItems.size},GRAND TOTAL (INR),${String.format(Locale.US, "%.2f", grandTotal)}\n")
      writer.write("\nNote: Please deliver/credit according to state drug licensing and GST rules.\n")
      writer.flush()
      writer.close()

      shareFile(context, file, "text/csv", "Distributor Order / Return ($distributorName)")
    } catch (e: Exception) {
      Log.e(TAG, "Error generating XLS/CSV file: ${e.message}", e)
    }
  }

  /**
   * Generates a PDF invoice/order/return slip using Android PdfDocument and shares it.
   */
  fun generateAndSharePdf(
    context: Context,
    cartItems: List<CartItem>,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY",
    dlNumber: String = "AS-KAM-2024-00892",
    gstin: String = "18AABCR1234F1Z5"
  ) {
    try {
      val document = PdfDocument()
      val pageWidth = 595 // Standard A4 width in points
      val pageHeight = 842 // Standard A4 height in points
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
      val page = document.startPage(pageInfo)
      val canvas = page.canvas

      val paint = Paint().apply { isAntiAlias = true }
      var currentY = 40f

      // Top Header Banner
      paint.color = 0xFF9C1258.toInt() // Royal Magenta
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

      // Header Text
      paint.color = Color.WHITE
      paint.textSize = 20f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(pharmacyName, 30f, 40f, paint)

      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Retail & Clinical Pharmacy ERP • DL: $dlNumber • GSTIN: $gstin", 30f, 58f, paint)
      canvas.drawText("Official Purchase Order & Short Expiry Return Authorization", 30f, 74f, paint)

      currentY = 120f

      // Order Info Box
      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 12f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("To Distributor: $distributorName", 30f, currentY, paint)

      paint.color = 0xFF64748B.toInt()
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
      canvas.drawText("Date: $dateStr", pageWidth - 180f, currentY, paint)

      currentY += 24f

      // Table Header Background
      paint.color = 0xFFF1F5F9.toInt()
      canvas.drawRect(30f, currentY, pageWidth - 30f, currentY + 24f, paint)

      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Item / Salt", 38f, currentY + 16f, paint)
      canvas.drawText("Company", 195f, currentY + 16f, paint)
      canvas.drawText("Batch/Exp", 310f, currentY + 16f, paint)
      canvas.drawText("Qty", 400f, currentY + 16f, paint)
      canvas.drawText("Rate (₹)", 445f, currentY + 16f, paint)
      canvas.drawText("Total (₹)", 510f, currentY + 16f, paint)

      currentY += 28f

      var grandTotal = 0.0
      paint.typeface = Typeface.DEFAULT
      paint.textSize = 9f

      cartItems.take(22).forEachIndexed { index, item ->
        val amount = item.totalAmount
        grandTotal += amount

        if (index % 2 == 1) {
          paint.color = 0xFFF8FAFC.toInt()
          canvas.drawRect(30f, currentY - 12f, pageWidth - 30f, currentY + 10f, paint)
        }

        paint.color = 0xFF1E293B.toInt()
        val displayName = if (item.medicineName.length > 25) item.medicineName.take(24) + "…" else item.medicineName
        canvas.drawText(displayName, 38f, currentY, paint)
        canvas.drawText(item.manufacturer.take(18), 195f, currentY, paint)
        val batchExp = "${item.batchNumber.ifBlank { "-" }} / ${item.expiryDate.ifBlank { "-" }}"
        canvas.drawText(batchExp.take(15), 310f, currentY, paint)
        canvas.drawText("${item.quantity} pk", 400f, currentY, paint)
        canvas.drawText(String.format(Locale.US, "%.1f", item.unitRate), 445f, currentY, paint)
        canvas.drawText(String.format(Locale.US, "%.1f", amount), 510f, currentY, paint)

        currentY += 20f
      }

      // Divider line
      paint.color = 0xFFCBD5E1.toInt()
      canvas.drawLine(30f, currentY + 4f, pageWidth - 30f, currentY + 4f, paint)

      currentY += 25f

      // Grand Total Box
      paint.color = 0xFF9C1258.toInt()
      paint.textSize = 13f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Grand Total: ₹${String.format(Locale.US, "%.2f", grandTotal)}", pageWidth - 220f, currentY, paint)

      paint.color = 0xFF475569.toInt()
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Total Items: ${cartItems.size} line(s)", 38f, currentY, paint)

      currentY += 50f

      // Signatures
      paint.color = 0xFF64748B.toInt()
      paint.textSize = 9f
      canvas.drawLine(38f, currentY + 30f, 180f, currentY + 30f, paint)
      canvas.drawText("Distributor Received Signature", 38f, currentY + 44f, paint)

      canvas.drawLine(pageWidth - 180f, currentY + 30f, pageWidth - 38f, currentY + 30f, paint)
      canvas.drawText("Pharmacist In-Charge Stamp", pageWidth - 180f, currentY + 44f, paint)

      document.finishPage(page)

      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val fileName = "Order_${distributorName.replace(" ", "_")}_$timeStamp.pdf"
      val file = File(context.cacheDir, fileName)
      val fos = FileOutputStream(file)
      document.writeTo(fos)
      document.close()
      fos.close()

      shareFile(context, file, "application/pdf", "Order Document ($distributorName)")
    } catch (e: Exception) {
      Log.e(TAG, "Error generating PDF document: ${e.message}", e)
    }
  }

  private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
    try {
      val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "Please find attached $title from ROYAL PHARMACY.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      val chooser = Intent.createChooser(intent, "Send to Distributor via")
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(chooser)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to launch share sheet: ${e.message}", e)
    }
  }
}
