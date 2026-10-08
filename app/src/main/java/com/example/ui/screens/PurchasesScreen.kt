package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Distributor
import com.example.data.model.PurchaseInvoice
import com.example.data.model.PurchaseOrder
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
import java.util.Locale

@Composable
fun PurchasesScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val distributors by viewModel.allDistributorsWithPurchases.collectAsState()
  val purchases by viewModel.allPurchases.collectAsState()
  val purchaseOrders by viewModel.allPurchaseOrders.collectAsState()

  var selectedTab by remember { mutableIntStateOf(1) } // Default to 1 (Invoices) so recent and newly added purchases are shown immediately
  var searchQuery by remember { mutableStateOf("") }
  var showAddPurchaseDialog by remember { mutableStateOf(false) }
  var poToDelete by remember { mutableStateOf<PurchaseOrder?>(null) }
  var invoiceToDelete by remember { mutableStateOf<PurchaseInvoice?>(null) }
  var invoiceToEdit by remember { mutableStateOf<PurchaseInvoice?>(null) }

  val filteredDistributors = distributors.filter {
    it.name.contains(searchQuery, ignoreCase = true) || it.gstin.contains(searchQuery, ignoreCase = true)
  }

  val filteredPurchases = purchases.filter {
    it.distributorName.contains(searchQuery, ignoreCase = true) || it.invoiceNumber.contains(searchQuery, ignoreCase = true)
  }

  val filteredOrders = purchaseOrders.filter {
    it.poNumber.contains(searchQuery, ignoreCase = true) ||
      it.supplierName.contains(searchQuery, ignoreCase = true) ||
      it.itemsJson.contains(searchQuery, ignoreCase = true)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { viewModel.navigateTo(Screen.HOME) },
            modifier = Modifier.size(32.dp).padding(end = 4.dp).testTag("btn_back_from_purchases")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Text(
            text = "Purchases & Inward Goods",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Button(
            onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) },
            colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 4.dp).testTag("btn_goto_purchase_orders")
          ) {
            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Orders PO", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { viewModel.navigateTo(Screen.SUPPLIERS) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_goto_suppliers")
          ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Suppliers", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 2. Tabs: Distributors, Invoices, Purchase Orders
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = RoyalMagenta,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = RoyalMagenta
          )
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              text = "Distributors (${distributors.size})",
              fontSize = 12.5.sp,
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 0) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_distributors")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              text = "Invoices (${purchases.size})",
              fontSize = 12.5.sp,
              fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 1) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_purchases_date")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              text = "Orders PO (${purchaseOrders.size})",
              fontSize = 12.5.sp,
              fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
              color = if (selectedTab == 2) RoyalMagenta else TextMuted
            )
          },
          modifier = Modifier.testTag("tab_purchase_orders")
        )
      }

      // 3. Search Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              when (selectedTab) {
                0 -> "Search Distributors"
                1 -> "Search Invoices"
                else -> "Search Purchase Orders (PO)"
              },
              fontSize = 13.sp,
              color = TextMuted
            )
          },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
          },
          singleLine = true,
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = RoyalMagenta,
            unfocusedBorderColor = CardBorder
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("purchase_search_bar")
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextDark)
        }
      }

      // 4. Content List
      when (selectedTab) {
        0 -> {
          // Tab 0: Distributors
          if (filteredDistributors.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No distributors recorded yet", fontSize = 14.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { showAddPurchaseDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                  Text("Record Purchase & Add Distributor")
                }
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredDistributors, key = { it.id }) { dist ->
                DistributorCard(dist)
              }
              item { Spacer(modifier = Modifier.height(80.dp)) }
            }
          }
        }

        1 -> {
          // Tab 1: Invoices
          if (filteredPurchases.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No purchases recorded yet", fontSize = 14.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { showAddPurchaseDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                  Text("+ Add New Purchase")
                }
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredPurchases, key = { it.id }) { purchase ->
                PurchaseInvoiceCard(
                  purchase = purchase,
                  onToggleStatus = { viewModel.togglePurchaseStatus(purchase) },
                  onEdit = { invoiceToEdit = purchase },
                  onDelete = { invoiceToDelete = purchase }
                )
              }
              item { Spacer(modifier = Modifier.height(80.dp)) }
            }
          }
        }

        else -> {
          // Tab 2: Purchase Orders (PO)
          if (filteredOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No purchase orders found", fontSize = 14.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { viewModel.navigateTo(Screen.PURCHASE_ORDERS) },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                ) {
                  Text("+ Create New Purchase Order")
                }
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              items(filteredOrders, key = { it.id }) { po ->
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
                  modifier = Modifier.fillMaxWidth().testTag("card_po_${po.poNumber}")
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(po.poNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                        Text(po.supplierName, fontSize = 12.sp, color = TextDark)
                        Text(po.orderDate, fontSize = 11.sp, color = TextMuted)
                      }
                      Column(horizontalAlignment = Alignment.End) {
                        Text(
                          String.format(Locale.getDefault(), "₹%.2f", po.totalAmount),
                          fontSize = 15.sp,
                          fontWeight = FontWeight.Bold,
                          color = RoyalMagenta
                        )
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (po.status == "RECEIVED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            po.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (po.status == "RECEIVED") StatusGreen else Color(0xFFD97706)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      // WhatsApp PO Share
                      OutlinedButton(
                        onClick = {
                          val msg = "ROYAL PHARMACY PO: ${po.poNumber} for ${po.supplierName}. Total: ₹${po.totalAmount}"
                          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}"))
                          try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                      ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 11.sp, color = StatusGreen)
                      }

                      // Delete Purchase Order Button (Prominent)
                      IconButton(
                        onClick = { poToDelete = po },
                        modifier = Modifier.size(36.dp).testTag("btn_delete_po_item_${po.poNumber}")
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete PO", tint = StatusRed, modifier = Modifier.size(18.dp))
                      }
                    }
                  }
                }
              }
              item { Spacer(modifier = Modifier.height(80.dp)) }
            }
          }
        }
      }
    }

    // Floating Button: "+ Add Purchase"
    Button(
      onClick = { showAddPurchaseDialog = true },
      shape = RoundedCornerShape(24.dp),
      colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("btn_add_purchase")
    ) {
      Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Add Purchase", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }

    // Add Purchase Dialog
    if (showAddPurchaseDialog) {
      AddPurchaseDialog(
        distributors = distributors,
        onDismiss = { showAddPurchaseDialog = false },
        onSave = { name, gstin, amount, count, invNo, status, payMode, medName, medQty, medCost ->
          viewModel.addNewPurchase(
            distributorName = name,
            gstin = gstin,
            amount = amount,
            itemCount = count,
            invoiceNo = invNo,
            status = status,
            paymentMode = payMode,
            medicineName = medName,
            medicineQuantity = medQty,
            medicineCostPrice = medCost
          )
          selectedTab = 1 // Switch to Invoices tab to immediately show newly saved purchase
          showAddPurchaseDialog = false
          Toast.makeText(context, "Purchase $invNo of ₹$amount saved successfully!", Toast.LENGTH_LONG).show()
        }
      )
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
            "Are you sure you want to delete purchase order ${order.poNumber} for ${order.supplierName}? Total value: ₹${String.format(Locale.getDefault(), "%,.2f", order.totalAmount)}. This cannot be undone.",
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
            modifier = Modifier.testTag("btn_confirm_delete_po_modal")
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

    // Delete Purchase Invoice Confirmation Dialog
    invoiceToDelete?.let { inv ->
      AlertDialog(
        onDismissRequest = { invoiceToDelete = null },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Purchase Invoice?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Text(
            "Are you sure you want to delete invoice ${inv.invoiceNumber} (${inv.distributorName}) of ₹${String.format(Locale.getDefault(), "%,.2f", inv.totalAmount)}?",
            fontSize = 13.sp,
            color = TextDark
          )
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deletePurchaseInvoice(inv)
              invoiceToDelete = null
              Toast.makeText(context, "Invoice ${inv.invoiceNumber} deleted", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
          ) {
            Text("Delete Invoice", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { invoiceToDelete = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Edit Purchase Invoice Dialog
    invoiceToEdit?.let { inv ->
      EditPurchaseInvoiceDialog(
        invoice = inv,
        onDismiss = { invoiceToEdit = null },
        onSave = { distName, gstin, amt, count, invNo, status, date ->
          viewModel.updatePurchaseInvoice(
            invoice = inv,
            newDistributorName = distName,
            newGstin = gstin,
            newAmount = amt,
            newItemsCount = count,
            newInvoiceNo = invNo,
            newStatus = status,
            newDate = date
          )
          invoiceToEdit = null
          Toast.makeText(context, "Invoice $invNo updated successfully!", Toast.LENGTH_SHORT).show()
        }
      )
    }
  }
}

@Composable
fun DistributorCard(dist: Distributor) {
  val initials = dist.name.split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .take(2)
    .joinToString("")
    .uppercase()

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
    modifier = Modifier.fillMaxWidth().testTag("distributor_card_${dist.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(RoyalMagenta.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (initials.isNotEmpty()) initials else "DP",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalMagenta
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(text = dist.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = "GSTIN: ${dist.gstin.ifBlank { "Unregistered" }}", fontSize = 11.sp, color = TextMuted)
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(text = "Balance", fontSize = 10.sp, color = TextMuted)
          Text(
            text = String.format(Locale.getDefault(), "₹%.2f", dist.balancePayable),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (dist.balancePayable > 0) StatusRed else StatusGreen
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Active Supplier", fontSize = 11.sp, color = TextMuted)
        }
        Text(text = "Last: ${dist.lastTxnDate}", fontSize = 11.sp, color = TextMuted)
      }
    }
  }
}

@Composable
fun PurchaseInvoiceCard(
  purchase: PurchaseInvoice,
  onToggleStatus: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  val isPaid = purchase.status == "Paid"

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorder)),
    modifier = Modifier.fillMaxWidth().testTag("purchase_invoice_${purchase.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = purchase.distributorName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${purchase.invoiceNumber} • ${purchase.invoiceDate}",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
        Text(
          text = String.format(Locale.getDefault(), "₹%.2f", purchase.totalAmount),
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${purchase.itemsCount} Items purchased",
          fontSize = 11.sp,
          color = TextMuted
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Interactive Paid / Unpaid Toggle Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
              .border(1.dp, if (isPaid) Color(0xFFA5D6A7) else Color(0xFFFFCDD2), RoundedCornerShape(6.dp))
              .clickable { onToggleStatus() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Toggle status",
                tint = if (isPaid) StatusGreen else StatusRed,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (isPaid) "PAID" else "UNPAID",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPaid) StatusGreen else StatusRed
              )
            }
          }

          IconButton(
            onClick = onEdit,
            modifier = Modifier.size(28.dp).testTag("btn_edit_invoice_${purchase.id}")
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Invoice", tint = RoyalNavy, modifier = Modifier.size(16.dp))
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp).testTag("btn_delete_invoice_${purchase.id}")
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun AddPurchaseDialog(
  distributors: List<Distributor>,
  onDismiss: () -> Unit,
  onSave: (String, String, Double, Int, String, String, String, String, Int, Double) -> Unit
) {
  val context = LocalContext.current
  var distributorName by remember { mutableStateOf(distributors.firstOrNull()?.name ?: "Sun Pharma Distribution Hub") }
  var gstin by remember { mutableStateOf(distributors.firstOrNull()?.gstin ?: "18AABCS9988K1Z3") }
  var invoiceNo by remember { mutableStateOf("PUR-${(1000..9999).random()}") }
  var totalAmount by remember { mutableStateOf("950.00") }
  var itemsCount by remember { mutableStateOf("3") }
  var paymentStatus by remember { mutableStateOf("Paid") } // "Paid" or "Unpaid"
  var paymentMode by remember { mutableStateOf("Cash") } // "Cash", "UPI", "Bank Transfer"

  // Optional stock addition
  var medicineName by remember { mutableStateOf("") }
  var medicineQtyText by remember { mutableStateOf("10") }
  var medicineCostText by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Record New Purchase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Quick distributor preset selector
        if (distributors.isNotEmpty()) {
          Text("Select Known Distributor:", fontSize = 11.sp, color = TextMuted)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            distributors.take(3).forEach { dist ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (distributorName == dist.name) RoyalMagenta.copy(alpha = 0.1f) else Color(0xFFF1F5F9))
                  .border(1.dp, if (distributorName == dist.name) RoyalMagenta else CardBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    distributorName = dist.name
                    gstin = dist.gstin
                  }
                  .padding(horizontal = 6.dp, vertical = 3.dp)
              ) {
                Text(
                  text = dist.name.take(12) + if (dist.name.length > 12) "..." else "",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (distributorName == dist.name) RoyalMagenta else TextDark
                )
              }
            }
          }
        }

        OutlinedTextField(
          value = distributorName,
          onValueChange = { distributorName = it },
          label = { Text("Distributor / Supplier Name*") },
          modifier = Modifier.fillMaxWidth().testTag("input_purchase_dist_name")
        )
        OutlinedTextField(
          value = gstin,
          onValueChange = { gstin = it },
          label = { Text("Distributor GSTIN") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = invoiceNo,
          onValueChange = { invoiceNo = it },
          label = { Text("Purchase Invoice No") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Amount (₹)*") },
            modifier = Modifier.weight(1f).testTag("input_purchase_amount")
          )
          OutlinedTextField(
            value = itemsCount,
            onValueChange = { itemsCount = it },
            label = { Text("Items Count") },
            modifier = Modifier.weight(1f)
          )
        }

        // Optional Medicine Stock Update
        Text("Receive Stock Medicine into Inventory (Optional):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
        OutlinedTextField(
          value = medicineName,
          onValueChange = { medicineName = it },
          placeholder = { Text("Medicine Name (e.g. Dolo 650, Pan 40)") },
          modifier = Modifier.fillMaxWidth()
        )
        if (medicineName.isNotBlank()) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = medicineQtyText,
              onValueChange = { medicineQtyText = it },
              label = { Text("Units to Add") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = medicineCostText,
              onValueChange = { medicineCostText = it },
              label = { Text("Cost Price (₹)") },
              modifier = Modifier.weight(1f)
            )
          }
        }

        // Payment Status Selection
        Text("Payment Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Paid") Color(0xFFE8F5E9) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Paid") StatusGreen else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Paid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "✓ Paid (Cash/UPI)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Paid") StatusGreen else TextDark
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Unpaid") Color(0xFFFFEBEE) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Unpaid") StatusRed else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Unpaid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "⚠️ Unpaid (Credit)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Unpaid") StatusRed else TextDark
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = totalAmount.toDoubleOrNull() ?: 0.0
          val cnt = itemsCount.toIntOrNull() ?: 1
          if (distributorName.isBlank()) {
            Toast.makeText(context, "Please enter distributor or supplier name", Toast.LENGTH_SHORT).show()
            return@Button
          }
          if (amt <= 0.0) {
            Toast.makeText(context, "Please enter a valid total amount", Toast.LENGTH_SHORT).show()
            return@Button
          }
          val medQty = medicineQtyText.toIntOrNull() ?: 0
          val medCost = medicineCostText.toDoubleOrNull() ?: 0.0
          onSave(distributorName, gstin, amt, cnt, invoiceNo, paymentStatus, paymentMode, medicineName, medQty, medCost)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_purchase")
      ) {
        Text("Save Purchase")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}

