package com.example.util

import android.content.Context
import android.net.Uri
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

data class MedicineUpdate(
    val name: String,
    val mrp: Double? = null,
    val purchaseRate: Double? = null,
    val saleRate: Double? = null
)

object ExcelImportUtil {
    fun parseMedicineExcel(context: Context, uri: Uri): List<MedicineUpdate> {
        val updates = mutableListOf<MedicineUpdate>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0) ?: return emptyList()
                
                // Assume Header in Row 0: 
                // Col 0: Name, Col 1: MRP, Col 2: Purchase Price (Purchase Rate), Col 3: Sale Price (Sale Rate)
                
                for (i in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(i) ?: continue
                    
                    val nameCell = row.getCell(0) ?: continue
                    val name = when (nameCell.cellType) {
                        CellType.STRING -> nameCell.stringCellValue
                        CellType.NUMERIC -> nameCell.numericCellValue.toString()
                        else -> null
                    } ?: continue

                    if (name.isBlank()) continue

                    val mrp = try { row.getCell(1)?.numericCellValue } catch (_: Exception) { null }
                    val purchase = try { row.getCell(2)?.numericCellValue } catch (_: Exception) { null }
                    val sale = try { row.getCell(3)?.numericCellValue } catch (_: Exception) { null }
                    
                    updates.add(MedicineUpdate(name, mrp, purchase, sale))
                }
                workbook.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return updates
    }
}
