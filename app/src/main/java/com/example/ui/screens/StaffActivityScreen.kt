package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import com.example.viewmodel.StaffActivityLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffActivityScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val activities by viewModel.staffActivityLogs.collectAsState()
  val staffMembers by viewModel.staffMembers.collectAsState()

  var selectedFilter by remember { mutableStateOf("ALL") }
  var searchQuery by remember { mutableStateOf("") }

  val filteredActivities = remember(activities, selectedFilter, searchQuery) {
    activities.filter { log ->
      val matchesFilter = when (selectedFilter) {
        "BILLING" -> log.actionType == "BILL_GENERATED"
        "STOCK" -> log.actionType == "STOCK_UPDATED"
        "STAFF" -> log.actionType in listOf("STAFF_ADDED", "STAFF_REMOVED", "PERMISSION_UPDATED")
        "PO" -> log.actionType == "PO_CREATED"
        else -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
        log.staffName.contains(searchQuery, ignoreCase = true) ||
        log.description.contains(searchQuery, ignoreCase = true) ||
        log.staffRole.contains(searchQuery, ignoreCase = true)

      matchesFilter && matchesSearch
    }
  }

  val totalBills = activities.count { it.actionType == "BILL_GENERATED" }
  val totalStockUpdates = activities.count { it.actionType == "STOCK_UPDATED" }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalNavy)
        .padding(horizontal = 12.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Staff Activity Audit Trail",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp
        )
        Text(
          text = "Real-time log of billing, inventory & staff operations",
          color = TextLight,
          fontSize = 11.sp
        )
      }
      IconButton(onClick = { viewModel.navigateTo(Screen.BUSINESS_PROFILE) }) {
        Icon(
          Icons.Default.ManageAccounts,
          contentDescription = "Staff Permissions",
          tint = Color(0xFFFFD54F)
        )
      }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Metric Summary Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricStatCard(
            title = "Active Staff",
            value = staffMembers.size.toString(),
            icon = Icons.Default.People,
            iconTint = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
          )
          MetricStatCard(
            title = "Bills Logged",
            value = totalBills.toString(),
            icon = Icons.Default.Receipt,
            iconTint = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
          )
          MetricStatCard(
            title = "Stock Updates",
            value = totalStockUpdates.toString(),
            icon = Icons.Default.Inventory2,
            iconTint = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Quick Actions for testing & simulation
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Live Activity Test Simulation",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  viewModel.logStaffActivity(
                    staffName = "Nijamuddin Khan",
                    staffRole = "POS-only access",
                    actionType = "BILL_GENERATED",
                    description = "Simulated Counter Bill #INV-1035 generated (₹620.00, Cash)",
                    badgeColorHex = 0xFF10B981
                  )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("btn_sim_bill")
              ) {
                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF10B981))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Bill Action", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = {
                  viewModel.logStaffActivity(
                    staffName = "Rahul Sharma",
                    staffRole = "Inventory & Billing access",
                    actionType = "STOCK_UPDATED",
                    description = "Simulated Stock Update: Paracetamol 650mg (+50 units)",
                    badgeColorHex = 0xFF3B82F6
                  )
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).testTag("btn_sim_stock")
              ) {
                Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF3B82F6))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Stock Action", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Search and Filter Bar
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by staff name or action description...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear")
                }
              }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("search_activity_input")
          )

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filters = listOf(
              "ALL" to "All Actions (${activities.size})",
              "BILLING" to "Billing Memos",
              "STOCK" to "Stock Updates",
              "STAFF" to "Staff Permissions",
              "PO" to "Purchase Orders"
            )
            items(filters) { (key, label) ->
              FilterChip(
                selected = selectedFilter == key,
                onClick = { selectedFilter = key },
                label = { Text(label, fontSize = 11.5.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = RoyalNavy,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }
      }

      // Activity Feed Items
      if (filteredActivities.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(32.dp).fillMaxWidth(),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
              Text("No activity records match your criteria", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Switch filters or simulate actions above to test the logger.", fontSize = 11.sp, color = TextMuted)
            }
          }
        }
      } else {
        items(filteredActivities, key = { it.id }) { log ->
          StaffActivityLogCard(log = log)
        }
      }

      item {
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }
}

@Composable
fun MetricStatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = CardDefaults.outlinedCardBorder(),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(title, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
      }
      Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
    }
  }
}

@Composable
fun StaffActivityLogCard(
  log: StaffActivityLog,
  modifier: Modifier = Modifier
) {
  val (actionIcon, actionLabel, iconBgColor) = when (log.actionType) {
    "BILL_GENERATED" -> Triple(Icons.Default.ReceiptLong, "BILL CREATED", Color(0xFF10B981))
    "STOCK_UPDATED" -> Triple(Icons.Default.Inventory2, "STOCK UPDATE", Color(0xFF2563EB))
    "STAFF_ADDED" -> Triple(Icons.Default.PersonAdd, "STAFF ADDED", Color(0xFF9C1258))
    "STAFF_REMOVED" -> Triple(Icons.Default.PersonRemove, "STAFF REMOVED", Color(0xFFEF4444))
    "PERMISSION_UPDATED" -> Triple(Icons.Default.Security, "PERMISSION CHANGED", Color(0xFF8B5CF6))
    "PO_CREATED" -> Triple(Icons.Default.LocalShipping, "PURCHASE ORDER", Color(0xFFF59E0B))
    else -> Triple(Icons.Default.CheckCircle, "ACTIVITY", Color(0xFF64748B))
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, CardBorder),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(iconBgColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = actionIcon,
          contentDescription = null,
          tint = iconBgColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = log.staffName,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F5F9))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = log.staffRole,
                fontSize = 9.sp,
                color = Color(0xFF475569),
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(iconBgColor.copy(alpha = 0.1f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = actionLabel,
              fontSize = 8.5.sp,
              color = iconBgColor,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }

        Text(
          text = log.description,
          fontSize = 12.sp,
          color = TextDark,
          lineHeight = 16.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = log.timestamp,
            fontSize = 10.sp,
            color = TextMuted
          )
          Text(
            text = "Audit Verified ✓",
            fontSize = 9.sp,
            color = Color(0xFF059669),
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
