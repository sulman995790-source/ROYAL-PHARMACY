@file:Suppress("DEPRECATION")
package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardPayPink
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.Screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.viewmodel.AuthLoginType
import com.example.viewmodel.UserRole
import com.example.viewmodel.VisualSyncState

@Composable
fun RoyalPharmacyTopHeader(
  businessName: String = "ROYAL PHARMACY",
  cartCount: Int = 0,
  userRole: UserRole = UserRole.OWNER,
  visualSyncState: VisualSyncState = VisualSyncState.SYNCED,
  onProfileClick: () -> Unit,
  onNotificationClick: () -> Unit,
  onCartClick: (() -> Unit)? = null,
  onLockClick: (() -> Unit)? = null,
  onVoiceSearchClick: (() -> Unit)? = null,
  onSyncManagerClick: (() -> Unit)? = null,
  onRoleClick: (() -> Unit)? = null,
  onVisualSyncClick: (() -> Unit)? = null,
  onDarkModeToggle: (() -> Unit)? = null,
  isDarkMode: Boolean = false,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(if (isDarkMode) Color(0xFF1E293B) else Color.White)
      .padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clickable(onClick = onProfileClick)
        .testTag("header_business_selector")
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isDarkMode) Color(0xFF831843) else RoyalMagentaLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalPharmacy,
          contentDescription = "Royal Pharmacy Logo",
          tint = if (isDarkMode) Color(0xFFF472B6) else RoyalMagenta,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = businessName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkMode) Color.White else TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )

          Spacer(modifier = Modifier.width(6.dp))

          // User Role Badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(userRole.badgeColorHex),
            modifier = Modifier
              .clickable { onRoleClick?.invoke() }
              .testTag("header_user_role_chip")
          ) {
            Text(
              text = if (userRole == UserRole.OWNER) "OWNER" else "STAFF",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Visual Sync Status Badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(visualSyncState.statusColorHex).copy(alpha = 0.15f),
            modifier = Modifier.clickable { onVisualSyncClick?.invoke() }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(Color(visualSyncState.statusColorHex))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = visualSyncState.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(visualSyncState.statusColorHex)
              )
            }
          }
        }
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
      if (onDarkModeToggle != null) {
        IconButton(
          onClick = onDarkModeToggle,
          modifier = Modifier.size(34.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF334155) else Color(0xFFFEF3C7)).testTag("header_dark_mode_button")
        ) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "Toggle Dark Mode",
            tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFD97706),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      if (onVoiceSearchClick != null) {
        IconButton(
          onClick = onVoiceSearchClick,
          modifier = Modifier.size(34.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF831843) else RoyalMagentaLight).testTag("header_voice_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Search",
            tint = if (isDarkMode) Color(0xFFF472B6) else RoyalMagenta,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      if (onSyncManagerClick != null) {
        IconButton(
          onClick = onSyncManagerClick,
          modifier = Modifier.size(34.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFE0F2FE)).testTag("header_sync_manager_button")
        ) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = "Sync Manager",
            tint = if (isDarkMode) Color(0xFF38BDF8) else RoyalNavy,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      if (onLockClick != null) {
        IconButton(
          onClick = onLockClick,
          modifier = Modifier.size(34.dp).testTag("header_lock_button")
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock App",
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      if (onCartClick != null) {
        IconButton(
          onClick = onCartClick,
          modifier = Modifier.size(34.dp).testTag("header_cart_button")
        ) {
          BadgedBox(
            badge = {
              if (cartCount > 0) {
                Badge(containerColor = RoyalMagenta) {
                  Text("$cartCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = "Cart",
              tint = if (cartCount > 0) RoyalMagenta else TextDark,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      IconButton(
        onClick = onNotificationClick,
        modifier = Modifier.size(34.dp).testTag("notification_bell_button")
      ) {
        BadgedBox(
          badge = {
            Badge(containerColor = RoyalMagenta) {
              Text("2", color = Color.White, fontSize = 9.sp)
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = TextDark,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  value: String,
  label: String,
  onClick: () -> Unit,
  isHighlighted: Boolean = false,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isHighlighted) CardPayPink else Color.White
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(
        if (isHighlighted) Color(0xFFFFCDD2) else CardBorder
      )
    ),
    modifier = modifier.testTag("metric_card_${label.replace(" ", "_").lowercase()}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Text(
        text = value,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = label,
          fontSize = 11.sp,
          fontWeight = FontWeight.Normal,
          color = TextMuted,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = null,
          tint = TextLight,
          modifier = Modifier.size(10.dp)
        )
      }
    }
  }
}

@Composable
fun RoyalBottomNav(
  currentScreen: Screen,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 6.dp,
    modifier = modifier
  ) {
    NavigationBarItem(
      selected = currentScreen == Screen.HOME,
      onClick = { onNavigate(Screen.HOME) },
      icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
      label = { Text("Home", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RoyalMagenta,
        selectedTextColor = RoyalMagenta,
        indicatorColor = RoyalMagentaLight,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_home")
    )

    NavigationBarItem(
      selected = currentScreen == Screen.PURCHASE,
      onClick = { onNavigate(Screen.PURCHASE) },
      icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Purchase") },
      label = { Text("Purchase", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RoyalMagenta,
        selectedTextColor = RoyalMagenta,
        indicatorColor = RoyalMagentaLight,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_purchase")
    )

    NavigationBarItem(
      selected = currentScreen == Screen.STOCK,
      onClick = { onNavigate(Screen.STOCK) },
      icon = { Icon(Icons.Default.Inventory2, contentDescription = "Stock") },
      label = { Text("Stock", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RoyalMagenta,
        selectedTextColor = RoyalMagenta,
        indicatorColor = RoyalMagentaLight,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_stock")
    )

    NavigationBarItem(
      selected = currentScreen == Screen.SALES,
      onClick = { onNavigate(Screen.SALES) },
      icon = { Icon(Icons.Default.BarChart, contentDescription = "Sales") },
      label = { Text("Sales", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RoyalMagenta,
        selectedTextColor = RoyalMagenta,
        indicatorColor = RoyalMagentaLight,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_sales")
    )

    NavigationBarItem(
      selected = currentScreen == Screen.MORE,
      onClick = { onNavigate(Screen.MORE) },
      icon = { Icon(Icons.Default.GridView, contentDescription = "More") },
      label = { Text("More", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = RoyalMagenta,
        selectedTextColor = RoyalMagenta,
        indicatorColor = RoyalMagentaLight,
        unselectedIconColor = TextMuted,
        unselectedTextColor = TextMuted
      ),
      modifier = Modifier.testTag("nav_more")
    )
  }
}

@Composable
fun FloatingQuikScanButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(24.dp),
    color = RoyalNavy,
    shadowElevation = 8.dp,
    modifier = modifier.testTag("floating_quickscan_button")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Icon(
        imageVector = Icons.Default.QrCodeScanner,
        contentDescription = "QuikScan",
        tint = Color.White,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "QuikScan",
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

data class QuickActionItem(
  val id: String,
  val label: String,
  val icon: ImageVector,
  val action: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionsBottomSheet(
  onDismiss: () -> Unit,
  onActionClick: (String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val items = listOf(
    QuickActionItem("create_bill", "Create\nSale Bill", Icons.Default.TrendingUp) { onActionClick("create_bill") },
    QuickActionItem("quickscan_billing", "QuickScan\nBilling", Icons.Default.QrCodeScanner) { onActionClick("quickscan_billing") },
    QuickActionItem("udhar_khata", "Udhar\nKhata", Icons.Default.AccountBalanceWallet) { onActionClick("udhar_khata") },
    QuickActionItem("substitutes", "Generic\nSubstitutes", Icons.Default.Inventory2) { onActionClick("substitutes") },
    QuickActionItem("add_stock", "Manage\nStock", Icons.Default.Inventory2) { onActionClick("add_stock") },
    QuickActionItem("import_purchase", "Import\nPurchases", Icons.Default.AddShoppingCart) { onActionClick("import_purchase") },
    QuickActionItem("suppliers", "Supplier\nOrders", Icons.Default.LocalShipping) { onActionClick("suppliers") },
    QuickActionItem("patients", "Patient\nProfiles", Icons.Default.MedicalServices) { onActionClick("patients") },
    QuickActionItem("reports", "Analytics &\nReports", Icons.Default.BarChart) { onActionClick("reports") },
    QuickActionItem("critical_alerts", "Critical Stock\nPush Alerts", Icons.Default.NotificationsActive) { onActionClick("critical_alerts") },
    QuickActionItem("cloud_sync", "Cloud\nSync", Icons.Default.CloudSync) { onActionClick("cloud_sync") },
    QuickActionItem("ai_chatbot", "AI Pharma\nSupport", Icons.Default.AutoAwesome) { onActionClick("ai_chatbot") },
    QuickActionItem("profile", "Business\nProfile", Icons.Default.Person) { onActionClick("profile") },
    QuickActionItem("support", "Help &\nSupport", Icons.Default.HeadsetMic) { onActionClick("support") }
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 32.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Quick Actions",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
        Text(
          text = "Dismiss",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = RoyalMagenta,
          modifier = Modifier
            .clickable(onClick = onDismiss)
            .padding(8.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
      ) {
        items(items) { item ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable { item.action() }
              .testTag("quick_action_${item.id}")
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(RoyalMagentaLight),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = RoyalMagenta,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = item.label,
              fontSize = 11.sp,
              lineHeight = 14.sp,
              textAlign = TextAlign.Center,
              color = TextDark,
              maxLines = 2
            )
          }
        }
      }
    }
  }
}

@Composable
fun VisualSyncStatusDialog(
  syncState: VisualSyncState,
  pendingCount: Int,
  lastSyncTime: String,
  userEmail: String,
  onTriggerSync: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.CloudDone,
          contentDescription = null,
          tint = Color(syncState.statusColorHex),
          modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Cloud Visual Sync Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = BorderStroke(1.dp, CardBorder)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
              Text("Current Status:", fontSize = 12.sp, color = TextMuted)
              Text(
                text = syncState.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(syncState.statusColorHex)
              )
            }
            HorizontalDivider(color = Color(0xFFE2E8F0))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
              Text("Connected Account:", fontSize = 12.sp, color = TextMuted)
              Text(userEmail, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
              Text("Pending Offline Queue:", fontSize = 12.sp, color = TextMuted)
              Text("$pendingCount items pending", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
              Text("Last Synced:", fontSize = 12.sp, color = TextMuted)
              Text(lastSyncTime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
            }
          }
        }

        Text(
          text = "Real-time sync keeps your pharmacy data backed up to Google Drive & Firebase securely. You can trigger manual sync at any time.",
          fontSize = 11.5.sp,
          color = TextMuted
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onTriggerSync()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
      ) {
        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Sync Now")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}

@Composable
fun UserRoleAuthDialog(
  currentRole: UserRole,
  currentEmail: String,
  currentPhone: String,
  currentName: String,
  authType: AuthLoginType,
  ownerPin: String,
  onLoginPhone: (String, String, UserRole) -> Unit,
  onLoginGmail: (String, String, UserRole) -> Unit,
  onRoleSwitched: (UserRole, String?) -> Boolean,
  onDismiss: () -> Unit
) {
  var selectedRole by remember { mutableStateOf(currentRole) }
  var loginMode by remember { mutableStateOf(authType) }
  var phoneInput by remember { mutableStateOf(currentPhone) }
  var emailInput by remember { mutableStateOf(currentEmail) }
  var nameInput by remember { mutableStateOf(currentName) }
  var pinInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  var isOtpStepActive by remember { mutableStateOf(false) }
  var otpCodeInput by remember { mutableStateOf("") }

  if (isOtpStepActive) {
    AlertDialog(
      onDismissRequest = onDismiss,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF059669),
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("2-Step Verification", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Enter the 6-digit OTP code sent to your active ${if (loginMode == AuthLoginType.GMAIL) "Gmail ($emailInput)" else "mobile ($phoneInput)"} account.",
            fontSize = 13.sp,
            color = TextDark
          )

          OutlinedTextField(
            value = otpCodeInput,
            onValueChange = { if (it.length <= 6) otpCodeInput = it },
            label = { Text("Verification OTP Code") },
            placeholder = { Text("123456") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF059669)) },
            modifier = Modifier.fillMaxWidth()
          )

          Text(
            text = "ℹ️ Note: For simulated testing, use standard code '123456'.",
            fontSize = 11.sp,
            color = TextMuted,
            fontWeight = FontWeight.Medium
          )

          errorMessage?.let { msg ->
            Text(msg, fontSize = 11.5.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (otpCodeInput == "123456") {
              if (loginMode == AuthLoginType.GMAIL) {
                onLoginGmail(emailInput, nameInput, selectedRole)
              } else {
                onLoginPhone(phoneInput, nameInput, selectedRole)
              }
              onDismiss()
            } else {
              errorMessage = "Incorrect OTP verification code! Enter standard code '123456' to proceed."
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
        ) {
          Text("Verify & Complete Sign In")
        }
      },
      dismissButton = {
        TextButton(onClick = { 
          isOtpStepActive = false 
          otpCodeInput = ""
          errorMessage = null
        }) {
          Text("Back")
        }
      }
    )
  } else {
    AlertDialog(
      onDismissRequest = onDismiss,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AdminPanelSettings,
            contentDescription = null,
            tint = RoyalMagenta,
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("User Roles & Login Options", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Select User Role:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(
              onClick = { selectedRole = UserRole.OWNER },
              colors = CardDefaults.cardColors(
                containerColor = if (selectedRole == UserRole.OWNER) RoyalMagentaLight else Color(0xFFF8FAFC)
              ),
              border = if (selectedRole == UserRole.OWNER) BorderStroke(1.5.dp, RoyalMagenta) else BorderStroke(1.dp, CardBorder),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👑 OWNER", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
                Text("Full Access - Edit All", fontSize = 10.sp, color = TextMuted)
              }
            }

            Card(
              onClick = { selectedRole = UserRole.STAFF },
              colors = CardDefaults.cardColors(
                containerColor = if (selectedRole == UserRole.STAFF) Color(0xFFEFF6FF) else Color(0xFFF8FAFC)
              ),
              border = if (selectedRole == UserRole.STAFF) BorderStroke(1.5.dp, Color(0xFF2563EB)) else BorderStroke(1.dp, CardBorder),
              modifier = Modifier.weight(1f)
            ) {
              Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💼 STAFF", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                Text("General Work & Billing", fontSize = 10.sp, color = TextMuted)
              }
            }
          }

          if (selectedRole == UserRole.OWNER && currentRole != UserRole.OWNER) {
            OutlinedTextField(
              value = pinInput,
              onValueChange = { pinInput = it },
              label = { Text("Enter Owner PIN (Default: 1234)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }

          HorizontalDivider(color = Color(0xFFE2E8F0))

          Text("Login Options:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = loginMode == AuthLoginType.GMAIL, onClick = { loginMode = AuthLoginType.GMAIL })
            Text("Gmail Account", fontSize = 13.sp, modifier = Modifier.clickable { loginMode = AuthLoginType.GMAIL })
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = loginMode == AuthLoginType.PHONE, onClick = { loginMode = AuthLoginType.PHONE })
            Text("Phone Number", fontSize = 13.sp, modifier = Modifier.clickable { loginMode = AuthLoginType.PHONE })
          }

          OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("User Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          if (loginMode == AuthLoginType.GMAIL) {
            OutlinedTextField(
              value = emailInput,
              onValueChange = { emailInput = it },
              label = { Text("Gmail Address") },
              singleLine = true,
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = RoyalMagenta) },
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            OutlinedTextField(
              value = phoneInput,
              onValueChange = { phoneInput = it },
              label = { Text("Mobile Phone Number") },
              singleLine = true,
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF2563EB)) },
              modifier = Modifier.fillMaxWidth()
            )
          }

          errorMessage?.let { msg ->
            Text(msg, fontSize = 11.5.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Medium)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (selectedRole != currentRole) {
              val success = onRoleSwitched(selectedRole, pinInput)
              if (!success) {
                errorMessage = "Incorrect Owner PIN! Enter default PIN '1234'."
                return@Button
              }
              // If switching to Staff without new credentials, apply switch immediately
              if (selectedRole == UserRole.STAFF && emailInput.isBlank() && phoneInput.isBlank()) {
                onDismiss()
                return@Button
              }
            }
            if (loginMode == AuthLoginType.GMAIL && emailInput.isBlank()) {
              errorMessage = "Gmail address cannot be empty!"
              return@Button
            }
            if (loginMode == AuthLoginType.PHONE && phoneInput.isBlank()) {
              errorMessage = "Phone number cannot be empty!"
              return@Button
            }
            // Transition to OTP Code Verification step
            errorMessage = null
            isOtpStepActive = true
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
        ) {
          Text(if (selectedRole == UserRole.STAFF && emailInput.isBlank() && phoneInput.isBlank()) "Switch to Staff Mode" else "Get OTP Code")
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun OwnerPinVerificationDialog(
  actionTitle: String?,
  errorMessage: String?,
  onVerifyPin: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var pin by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Owner Permission Required", fontSize = 17.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Staff mode is active. Entering Owner PIN is required to perform '${actionTitle ?: "restricted edit"}'.",
          fontSize = 12.5.sp,
          color = TextDark
        )

        OutlinedTextField(
          value = pin,
          onValueChange = { pin = it },
          label = { Text("Enter Owner PIN (Default: 1234)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let { msg ->
          Text(msg, fontSize = 11.5.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onVerifyPin(pin) },
        colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
      ) {
        Text("Authorize & Proceed")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
