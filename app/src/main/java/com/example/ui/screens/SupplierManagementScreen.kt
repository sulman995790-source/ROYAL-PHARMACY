@file:Suppress("DEPRECATION")
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.PurchaseOrder
import com.example.data.model.Supplier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierManagementScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val suppliers by viewModel.allSuppliers.collectAsState()
  val purchaseOrders by viewModel.allPurchaseOrders.collectAsState()
  val medicines by viewModel.allMedicines.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Suppliers, 1: Purchase Orders, 2: Receive Stock
  var showAddSupplierDialog by remember { mutableStateOf(false) }
  var supplierToEdit by remember { mutableStateOf<Supplier?>(null) }
  var supplierToDelete by remember { mutableStateOf<Supplier?>(null) }
  var showCreatePoDialog by remember { mutableStateOf(false) }
  var poToEdit by remember { mutableStateOf<PurchaseOrder?>(null) }
  var poToDelete by remember { mutableStateOf<PurchaseOrder?>(null) }
  var poToReceive by remember { mutableStateOf<PurchaseOrder?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { viewModel.navigateTo(Screen.HOME) }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
        }
        Text(
          text = "Supplier Management & POs",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
      }

      // Tabs
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
          text = { Text("Suppliers (${suppliers.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_suppliers_list")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Purchase Orders (${purchaseOrders.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_purchase_orders")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Receive Stock", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
          modifier = Modifier.testTag("tab_receive_stock")
        )
      }

      when (selectedTab) {
        0 -> {
          // Tab 0: Suppliers Database
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(suppliers, key = { it.id }) { sup ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth().testTag("supplier_card_${sup.id}")
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(sup.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text("Contact: ${sup.contactPerson} • ${sup.phone}", fontSize = 12.sp, color = TextMuted)
                      Text("GSTIN: ${sup.gstin} • DL: ${sup.drugLicenseNo}", fontSize = 11.sp, color = TextMuted)
                      Text(sup.address, fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    if (sup.outstandingPayable > 0) {
                      Column(horizontalAlignment = Alignment.End) {
                        Text("₹${sup.outstandingPayable}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusRed)
                        Text("Payable", fontSize = 10.sp, color = TextMuted)
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    // Call button
                    OutlinedButton(
                      onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sup.phone}"))
                        context.startActivity(intent)
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                      Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Call", fontSize = 11.sp, color = RoyalNavy)
                    }

                    // Google Maps Real-Time Route Button
                    Button(
                      onClick = {
                        val geoUri = Uri.parse("geo:${sup.latitude},${sup.longitude}?q=${Uri.encode(sup.name + ", " + sup.address)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                          setPackage("com.google.android.apps.maps")
                        }
                        try {
                          context.startActivity(mapIntent)
                        } catch (e: Exception) {
                          val webMap = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(sup.address)}"))
                          context.startActivity(webMap)
                        }
                      },
                      shape = RoundedCornerShape(8.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                      modifier = Modifier.weight(1.2f).height(36.dp).testTag("btn_map_route_${sup.id}")
                    ) {
                      Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                      Spacer(modifier = Modifier.width(3.dp))
                      Text("Route", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Edit Supplier Button
                    IconButton(
                      onClick = { supplierToEdit = sup },
                      modifier = Modifier.size(36.dp).testTag("btn_edit_supplier_${sup.id}")
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit Supplier", tint = RoyalNavy, modifier = Modifier.size(16.dp))
                    }

                    if (sup.outstandingPayable > 0) {
                      OutlinedButton(
                        onClick = {
                          viewModel.updateSupplier(sup.copy(outstandingPayable = 0.0))
                          Toast.makeText(context, "Settled payable for ${sup.name}!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusGreen),
                        modifier = Modifier.height(36.dp)
                      ) {
                        Text("Mark Paid", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    // Delete Supplier Button
                    IconButton(
                      onClick = { supplierToDelete = sup },
                      modifier = Modifier.size(36.dp).testTag("btn_delete_supplier_${sup.id}")
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete Supplier", tint = StatusRed, modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(70.dp))
            }
          }
        }

        1 -> {
          // Tab 1: Purchase Orders List
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(purchaseOrders, key = { it.id }) { po ->
              Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                modifier = Modifier.fillMaxWidth().testTag("po_card_${po.id}")
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                  ) {
                    Column {
                      Text(po.poNumber, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text("Supplier: ${po.supplierName}", fontSize = 12.sp, color = TextMuted)
                      Text("Ordered: ${po.orderDate} • Expected: ${po.expectedDeliveryDate}", fontSize = 11.sp, color = TextMuted)
                    }

                    // Status Pill
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (po.status == "RECEIVED") Color(0xFFD1FAE5) else Color(0xFFFEF3C7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = po.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (po.status == "RECEIVED") StatusGreen else Color(0xFFD97706)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Text("Items: ${po.itemsJson}", fontSize = 11.sp, color = TextDark)
                  Spacer(modifier = Modifier.height(6.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = String.format(Locale.getDefault(), "Total: ₹%.2f", po.totalAmount),
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = RoyalMagenta
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      if (po.status == "ORDERED") {
                        Button(
                          onClick = { poToReceive = po },
                          shape = RoundedCornerShape(6.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                          Text("Record Stock", fontSize = 11.sp, color = Color.White)
                        }
                      } else {
                        Text("Received on ${po.receivedDate}", fontSize = 11.sp, color = StatusGreen, fontWeight = FontWeight.Medium)
                      }

                      IconButton(
                        onClick = { poToEdit = po },
                        modifier = Modifier.size(32.dp).testTag("btn_edit_po_${po.id}")
                      ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit PO", tint = RoyalNavy, modifier = Modifier.size(15.dp))
                      }

                      IconButton(
                        onClick = { poToDelete = po },
                        modifier = Modifier.size(32.dp).testTag("btn_delete_po_${po.id}")
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete PO", tint = StatusRed, modifier = Modifier.size(15.dp))
                      }
                    }
                  }
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(70.dp))
            }
          }
        }

        2 -> {
          // Tab 2: Record Received Stock Flow
          val pendingOrders = purchaseOrders.filter { it.status == "ORDERED" }
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            item {
              Text(
                text = "Select a pending purchase order to receive stock into inventory:",
                fontSize = 13.sp,
                color = TextMuted
              )
            }

            items(pendingOrders, key = { it.id }) { po ->
              Card(
                onClick = { poToReceive = po },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = androidx.compose.ui.graphics.SolidColor(RoyalMagenta)
                ),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(po.poNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(po.supplierName, fontSize = 12.sp, color = TextMuted)
                    Text(po.itemsJson, fontSize = 11.sp, color = TextDark)
                  }
                  Button(
                    onClick = { poToReceive = po },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                  ) {
                    Text("Receive", fontSize = 12.sp, color = Color.White)
                  }
                }
              }
            }

            if (pendingOrders.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(8.dp),
                  colors = CardDefaults.cardColors(containerColor = Color.White),
                  modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                ) {
                  Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("All purchase orders have been received and reconciled! Create a new PO to order more stock.", color = TextMuted, fontSize = 13.sp)
                  }
                }
              }
            }
          }
        }
      }
    }

    // Floating Button
    if (selectedTab == 0) {
      Button(
        onClick = { showAddSupplierDialog = true },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).testTag("btn_add_supplier")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Supplier", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
    } else if (selectedTab == 1) {
      Button(
        onClick = { showCreatePoDialog = true },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).testTag("btn_create_po")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Create PO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }
    }

    // Add Supplier Dialog
    if (showAddSupplierDialog) {
      AddSupplierDialog(
        onDismiss = { showAddSupplierDialog = false },
        onSave = { sup ->
          viewModel.addSupplier(sup)
          showAddSupplierDialog = false
        }
      )
    }

    // Create Purchase Order Dialog
    if (showCreatePoDialog) {
      CreatePoDialog(
        suppliers = suppliers,
        medicines = medicines,
        onDismiss = { showCreatePoDialog = false },
        onCreate = { supId, supName, itemsStr, total, expDate ->
          viewModel.createPurchaseOrder(supId, supName, itemsStr, total, expDate)
          showCreatePoDialog = false
        }
      )
    }

    // Receive Stock Confirmation Dialog
    poToReceive?.let { po ->
      ReceiveStockDialog(
        po = po,
        medicines = medicines,
        onDismiss = { poToReceive = null },
        onConfirm = { invoiceNo, itemsToReceive ->
          viewModel.recordReceivedStockFromPO(po, invoiceNo, itemsToReceive)
          poToReceive = null
        }
      )
    }

    // Edit Supplier Dialog
    supplierToEdit?.let { sup ->
      EditSupplierDialog(
        supplier = sup,
        onDismiss = { supplierToEdit = null },
        onSave = { updated ->
          viewModel.updateSupplier(updated)
          supplierToEdit = null
        }
      )
    }

    // Delete Supplier Confirmation
    supplierToDelete?.let { sup ->
      AlertDialog(
        onDismissRequest = { supplierToDelete = null },
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = StatusRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Supplier?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Text("Are you sure you want to delete supplier '${sup.name}'? Their contact details and order links will be removed.")
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteSupplier(sup)
              supplierToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
          ) {
            Text("Delete Supplier", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { supplierToDelete = null }) {
            Text("Cancel", color = TextMuted)
          }
        }
      )
    }

    // Edit PO Dialog
    poToEdit?.let { po ->
      EditPoDialog(
        po = po,
        onDismiss = { poToEdit = null },
        onSave = { updated ->
          viewModel.updatePurchaseOrder(updated)
          poToEdit = null
        }
      )
    }

    // Delete PO Confirmation
    poToDelete?.let { po ->
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
          Text("Are you sure you want to delete purchase order ${po.poNumber} for '${po.supplierName}' with valuation ₹${String.format(Locale.getDefault(), "%,.2f", po.totalAmount)}?")
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deletePurchaseOrder(po)
              poToDelete = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
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
  }
}

@Composable
fun AddSupplierDialog(
  onDismiss: () -> Unit,
  onSave: (Supplier) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var contact by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var gstin by remember { mutableStateOf("") }
  var dlNo by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Add Pharma Supplier / C&F Agent", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Agency / Company Name*") }, modifier = Modifier.fillMaxWidth().testTag("input_supplier_name"))
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Person") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number*") }, modifier = Modifier.fillMaxWidth().testTag("input_supplier_phone"))
        OutlinedTextField(value = gstin, onValueChange = { gstin = it }, label = { Text("GSTIN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dlNo, onValueChange = { dlNo = it }, label = { Text("Drug License Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Warehouse Address") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onSave(
              Supplier(
                name = name,
                companyName = name,
                contactPerson = contact,
                phone = phone,
                gstin = gstin,
                drugLicenseNo = dlNo,
                address = address
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
      ) {
        Text("Save Supplier")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePoDialog(
  suppliers: List<Supplier>,
  medicines: List<com.example.data.model.MedicineItem>,
  onDismiss: () -> Unit,
  onCreate: (Long, String, String, Double, String) -> Unit
) {
  var selectedSupplier by remember { mutableStateOf(suppliers.firstOrNull()) }
  var supExpanded by remember { mutableStateOf(false) }
  var selectedMedicine by remember { mutableStateOf(medicines.firstOrNull()) }
  var medExpanded by remember { mutableStateOf(false) }
  var requestedPacks by remember { mutableStateOf("50") }
  var expectedDate by remember { mutableStateOf("10-10-2026") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Purchase Order (PO)", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Supplier Dropdown
        ExposedDropdownMenuBox(expanded = supExpanded, onExpandedChange = { supExpanded = !supExpanded }) {
          OutlinedTextField(
            value = selectedSupplier?.name ?: "Select Supplier",
            onValueChange = {},
            readOnly = true,
            label = { Text("Supplier*") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
          )
          ExposedDropdownMenu(expanded = supExpanded, onDismissRequest = { supExpanded = false }) {
            suppliers.forEach { sup ->
              DropdownMenuItem(
                text = { Text(sup.name) },
                onClick = { selectedSupplier = sup; supExpanded = false }
              )
            }
          }
        }

        // Medicine Dropdown
        ExposedDropdownMenuBox(expanded = medExpanded, onExpandedChange = { medExpanded = !medExpanded }) {
          OutlinedTextField(
            value = selectedMedicine?.name ?: "Select Medicine",
            onValueChange = {},
            readOnly = true,
            label = { Text("Order Medicine*") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = medExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
          )
          ExposedDropdownMenu(expanded = medExpanded, onDismissRequest = { medExpanded = false }) {
            medicines.forEach { med ->
              DropdownMenuItem(
                text = { Text("${med.name} (@ ₹${med.purchaseRate})") },
                onClick = { selectedMedicine = med; medExpanded = false }
              )
            }
          }
        }

        OutlinedTextField(
          value = requestedPacks,
          onValueChange = { requestedPacks = it },
          label = { Text("Quantity (Packs)*") },
          modifier = Modifier.fillMaxWidth().testTag("input_po_packs")
        )

        OutlinedTextField(
          value = expectedDate,
          onValueChange = { expectedDate = it },
          label = { Text("Expected Delivery Date") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          selectedSupplier?.let { sup ->
            selectedMedicine?.let { med ->
              val packs = requestedPacks.toIntOrNull() ?: 10
              val total = packs * med.purchaseRate
              val itemsSummary = "${med.name} (x$packs packs)"
              onCreate(sup.id, sup.name, itemsSummary, total, expectedDate)
            }
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier.testTag("btn_submit_po")
      ) {
        Text("Issue PO")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}

@Composable
fun ReceiveStockDialog(
  po: PurchaseOrder,
  medicines: List<com.example.data.model.MedicineItem>,
  onDismiss: () -> Unit,
  onConfirm: (String, List<Pair<Long, Int>>) -> Unit
) {
  var invoiceNo by remember { mutableStateOf("INV-SUP-${System.currentTimeMillis() % 10000}") }
  var receivedPacks by remember { mutableStateOf("50") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Receive Stock for ${po.poNumber}", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Supplier: ${po.supplierName}", fontSize = 13.sp, color = TextDark)
        Text("Items: ${po.itemsJson}", fontSize = 12.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = invoiceNo,
          onValueChange = { invoiceNo = it },
          label = { Text("Supplier Delivery Invoice No*") },
          modifier = Modifier.fillMaxWidth().testTag("input_receive_invoice_no")
        )
        OutlinedTextField(
          value = receivedPacks,
          onValueChange = { receivedPacks = it },
          label = { Text("Confirmed Received Packs") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val packs = receivedPacks.toIntOrNull() ?: 10
          val matchedMed = medicines.firstOrNull { po.itemsJson.contains(it.name, ignoreCase = true) } ?: medicines.firstOrNull()
          val items = if (matchedMed != null) listOf(Pair(matchedMed.id, packs)) else emptyList()
          onConfirm(invoiceNo, items)
        },
        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
        modifier = Modifier.testTag("btn_confirm_receive_stock")
      ) {
        Text("Reconcile & Update Inventory")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}

@Composable
fun EditSupplierDialog(
  supplier: Supplier,
  onDismiss: () -> Unit,
  onSave: (Supplier) -> Unit
) {
  var name by remember { mutableStateOf(supplier.name) }
  var contact by remember { mutableStateOf(supplier.contactPerson) }
  var phone by remember { mutableStateOf(supplier.phone) }
  var gstin by remember { mutableStateOf(supplier.gstin) }
  var dlNo by remember { mutableStateOf(supplier.drugLicenseNo) }
  var address by remember { mutableStateOf(supplier.address) }
  var payableText by remember { mutableStateOf(supplier.outstandingPayable.toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Supplier Details", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Agency / Company Name*") }, modifier = Modifier.fillMaxWidth().testTag("input_edit_supplier_name"))
        OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contact Person") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number*") }, modifier = Modifier.fillMaxWidth().testTag("input_edit_supplier_phone"))
        OutlinedTextField(value = gstin, onValueChange = { gstin = it }, label = { Text("GSTIN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dlNo, onValueChange = { dlNo = it }, label = { Text("Drug License Number") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Warehouse Address") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = payableText, onValueChange = { payableText = it }, label = { Text("Outstanding Payable (₹)") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            val payable = payableText.toDoubleOrNull() ?: supplier.outstandingPayable
            onSave(
              supplier.copy(
                name = name.trim(),
                companyName = name.trim(),
                contactPerson = contact.trim(),
                phone = phone.trim(),
                gstin = gstin.trim(),
                drugLicenseNo = dlNo.trim(),
                address = address.trim(),
                outstandingPayable = payable
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier.testTag("btn_save_edit_supplier")
      ) {
        Text("Update Supplier")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}

@Composable
fun EditPoDialog(
  po: PurchaseOrder,
  onDismiss: () -> Unit,
  onSave: (PurchaseOrder) -> Unit
) {
  var supplierName by remember { mutableStateOf(po.supplierName) }
  var itemsJson by remember { mutableStateOf(po.itemsJson) }
  var totalAmountText by remember { mutableStateOf(po.totalAmount.toString()) }
  var expectedDate by remember { mutableStateOf(po.expectedDeliveryDate) }
  var status by remember { mutableStateOf(po.status) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Edit Purchase Order ${po.poNumber}", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = supplierName, onValueChange = { supplierName = it }, label = { Text("Supplier Name*") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = itemsJson, onValueChange = { itemsJson = it }, label = { Text("Ordered Items Summary") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = totalAmountText, onValueChange = { totalAmountText = it }, label = { Text("Total Valuation (₹)*") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = expectedDate, onValueChange = { expectedDate = it }, label = { Text("Expected Delivery Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status (ORDERED / RECEIVED)") }, modifier = Modifier.fillMaxWidth())
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amt = totalAmountText.toDoubleOrNull() ?: po.totalAmount
          onSave(
            po.copy(
              supplierName = supplierName.trim(),
              itemsJson = itemsJson.trim(),
              totalAmount = amt,
              expectedDeliveryDate = expectedDate.trim(),
              status = status.trim().uppercase()
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
      ) {
        Text("Update PO")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
    }
  )
}
