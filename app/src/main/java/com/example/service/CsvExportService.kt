package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.MedicineItem
import com.example.data.model.SaleInvoice
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExportService {

  fun exportInventoryToCsv(context: Context, medicines: List<MedicineItem>) {
    val header = "ID,Name,Manufacturer,Composition,Batch,Expiry,Stock,MRP,PurchaseRate,SaleRate,Rack\n"
    val data = StringBuilder(header)
    
    medicines.forEach { med ->
      data.append("${med.id},")
      data.append("\"${med.name}\",")
      data.append("\"${med.manufacturer}\",")
      data.append("\"${med.composition}\",")
      data.append("${med.batchNumber},")
      data.append("${med.expiryDate},")
      data.append("${med.stockPacks},")
      data.append("${med.mrp},")
      data.append("${med.purchaseRate},")
      data.append("${med.saleRate},")
      data.append("\"${med.rackLocation}\"\n")
    }
    
    shareCsv(context, data.toString(), "Inventory_Export")
  }

  fun exportSalesToCsv(context: Context, sales: List<SaleInvoice>) {
    val header = "InvoiceNo,Date,Customer,Phone,Doctor,Items,Total,PaymentMode,Status\n"
    val data = StringBuilder(header)
    
    sales.forEach { sale ->
      data.append("${sale.invoiceNumber},")
      data.append("${sale.invoiceDate},")
      data.append("\"${sale.customerName}\",")
      data.append("${sale.customerPhone},")
      data.append("\"${sale.doctorName}\",")
      data.append("\"${sale.itemsJson.replace("\"", "'")}\",")
      data.append("${sale.grandTotal},")
      data.append("${sale.paymentMode},")
      data.append("${if (sale.isPaid) "Paid" else "Unpaid"}\n")
    }
    
    shareCsv(context, data.toString(), "Sales_Export")
  }

  private fun shareCsv(context: Context, content: String, prefix: String) {
    try {
      val fileName = "${prefix}_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.csv"
      val file = File(context.cacheDir, fileName)
      
      FileOutputStream(file).use { out ->
        out.write(content.toByteArray(Charsets.UTF_8))
      }
      
      val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "$prefix - Royal Pharmacy")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      
      context.startActivity(Intent.createChooser(intent, "Export CSV Report"))
    } catch (e: Exception) {
      Toast.makeText(context, "CSV Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
  }
}