@Composable
fun EditPurchaseInvoiceDialog(
  invoice: PurchaseInvoice,
  onDismiss: () -> Unit,
  onSave: (String, String, Double, Int, String, String, String) -> Unit
) {
  val context = LocalContext.current
  var distributorName by remember { mutableStateOf(invoice.distributorName) }
  var gstin by remember { mutableStateOf(invoice.distributorGstin) }
  var invoiceNo by remember { mutableStateOf(invoice.invoiceNumber) }
  var totalAmount by remember { mutableStateOf(invoice.totalAmount.toString()) }
  var itemsCount by remember { mutableStateOf(invoice.itemsCount.toString()) }
  var paymentStatus by remember { mutableStateOf(invoice.status) }
  var invoiceDate by remember { mutableStateOf(invoice.invoiceDate) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Edit Purchase Invoice ${invoice.invoiceNumber}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = distributorName,
          onValueChange = { distributorName = it },
          label = { Text("Distributor / Supplier Name*") },
          modifier = Modifier.fillMaxWidth().testTag("input_edit_purchase_dist_name")
        )
        OutlinedTextField(
          value = gstin,
          onValueChange = { gstin = it },
          label = { Text("Distributor GSTIN") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = invoiceNo,
          onValueChange = { invoiceNo = it },
          label = { Text("Purchase Invoice No*") },
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = invoiceDate,
          onValueChange = { invoiceDate = it },
          label = { Text("Invoice Date (e.g. 08-10-2026)") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Amount (₹)*") },
            modifier = Modifier.weight(1f).testTag("input_edit_purchase_amount")
          )
          OutlinedTextField(
            value = itemsCount,
            onValueChange = { itemsCount = it },
            label = { Text("Items Count") },
            modifier = Modifier.weight(1f)
          )
        }

        Text("Payment Status:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Paid") Color(0xFFE8F5E9) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Paid") StatusGreen else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Paid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "✓ Paid (Cash/UPI)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Paid") StatusGreen else TextDark
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (paymentStatus == "Unpaid") Color(0xFFFFEBEE) else Color(0xFFF8FAFC))
              .border(
                1.5.dp,
                if (paymentStatus == "Unpaid") StatusRed else CardBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable { paymentStatus = "Unpaid" }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              "⚠️ Unpaid (Credit)",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (paymentStatus == "Unpaid") StatusRed else TextDark
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = totalAmount.toDoubleOrNull() ?: 0.0
          val cnt = itemsCount.toIntOrNull() ?: 1
          if (distributorName.isBlank()) {
            Toast.makeText(context, "Please enter distributor or supplier name", Toast.LENGTH_SHORT).show()
            return@Button
          }
          if (amt <= 0.0) {
            Toast.makeText(context, "Please enter a valid total amount", Toast.LENGTH_SHORT).show()
            return@Button
          }
          onSave(distributorName, gstin, amt, cnt, invoiceNo, paymentStatus, invoiceDate)
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
        modifier = Modifier.testTag("btn_save_edit_purchase")
      ) {
        Text("Update Invoice")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextMuted)
      }
    }
  )
}
