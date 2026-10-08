package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.Locale
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.util.QrCodeGeneratorUtil

@Composable
fun StoreUpiQrDialog(
  upiId: String,
  storeName: String,
  initialAmount: Double = 0.0,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  var billAmount by remember { mutableStateOf(if (initialAmount > 0) String.format(Locale.getDefault(), "%.2f", initialAmount) else "") }

  // UPI deep link standard: upi://pay?pa=<upi_id>&pn=<name>&am=<amt>&cu=INR
  val upiUrl = remember(upiId, storeName, billAmount) {
    val encodedName = java.net.URLEncoder.encode(storeName, "UTF-8")
    val amtParam = if (billAmount.isNotBlank() && (billAmount.toDoubleOrNull() ?: 0.0) > 0) "&am=$billAmount" else ""
    "upi://pay?pa=$upiId&pn=$encodedName$amtParam&cu=INR"
  }

  val qrBitmap = remember(upiUrl) {
    QrCodeGeneratorUtil.generateQrBitmap(upiUrl, pixelSize = 280)
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .testTag("dialog_store_upi_qr")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDE9FE)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.QrCode, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(storeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Scan & Pay with any UPI App", fontSize = 11.sp, color = TextMuted)
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Crisp QR Code
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(2.dp, RoyalNavy, RoundedCornerShape(12.dp))
            .padding(14.dp)
        ) {
          Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "UPI Payment QR Code",
            modifier = Modifier.size(190.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Copyable UPI ID Box
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF1F5F9))
            .clickable {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Store UPI ID", upiId))
              Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "UPI: $upiId",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = RoyalMagenta, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Optional Amount Input (Updates QR dynamically!)
        OutlinedTextField(
          value = billAmount,
          onValueChange = { billAmount = it },
          label = { Text("Bill Amount (Optional)") },
          placeholder = { Text("e.g. 450") },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_upi_qr_amount")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Supported UPI Apps Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf("GPay", "PhonePe", "Paytm", "BHIM").forEach { appName ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(appName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Share Link / Button
        Button(
          onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_SUBJECT, "Pay $storeName via UPI")
              putExtra(Intent.EXTRA_TEXT, "Pay $storeName: $upiUrl\nUPI ID: $upiId")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Payment Link"))
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_share_upi_payment")
        ) {
          Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share Payment Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
