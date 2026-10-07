package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BusinessProfile
import com.example.data.model.SaleInvoice
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import androidx.compose.ui.platform.LocalContext
import com.example.service.InvoicePrintOptions
import com.example.service.InvoicePrinterService
import java.util.Locale

@Composable
fun InvoiceReceiptDialog(
  invoice: SaleInvoice,
  profile: BusinessProfile,
  onDismiss: () -> Unit,
  onOpenPrinterStation: (() -> Unit)? = null
) {
  val context = LocalContext.current
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 24.dp)
        .testTag("dialog_invoice_receipt")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top action bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tax Invoice / Cash Memo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pharmacy Header Box
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = profile.businessName,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = profile.addressLine1,
            fontSize = 11.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
          )
          Text(
            text = "Phone: ${profile.phone} • Email: ${profile.email}",
            fontSize = 10.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("DL Form 20/21: ${profile.drugLicenseForm20.ifBlank { "DL-784146" }}", fontSize = 9.sp, color = TextMuted)
            Text("GSTIN: ${profile.gstin}", fontSize = 9.sp, color = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Invoice Meta
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("Bill To: ${invoice.customerName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            if (invoice.customerPhone.isNotBlank()) {
              Text("Phone: ${invoice.customerPhone}", fontSize = 11.sp, color = TextMuted)
            }
            if (invoice.doctorName.isNotBlank()) {
              Text("Prescribed By: ${invoice.doctorName}", fontSize = 11.sp, color = TextMuted)
            }
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Inv No: ${invoice.invoiceNumber}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Date: ${invoice.invoiceDate}", fontSize = 11.sp, color = TextMuted)
            Text("Type: ${invoice.saleType}", fontSize = 11.sp, color = RoyalMagenta)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
        Spacer(modifier = Modifier.height(10.dp))

        // Items Breakdown
        Text("Dispensed Medicines:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = invoice.itemsJson,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          color = TextDark,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
        Spacer(modifier = Modifier.height(8.dp))

        // Totals
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Subtotal", fontSize = 12.sp, color = TextMuted)
          Text(String.format(Locale.getDefault(), "₹%.2f", invoice.subtotal), fontSize = 12.sp, color = TextDark)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("GST (CGST + SGST)", fontSize = 12.sp, color = TextMuted)
          Text(String.format(Locale.getDefault(), "₹%.2f", invoice.gstTotal), fontSize = 12.sp, color = TextDark)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text("Payment Mode", fontSize = 12.sp, color = TextMuted)
          Text(invoice.paymentMode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("GRAND TOTAL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
          Text(
            String.format(Locale.getDefault(), "₹%.2f", invoice.grandTotal),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RoyalMagenta
          )
        }

        if (invoice.loyaltyPointsEarned > 0 || invoice.loyaltyPointsRedeemed > 0) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFFEF3C7))
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "⭐ Loyalty Rewards Program:",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF92400E)
            )
            Text(
              text = buildString {
                if (invoice.loyaltyPointsRedeemed > 0) append("Redeemed: -${invoice.loyaltyPointsRedeemed}  ")
                if (invoice.loyaltyPointsEarned > 0) append("Earned: +${invoice.loyaltyPointsEarned} pts")
              },
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFB45309)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Pharmacist Signature: Suleman Hoque (Reg: 4146-AS)\n*Medicines once sold will not be returned without cash memo*",
          fontSize = 9.sp,
          color = TextMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                InvoicePrinterService.shareInvoiceOnWhatsApp(context, invoice, profile)
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("WhatsApp", color = RoyalNavy, fontSize = 11.5.sp)
            }

            OutlinedButton(
              onClick = {
                InvoicePrinterService.generateAndSharePdfInvoice(context, invoice, profile, InvoicePrintOptions())
              },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Export PDF", color = RoyalMagenta, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                InvoicePrinterService.printInvoiceViaSystem(context, invoice, profile, InvoicePrintOptions())
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
              modifier = Modifier.weight(1f)
            ) {
              Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Print", color = Color.White, fontSize = 11.5.sp)
            }
          }

          if (onOpenPrinterStation != null) {
            Button(
              onClick = {
                onDismiss()
                onOpenPrinterStation()
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
              modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
              Text("Open ESC/POS Thermal & A4 Print Station", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
