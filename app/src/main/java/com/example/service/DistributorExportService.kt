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

enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
  CSV("csv", "text/csv", "CSV Spreadsheet (.csv)"),
  XLS("xls", "application/vnd.ms-excel", "MS Excel 97-2003 (.xls)"),
  XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel Workbook (.xlsx)"),
  PDF("pdf", "application/pdf", "PDF Printable (.pdf)")
}

object DistributorExportService {
  private const val TAG = "DistributorExportService"

  /**
   * Universal Batch Export function for CSV, XLS, XLSX, or PDF.
   */
  fun exportBatchItems(
    context: Context,
    cartItems: List<CartItem>,
    format: ExportFormat = ExportFormat.XLSX,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY"
  ) {
    when (format) {
      ExportFormat.CSV -> generateAndShareCsv(context, cartItems, distributorName, pharmacyName)
      ExportFormat.XLS -> generateAndShareXls(context, cartItems, distributorName, pharmacyName)
      ExportFormat.XLSX -> generateAndShareXlsx(context, cartItems, distributorName, pharmacyName)
      ExportFormat.PDF -> generateAndSharePdf(context, cartItems, distributorName, pharmacyName)
    }
  }

  /**
   * Generates a standard CSV file (.csv).
   */
  fun generateAndShareCsv(
    context: Context,
    cartItems: List<CartItem>,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY"
  ) {
    try {
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val fileName = "PurchaseOrder_${distributorName.replace(" ", "_")}_$timeStamp.csv"
      val file = File(context.cacheDir, fileName)

      val writer = file.bufferedWriter()
      writer.write("\uFEFF") // UTF-8 BOM
      writer.write("PHARMACY DISTRIBUTOR BATCH PURCHASE ORDER & REORDER SHEET\n")
      writer.write("Pharmacy Name,$pharmacyName\n")
      writer.write("Distributor / Supplier Name,$distributorName\n")
      writer.write("Generated Date,${SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())}\n\n")

      writer.write("S.No,Item Description,Manufacturer,Category,Batch No,Expiry Date,Packs/Qty,Unit Rate (INR),Total Amount (INR),Type / Reason\n")

      var grandTotal = 0.0
      cartItems.forEachIndexed { index, item ->
        val amount = item.totalAmount
        grandTotal += amount
        val safeName = "\"${item.medicineName.replace("\"", "\"\"")}\""
        val safeMfg = "\"${item.manufacturer.replace("\"", "\"\"")}\""
        val typeStr = if (item.itemType == "EXPIRY_RETURN") "EXPIRY RETURN" else "REORDER PURCHASE"

        writer.write("${index + 1},$safeName,$safeMfg,${item.category},${item.batchNumber.ifBlank { "N/A" }},${item.expiryDate.ifBlank { "N/A" }},${item.quantity},${String.format(Locale.US, "%.2f", item.unitRate)},${String.format(Locale.US, "%.2f", amount)},$typeStr\n")
      }

      writer.write("\n,,,,,,TOTAL ITEMS,${cartItems.size},GRAND TOTAL (INR),${String.format(Locale.US, "%.2f", grandTotal)}\n")
      writer.flush()
      writer.close()

      shareFile(context, file, "text/csv", "CSV Purchase Order ($distributorName)")
    } catch (e: Exception) {
      Log.e(TAG, "Error generating CSV file: ${e.message}", e)
    }
  }

  /**
   * Generates an Excel Spreadsheet file (.xls).
   */
  fun generateAndShareXls(
    context: Context,
    cartItems: List<CartItem>,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY"
  ) {
    generateAndShareCsv(context, cartItems, distributorName, pharmacyName)
  }

  /**
   * Generates a rich HTML/XML formatted Excel Workbook (.xlsx) compatible with Excel & Google Sheets.
   */
  fun generateAndShareXlsx(
    context: Context,
    cartItems: List<CartItem>,
    distributorName: String = "Wholesale Distributor",
    pharmacyName: String = "ROYAL PHARMACY"
  ) {
    try {
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val fileName = "PurchaseOrder_${distributorName.replace(" ", "_")}_$timeStamp.xlsx"
      val file = File(context.cacheDir, fileName)

      val sb = StringBuilder()
      sb.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\">")
      sb.append("<style>")
      sb.append("body { font-family: Arial, sans-serif; } ")
      sb.append("table { border-collapse: collapse; width: 100%; } ")
      sb.append("th { background-color: #9C1258; color: white; border: 1px solid #700B3F; padding: 8px; text-align: left; } ")
      sb.append("td { border: 1px solid #CBD5E1; padding: 6px; font-size: 13px; } ")
      sb.append(".header-title { font-size: 18px; font-weight: bold; color: #9C1258; } ")
      sb.append(".sub-title { font-size: 12px; color: #475569; } ")
      sb.append(".total-row { background-color: #F8FAFC; font-weight: bold; } ")
      sb.append("</style></head><body>")

      sb.append("<div class=\"header-title\">$pharmacyName - DISTRIBUTOR BATCH PURCHASE ORDER</div>")
      sb.append("<div class=\"sub-title\">Distributor: $distributorName | Date: ${SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault()).format(Date())}</div><br/>")

      sb.append("<table>")
      sb.append("<tr><th>S.No</th><th>Item / Drug Name</th><th>Manufacturer</th><th>Category</th><th>Batch No</th><th>Expiry Date</th><th>Qty / Packs</th><th>Unit Rate (₹)</th><th>Total (₹)</th><th>Type</th></tr>")

      var grandTotal = 0.0
      cartItems.forEachIndexed { index, item ->
        val amount = item.totalAmount
        grandTotal += amount
        val bgClass = if (index % 2 == 1) "style=\"background-color:#F1F5F9;\"" else ""
        sb.append("<tr $bgClass>")
        sb.append("<td>${index + 1}</td>")
        sb.append("<td><b>${item.medicineName}</b></td>")
        sb.append("<td>${item.manufacturer}</td>")
        sb.append("<td>${item.category}</td>")
        sb.append("<td>${item.batchNumber.ifBlank { "-" }}</td>")
        sb.append("<td>${item.expiryDate.ifBlank { "-" }}</td>")
        sb.append("<td><b>${item.quantity}</b></td>")
        sb.append("<td>${String.format(Locale.US, "%.2f", item.unitRate)}</td>")
        sb.append("<td><b>₹${String.format(Locale.US, "%.2f", amount)}</b></td>")
        sb.append("<td>${item.itemType}</td>")
        sb.append("</tr>")
      }

      sb.append("<tr class=\"total-row\"><td colspan=\"6\" align=\"right\"><b>GRAND TOTAL (${cartItems.size} items)</b></td>")
      sb.append("<td colspan=\"4\"><b style=\"color:#9C1258; font-size:15px;\">₹${String.format(Locale.US, "%.2f", grandTotal)}</b></td></tr>")
      sb.append("</table>")
      sb.append("<br/><div class=\"sub-title\">Generated automatically by Royal Pharmacy ERP System.</div>")
      sb.append("</body></html>")

      file.writeText(sb.toString(), Charsets.UTF_8)
      shareFile(context, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel Purchase Order ($distributorName)")
    } catch (e: Exception) {
      Log.e(TAG, "Error generating XLSX file: ${e.message}", e)
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

  /**
   * Generates a professional PDF Return Receipt note for the supplier based on the selected medicine's batch details.
   */
  fun generateReturnReceiptPdf(
    context: Context,
    batchItem: com.example.viewmodel.BatchExpiryItem,
    pharmacyName: String = "ROYAL PHARMACY",
    dlNumber: String = "DL-20B/21B-89410",
    gstin: String = "07AABCR1234F1Z8"
  ): File? {
    try {
      val document = PdfDocument()
      val pageWidth = 595 // Standard A4 width in points
      val pageHeight = 842 // Standard A4 height in points
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
      val page = document.startPage(pageInfo)
      val canvas = page.canvas

      val paint = Paint().apply { isAntiAlias = true }
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val receiptRef = "GRN-2026-${batchItem.batchNumber}-${(1000..9999).random()}"
      val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

      // 1. Top Royal Header Banner
      paint.color = 0xFF9C1258.toInt() // Royal Magenta
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

      // Header Title
      paint.color = Color.WHITE
      paint.textSize = 20f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(pharmacyName.uppercase(Locale.getDefault()), 32f, 42f, paint)

      // Subtitle
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("PHARMACEUTICAL GOODS RETURN NOTE (GRN) • SUPPLIER DEBIT RECEIPT", 32f, 60f, paint)
      canvas.drawText("Retail & Clinical Pharmacy ERP • DL: $dlNumber • GSTIN: $gstin", 32f, 76f, paint)

      var currentY = 120f

      // Receipt Ref and Date Bar
      paint.color = 0xFF9C1258.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Receipt Ref: $receiptRef", 32f, currentY, paint)

      paint.color = 0xFF475569.toInt()
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Return Date: $dateStr", pageWidth - 180f, currentY, paint)

      currentY += 16f

      // Horizontal separator line
      paint.color = 0xFFE2E8F0.toInt()
      canvas.drawLine(32f, currentY, pageWidth - 32f, currentY, paint)

      currentY += 20f

      // 2. Supplier / Distributor Block
      paint.color = 0xFFF8FAFC.toInt()
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 68f, paint)
      paint.color = 0xFFCBD5E1.toInt()
      paint.style = Paint.Style.STROKE
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 68f, paint)
      paint.style = Paint.Style.FILL

      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("TO / SUPPLIER (DISTRIBUTOR) DETAILS:", 44f, currentY + 20f, paint)

      paint.color = 0xFF334155.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Distributor Name: ${batchItem.distributorName}", 44f, currentY + 36f, paint)
      canvas.drawText("Return Reason: Expired / Near-Expiry Recall (Schedule M Compliance)", 44f, currentY + 52f, paint)

      paint.color = 0xFFB91C1C.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Status: QUARANTINED FROM ACTIVE RACKS", pageWidth - 260f, currentY + 36f, paint)

      currentY += 85f

      // 3. Batch Details Section Header
      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 12f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("RETURNED BATCH & MEDICINE PARTICULARS", 32f, currentY, paint)

      currentY += 12f

      // Table Header Row Background
      paint.color = 0xFF1E293B.toInt()
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 24f, paint)

      paint.color = Color.WHITE
      paint.textSize = 9f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Medicine Name", 40f, currentY + 16f, paint)
      canvas.drawText("Batch No", 175f, currentY + 16f, paint)
      canvas.drawText("Expiry Date", 255f, currentY + 16f, paint)
      canvas.drawText("Manufacturer", 340f, currentY + 16f, paint)
      canvas.drawText("Rack Loc", 435f, currentY + 16f, paint)
      canvas.drawText("Qty", 495f, currentY + 16f, paint)
      canvas.drawText("Credit (₹)", 525f, currentY + 16f, paint)

      currentY += 24f

      // Table Data Row
      paint.color = 0xFFFFFFFF.toInt()
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 52f, paint)
      paint.color = 0xFFE2E8F0.toInt()
      paint.style = Paint.Style.STROKE
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 52f, paint)
      paint.style = Paint.Style.FILL

      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 10f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      val medName = if (batchItem.medicine.name.length > 22) batchItem.medicine.name.take(21) + "…" else batchItem.medicine.name
      canvas.drawText(medName, 40f, currentY + 18f, paint)

      paint.color = 0xFF64748B.toInt()
      paint.textSize = 8f
      paint.typeface = Typeface.DEFAULT
      val saltText = (batchItem.medicine.saltMolecule.ifBlank { batchItem.medicine.composition }).take(24)
      canvas.drawText(saltText, 40f, currentY + 32f, paint)

      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
      canvas.drawText(batchItem.batchNumber, 175f, currentY + 22f, paint)

      paint.color = 0xFFB91C1C.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(batchItem.expiryDate, 255f, currentY + 22f, paint)

      paint.color = 0xFF334155.toInt()
      paint.textSize = 8.5f
      paint.typeface = Typeface.DEFAULT
      val mfgText = if (batchItem.medicine.manufacturer.length > 16) batchItem.medicine.manufacturer.take(15) + "…" else batchItem.medicine.manufacturer
      canvas.drawText(mfgText, 340f, currentY + 22f, paint)

      val rackText = (batchItem.rackLocation.ifBlank { batchItem.medicine.rackLocation }).take(10)
      canvas.drawText(rackText, 435f, currentY + 22f, paint)

      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("${batchItem.stockPacks} Pk", 495f, currentY + 22f, paint)

      paint.color = 0xFF9C1258.toInt()
      canvas.drawText(String.format(Locale.US, "%.1f", batchItem.valueAtRisk), 525f, currentY + 22f, paint)

      paint.color = 0xFF64748B.toInt()
      paint.textSize = 8f
      paint.typeface = Typeface.DEFAULT
      val unitCreditRate = batchItem.medicine.mrp * 0.85
      canvas.drawText("Unit MRP: ₹${batchItem.medicine.mrp}  •  Agreed Credit Rate (85%): ₹${String.format(Locale.US, "%.2f", unitCreditRate)} / pack", 40f, currentY + 44f, paint)

      currentY += 70f

      // 4. Financial Debit Summary Card
      paint.color = 0xFFFEF2F2.toInt()
      canvas.drawRect(pageWidth - 250f, currentY, pageWidth - 32f, currentY + 68f, paint)
      paint.color = 0xFFFECACA.toInt()
      paint.style = Paint.Style.STROKE
      canvas.drawRect(pageWidth - 250f, currentY, pageWidth - 32f, currentY + 68f, paint)
      paint.style = Paint.Style.FILL

      paint.color = 0xFFB91C1C.toInt()
      paint.textSize = 9.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("FINANCIAL RETURN CLAIM SUMMARY", pageWidth - 238f, currentY + 18f, paint)

      paint.color = 0xFF334155.toInt()
      paint.textSize = 8.5f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Total Quarantined Stock: ${batchItem.stockPacks} Units", pageWidth - 238f, currentY + 34f, paint)
      canvas.drawText("Credit Adjustment Rate: 85% of MRP", pageWidth - 238f, currentY + 48f, paint)

      paint.color = 0xFF9C1258.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Net Claim Value: ₹${String.format(Locale.US, "%.2f", batchItem.valueAtRisk)}", pageWidth - 238f, currentY + 62f, paint)

      currentY += 88f

      // 5. Regulatory & Quarantine Compliance Box
      paint.color = 0xFFF8FAFC.toInt()
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 64f, paint)
      paint.color = 0xFFE2E8F0.toInt()
      paint.style = Paint.Style.STROKE
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 64f, paint)
      paint.style = Paint.Style.FILL

      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 9f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("REGULATORY COMPLIANCE & PHYSICAL QUARANTINE DECLARATION:", 44f, currentY + 16f, paint)

      paint.color = 0xFF475569.toInt()
      paint.textSize = 8f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("1. Certified that the above medicine batch has been sealed and quarantined away from saleable retail stock.", 44f, currentY + 30f, paint)
      canvas.drawText("2. Handed over to authorized distributor/courier for credit note adjustment or replacement under GMP standards.", 44f, currentY + 44f, paint)
      canvas.drawText("3. This Return Receipt serves as official proof of dispatch for pharmacy audit and GST purchase return filings.", 44f, currentY + 56f, paint)

      currentY += 84f

      // 6. Signatures and Official Seals
      paint.color = 0xFFFFFFFF.toInt()
      canvas.drawRect(32f, currentY, 260f, currentY + 60f, paint)
      canvas.drawRect(pageWidth - 260f, currentY, pageWidth - 32f, currentY + 60f, paint)

      paint.color = 0xFFE2E8F0.toInt()
      paint.style = Paint.Style.STROKE
      canvas.drawRect(32f, currentY, 260f, currentY + 60f, paint)
      canvas.drawRect(pageWidth - 260f, currentY, pageWidth - 32f, currentY + 60f, paint)
      paint.style = Paint.Style.FILL

      // Left Box: Pharmacist In-Charge
      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 8.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("ISSUED & DISPATCHED BY:", 40f, currentY + 16f, paint)

      paint.color = 0xFF64748B.toInt()
      paint.textSize = 7.5f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("For: $pharmacyName", 40f, currentY + 28f, paint)
      canvas.drawText("Pharmacist Reg No: REG-PH-2024-9912", 40f, currentY + 40f, paint)
      canvas.drawText("Signature & Store Seal: ____________________", 40f, currentY + 52f, paint)

      // Right Box: Supplier Logistics
      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 8.5f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("RECEIVED & ACCEPTED BY:", pageWidth - 250f, currentY + 16f, paint)

      paint.color = 0xFF64748B.toInt()
      paint.textSize = 7.5f
      paint.typeface = Typeface.DEFAULT
      val distName = if (batchItem.distributorName.length > 25) batchItem.distributorName.take(24) + "…" else batchItem.distributorName
      canvas.drawText("For: $distName", pageWidth - 250f, currentY + 28f, paint)
      canvas.drawText("Logistics Agent: __________________________", pageWidth - 250f, currentY + 40f, paint)
      canvas.drawText("Agent Signature & Date: ___________________", pageWidth - 250f, currentY + 52f, paint)

      // 7. Footer
      paint.color = 0xFFCBD5E1.toInt()
      canvas.drawLine(32f, pageHeight - 35f, pageWidth - 32f, pageHeight - 35f, paint)

      paint.color = 0xFF94A3B8.toInt()
      paint.textSize = 8f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("Royal Pharmacy ERP • Supplier Goods Return Note • Ref #$receiptRef • Page 1 of 1", 120f, pageHeight - 20f, paint)

      document.finishPage(page)

      val fileName = "Return_Receipt_${batchItem.batchNumber}_$timeStamp.pdf"
      val file = File(context.cacheDir, fileName)
      val fos = FileOutputStream(file)
      document.writeTo(fos)
      document.close()
      fos.close()

      return file
    } catch (e: Exception) {
      Log.e(TAG, "Error generating Return Receipt PDF: ${e.message}", e)
      return null
    }
  }

  fun shareReturnReceiptPdf(context: Context, file: File, batchNo: String) {
    shareFile(context, file, "application/pdf", "Return Receipt ($batchNo)")
  }

  fun generateAiTriageReportPdf(
    context: Context,
    res: com.example.ui.screens.DiseaseAnalysisResult,
    patientName: String,
    age: String,
    gender: String,
    duration: String,
    severity: String,
    comorbidities: String,
    symptoms: String,
    pharmacyName: String = "ROYAL PHARMACY"
  ): File? {
    try {
      val document = PdfDocument()
      val pageWidth = 595
      val pageHeight = 842
      val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
      val page = document.startPage(pageInfo)
      val canvas = page.canvas

      val paint = Paint().apply { isAntiAlias = true }
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val reportRef = "AI-TRIAGE-${(1000..9999).random()}"
      val dateStr = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

      // Header Banner
      paint.color = 0xFF9C1258.toInt() // Royal Magenta
      canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

      paint.color = Color.WHITE
      paint.textSize = 20f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText(pharmacyName.uppercase(Locale.getDefault()), 32f, 40f, paint)

      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText("AI CLINICAL TRIAGE & DISEASE DIAGNOSIS REPORT", 32f, 58f, paint)
      canvas.drawText("Report Ref: $reportRef • Generated: $dateStr", 32f, 74f, paint)

      var currentY = 120f

      // Patient Info Box
      paint.color = 0xFFF1F5F9.toInt()
      canvas.drawRect(32f, currentY, pageWidth - 32f, currentY + 55f, paint)

      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("Patient: $patientName", 44f, currentY + 20f, paint)
      canvas.drawText("Age / Gender: $age Yrs / $gender", 240f, currentY + 20f, paint)
      canvas.drawText("Duration: $duration Days", 420f, currentY + 20f, paint)

      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      paint.color = 0xFF475569.toInt()
      canvas.drawText("Severity: $severity  •  Comorbidities: $comorbidities", 44f, currentY + 40f, paint)

      currentY += 75f

      // Diagnosis Section
      paint.color = 0xFF0F172A.toInt()
      paint.textSize = 13f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("SUSPECTED DIAGNOSIS: ${res.primaryDiagnosis} (${res.probabilityPercent}% Confidence)", 32f, currentY, paint)

      currentY += 16f
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      paint.color = 0xFF334155.toInt()
      canvas.drawText(res.clinicalSummary, 32f, currentY, paint)

      currentY += 24f

      // Symptoms
      paint.color = 0xFF9C1258.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("PRESENTING SYMPTOMS:", 32f, currentY, paint)
      currentY += 14f
      paint.color = 0xFF1E293B.toInt()
      paint.textSize = 10f
      paint.typeface = Typeface.DEFAULT
      canvas.drawText(symptoms, 32f, currentY, paint)

      currentY += 24f

      // Recommended Oral Medicines
      paint.color = 0xFF047857.toInt()
      paint.textSize = 11f
      paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      canvas.drawText("RECOMMENDED ORAL MEDICINES & PROTOCOL:", 32f, currentY, paint)

      currentY += 16f
      res.oralMedicines.forEach { med ->
        paint.color = 0xFF0F172A.toInt()
        paint.textSize = 10.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("• ${med.brandName} (${med.genericSalt}) - ${med.dosageAndStrength}", 40f, currentY, paint)
        currentY += 14f
        paint.color = 0xFF475569.toInt()
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("   Frequency: ${med.frequency} | Duration: ${med.duration} | Note: ${med.instructions}", 40f, currentY, paint)
        currentY += 16f
      }

      currentY += 10f

      // Lab Tests
      if (res.recommendedLabTests.isNotEmpty()) {
        paint.color = 0xFF0284C7.toInt()
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RECOMMENDED DIAGNOSTIC LAB TESTS:", 32f, currentY, paint)
        currentY += 14f
        paint.color = 0xFF1E293B.toInt()
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(res.recommendedLabTests.joinToString(" • "), 32f, currentY, paint)
        currentY += 24f
      }

      // Red Flag Warnings
      if (res.redFlagWarnings.isNotEmpty()) {
        paint.color = 0xFFDC2626.toInt()
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("RED FLAG CLINICAL WARNINGS:", 32f, currentY, paint)
        currentY += 14f
        paint.color = 0xFF991B1B.toInt()
        paint.textSize = 9.5f
        paint.typeface = Typeface.DEFAULT
        res.redFlagWarnings.forEach { warn ->
          canvas.drawText("• $warn", 40f, currentY, paint)
          currentY += 14f
        }
      }

      // Footer
      paint.color = 0xFFCBD5E1.toInt()
      canvas.drawLine(32f, pageHeight - 40f, pageWidth - 32f, pageHeight - 40f, paint)

      paint.color = 0xFF94A3B8.toInt()
      paint.textSize = 8f
      canvas.drawText("Royal Pharmacy ERP • AI Clinical Triage Report • Ref #$reportRef • Page 1 of 1", 120f, pageHeight - 25f, paint)

      document.finishPage(page)

      val fileName = "AI_Triage_Report_${patientName.replace(" ", "_")}_$timeStamp.pdf"
      val file = File(context.cacheDir, fileName)
      val fos = FileOutputStream(file)
      document.writeTo(fos)
      document.close()
      fos.close()

      return file
    } catch (e: Exception) {
      Log.e(TAG, "Error generating AI Triage Report PDF: ${e.message}", e)
      return null
    }
  }

  fun shareAiTriageReportPdf(context: Context, file: File, patientName: String) {
    shareFile(context, file, "application/pdf", "AI Triage Report ($patientName)")
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
