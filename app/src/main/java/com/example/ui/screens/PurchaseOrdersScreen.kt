package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MedicineItem
import com.example.data.model.POItem
import com.example.data.model.PurchaseOrder
import com.example.data.model.Supplier
import com.example.service.DistributorExportService
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOrdersScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val purchaseOrders by viewModel.allPurchaseOrders.collectAsState()
  val suppliers by viewModel.allSuppliers.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: All POs, 1: Pending/Ordered, 2: Received, 3: Drafts
  var searchQuery by remember { mutableStateOf("") }
  var showCreatePoDialog by remember { mutableStateOf(false) }
  var poToReceive by remember { mutableStateOf<PurchaseOrder?>(null) }
  var poToDelete by remember { mutableStateOf<PurchaseOrder?>(null) }

  val orderedPos = purchaseOrders.filter { it.status == "ORDERED" }
  val receivedPos = purchaseOrders.filter { it.status == "RECEIVED" }

  val displayedOrders = when (selectedTab) {
    1 -> orderedPos
    2 -> receivedPos
    else -> purchaseOrders
  }.filter {
    searchQuery.isBlank() ||
      it.poNumber.contains(searchQuery, ignoreCase = true) ||
      it.supplierName.contains(searchQuery, ignoreCase = true) ||
      it.itemsJson.contains(searchQuery, ignoreCase = true)
  }

  val totalOpenOrdersAmount = orderedPos.sumOf { it.totalAmount }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Top App Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(RoyalNavy)
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.PURCHASE) },
            modifier = Modifier.size(36.dp).testTag("btn_back_from_po_screen")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Purchase Order Module", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("Distributor Procurement & Goods Receipt (GRN)", color = Color(0xFF93C5FD), fontSize = 11.sp)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.SUPPLIERS) },
            modifier = Modifier.testTag("btn_goto_suppliers_crm")
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = "Suppliers", tint = Color(0xFF67E8F9))
          }
        }
      }

      // 2. Summary & Filters Box
      Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Metric Cards Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Open POs Box
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFEF3C7))
                .padding(10.dp)
            ) {
              Column {
                Text("OPEN ORDERS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                Text("${orderedPos.size} Pending", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                Text("₹${String.format(Locale.getDefault(), "%,.0f", totalOpenOrdersAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
              }
            }

            // Received POs Box
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDCFCE7))
                .padding(10.dp)
            ) {
              Column {
                Text("COMPLETED GRN", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = StatusGreen)
                Text("${receivedPos.size} Received", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = StatusGreen)
                Text("Inventory Updated", fontSize = 9.5.sp, color = TextMuted)
              }
            }

            // Active Suppliers Box
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDE9FE))
                .padding(10.dp)
            ) {
              Column {
                Text("DISTRIBUTORS", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                Text("${suppliers.size} Vendors", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalNavy)
                Text("Assam & National", fontSize = 9.5.sp, color = TextMuted)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Search Field
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by PO No, Supplier Name, or Medicine...", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFFF8FAFC),
              unfocusedContainerColor = Color(0xFFF8FAFC),
              focusedBorderColor = RoyalMagenta,
              unfocusedBorderColor = CardBorder
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("input_po_search")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Filter Tabs
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(
              "All Orders (${purchaseOrders.size})",
              "Pending Delivery (${orderedPos.size})",
              "Completed GRN (${receivedPos.size})"
            ).forEachIndexed { idx, label ->
              FilterChip(
                selected = selectedTab == idx,
                onClick = { selectedTab = idx },
                label = { Text(label, fontSize = 11.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalNavy,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }

      // 3. Purchase Orders List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (displayedOrders.isEmpty()) {
          item {
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No Purchase Orders Found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Create a new Purchase Order to procure medicines from distributors.", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                  onClick = { showCreatePoDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Create First Purchase Order", color = Color.White)
                }
              }
            }
          }
        } else {
          items(displayedOrders) { po ->
            val isReceived = po.status == "RECEIVED"

            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isReceived) Color(0xFFF8FAFC) else Color.White
              ),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                  if (isReceived) CardBorder else Color(0xFFDDD6FE)
                )
              ),
              modifier = Modifier.fillMaxWidth().testTag("card_po_${po.poNumber}")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Top Row: PO Number + Status
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isReceived) StatusGreen else RoyalNavy)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = po.poNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Date: ${po.orderDate}",
                      fontSize = 11.sp,
                      color = TextMuted
                    )
                  }

                  // Status Badge
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isReceived) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(
                      text = if (isReceived) "COMPLETED (GRN)" else "PENDING DELIVERY",
                      fontSize = 9.5.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isReceived) StatusGreen else Color(0xFFB45309)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Supplier Info
                Text(
                  text = "Supplier: ${po.supplierName}",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )

                if (po.expectedDeliveryDate.isNotBlank()) {
                  Text(
                    text = "Expected Delivery: ${po.expectedDeliveryDate}",
                    fontSize = 11.sp,
                    color = Color(0xFF4338CA)
                  )
                }

                if (isReceived && po.supplierInvoiceNumber.isNotBlank()) {
                  Text(
                    text = "Supplier Inv No: ${po.supplierInvoiceNumber} • Received: ${po.receivedDate}",
                    fontSize = 11.sp,
                    color = StatusGreen
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                Spacer(modifier = Modifier.height(8.dp))

                // Item Breakdown
                Text("Ordered Medicines:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = po.itemsJson,
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextDark,
                  lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
                Spacer(modifier = Modifier.height(8.dp))

                // Total Amount
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("Total PO Valuation:", fontSize = 12.sp, color = TextDark)
                  Text(
                    text = "₹${String.format(Locale.getDefault(), "%,.2f", po.totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalMagenta,
                    fontFamily = FontFamily.Monospace
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  // WhatsApp PO
                  OutlinedButton(
                    onClick = {
                      val supplierObj = suppliers.find { it.name.equals(po.supplierName, ignoreCase = true) || it.companyName.equals(po.supplierName, ignoreCase = true) }
                      val phone = supplierObj?.phone?.replace("[^0-9]".toRegex(), "") ?: ""
                      val msg = buildString {
                        append("🏥 *${profile.businessName}* 🏥\n")
                        append("📜 *PURCHASE ORDER: ${po.poNumber}*\n")
                        append("Supplier: ${po.supplierName}\n")
                        append("Order Date: ${po.orderDate}\n")
                        append("Expected Delivery: ${po.expectedDeliveryDate}\n")
                        append("--------------------------------\n")
                        append("📦 *Ordered Items:*\n")
                        append("${po.itemsJson}\n")
                        append("--------------------------------\n")
                        append("💰 *TOTAL VALUE: ₹${String.format(Locale.getDefault(), "%,.2f", po.totalAmount)}*\n")
                        append("Please confirm dispatch and send GST invoice with DL Form 20/21 compliance.\n")
                        append("Thank you!\nRoyal Pharmacy POS Cloud Edition")
                      }

                      val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = if (phone.isNotBlank()) {
                          Uri.parse("https://api.whatsapp.com/send?phone=91$phone&text=${Uri.encode(msg)}")
                        } else {
                          Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}")
                        }
                      }
                      try {
                        context.startActivity(intent)
                      } catch (_: Exception) {
                        Toast.makeText(context, "WhatsApp not available", Toast.LENGTH_SHORT).show()
                      }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                  ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp PO", fontSize = 11.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                  }

                  if (!isReceived) {
                    Button(
                      onClick = { poToReceive = po },
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                      modifier = Modifier.weight(1.2f).height(38.dp).testTag("btn_receive_po_${po.poNumber}")
                    ) {
                      Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Receive Stock (GRN)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                  }

                  // Delete Purchase Order Button
                  IconButton(
                    onClick = { poToDelete = po },
                    modifier = Modifier.size(38.dp).testTag("btn_delete_po_${po.poNumber}")
                  ) {
                    Icon(
                      Icons.Default.Delete,
                      contentDescription = "Delete Purchase Order",
                      tint = StatusRed,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Delete PO Confirmation Dialog
    poToDelete?.let { order ->
      AlertDialog(
        onDismissRequest = { poToDelete = null },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Purchase Order?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Text(
            "Are you sure you want to delete purchase order ${order.poNumber} (${order.supplierName}) with valuation ₹${String.format(Locale.getDefault(), "%,.2f", order.totalAmount)}? This action cannot be undone.",
            fontSize = 13.sp,
            color = TextDark
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deletePurchaseOrder(order)
              poToDelete = null
              Toast.makeText(context, "Purchase order ${order.poNumber} deleted", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
            modifier = Modifier.testTag("btn_confirm_delete_po")
          ) {
            Text("Delete PO", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { poToDelete = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Floating Action Button to Create PO
    FloatingActionButton(
      onClick = { showCreatePoDialog = true },
      containerColor = RoyalNavy,
      contentColor = Color.White,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(20.dp)
        .testTag("fab_create_purchase_order")
    ) {
      Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Add, contentDescription = "Create PO")
        Spacer(modifier = Modifier.width(6.dp))
        Text("Create PO", fontWeight = FontWeight.Bold)
      }
    }
  }

  // Create Purchase Order Dialog
  if (showCreatePoDialog) {
    var selectedSupplier by remember { mutableStateOf(suppliers.firstOrNull()) }
    var isSupplierDropdownOpen by remember { mutableStateOf(false) }
    var deliveryDateInput by remember { mutableStateOf("15-10-2026") }
    val selectedItems = remember { mutableStateListOf<POItem>() }

    if (selectedItems.isEmpty()) {
      medicines.take(3).forEach { m ->
        selectedItems.add(
          POItem(
            medicineId = m.id,
            medicineName = m.name,
            requestedPacks = if (m.stockPacks <= m.minStockAlert) m.minStockAlert * 2 else 10,
            estimatedUnitRate = m.purchaseRate,
            batchNumber = "RP-${(100..999).random()}",
            expiryDate = "12/2027"
          )
        )
      }
    }

    AlertDialog(
      onDismissRequest = { showCreatePoDialog = false },
      title = {
        Text("Create Purchase Order (PO)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Supplier selector dropdown
          ExposedDropdownMenuBox(
            expanded = isSupplierDropdownOpen,
            onExpandedChange = { isSupplierDropdownOpen = it }
          ) {
            OutlinedTextField(
              value = selectedSupplier?.name ?: "Select Supplier",
              onValueChange = {},
              readOnly = true,
              label = { Text("Distributor / Supplier") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSupplierDropdownOpen) },
              modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = isSupplierDropdownOpen,
              onDismissRequest = { isSupplierDropdownOpen = false }
            ) {
              suppliers.forEach { sup ->
                DropdownMenuItem(
                  text = { Text("${sup.name} (${sup.companyName})") },
                  onClick = {
                    selectedSupplier = sup
                    isSupplierDropdownOpen = false
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = deliveryDateInput,
            onValueChange = { deliveryDateInput = it },
            label = { Text("Expected Delivery Date") },
            placeholder = { Text("DD-MM-YYYY") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Text("Procurement Items:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFF8FAFC))
              .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            selectedItems.forEachIndexed { idx, itm ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(itm.medicineName, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = TextDark)
                  Text("Rate: ₹${String.format(Locale.getDefault(), "%.2f", itm.estimatedUnitRate)}", fontSize = 10.sp, color = TextMuted)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text("${itm.requestedPacks} Packs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    "₹${String.format(Locale.getDefault(), "%.0f", itm.requestedPacks * itm.estimatedUnitRate)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalMagenta
                  )
                }
              }
            }
          }

          val computedTotal = selectedItems.sumOf { it.requestedPacks * it.estimatedUnitRate }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Total Estimated PO Amount:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("₹${String.format(Locale.getDefault(), "%,.2f", computedTotal)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = RoyalMagenta)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val sup = selectedSupplier ?: suppliers.firstOrNull()
            if (sup != null) {
              val itemsText = selectedItems.joinToString("\n") { "${it.requestedPacks}x ${it.medicineName} @ ₹${String.format(Locale.getDefault(), "%.2f", it.estimatedUnitRate)}" }
              val totalAmt = selectedItems.sumOf { it.requestedPacks * it.estimatedUnitRate }
              viewModel.createPurchaseOrder(
                supplierId = sup.id,
                supplierName = sup.name,
                itemsJson = itemsText,
                totalAmount = totalAmt,
                expectedDeliveryDate = deliveryDateInput
              )
              showCreatePoDialog = false
            } else {
              Toast.makeText(context, "Please select a distributor", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Generate & Save PO")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreatePoDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Receive Goods (GRN) Dialog
  if (poToReceive != null) {
    var supplierInvoiceNoInput by remember { mutableStateOf("INV-SUP-${(1000..9999).random()}") }

    AlertDialog(
      onDismissRequest = { poToReceive = null },
      title = {
        Text("Goods Receipt Note (GRN) - ${poToReceive!!.poNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text("Supplier: ${poToReceive!!.supplierName}", fontSize = 12.sp, color = TextDark)
          Text("Ordered Items:\n${poToReceive!!.itemsJson}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextMuted)

          OutlinedTextField(
            value = supplierInvoiceNoInput,
            onValueChange = { supplierInvoiceNoInput = it },
            label = { Text("Supplier Tax Invoice Number") },
            placeholder = { Text("e.g. GST-INV-8910") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFDCFCE7))
              .padding(8.dp)
          ) {
            Text(
              text = "✅ Confirming GRN will automatically increment medicine stock packs in your catalog and record purchase ledger entry.",
              fontSize = 10.5.sp,
              color = Color(0xFF166534)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.recordReceivedStockFromPO(
              po = poToReceive!!,
              supplierInvoiceNo = supplierInvoiceNoInput,
              itemsToReceive = listOf() // Handled by repository
            )
            Toast.makeText(context, "Stock received for ${poToReceive!!.poNumber}!", Toast.LENGTH_SHORT).show()
            poToReceive = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
        ) {
          Text("Confirm Stock Received")
        }
      },
      dismissButton = {
        TextButton(onClick = { poToReceive = null }) {
          Text("Cancel")
        }
      }
    )
  }
}
