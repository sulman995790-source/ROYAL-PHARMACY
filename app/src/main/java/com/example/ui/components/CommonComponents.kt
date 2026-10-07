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

@Composable
fun RoyalPharmacyTopHeader(
  businessName: String = "ROYAL PHARMACY",
  cartCount: Int = 0,
  onProfileClick: () -> Unit,
  onNotificationClick: () -> Unit,
  onCartClick: (() -> Unit)? = null,
  onLockClick: (() -> Unit)? = null,
  onVoiceSearchClick: (() -> Unit)? = null,
  onSyncManagerClick: (() -> Unit)? = null,
  onDarkModeToggle: (() -> Unit)? = null,
  isDarkMode: Boolean = false,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(if (isDarkMode) Color(0xFF1E293B) else Color.White)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clickable(onClick = onProfileClick)
        .testTag("header_business_selector")
    ) {
      // LocalWell-style pink square cross icon
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(if (isDarkMode) Color(0xFF831843) else RoyalMagentaLight),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.LocalPharmacy,
          contentDescription = "Royal Pharmacy Logo",
          tint = if (isDarkMode) Color(0xFFF472B6) else RoyalMagenta,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = businessName,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDarkMode) Color.White else TextDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "Add Business",
          fontSize = 12.sp,
          fontWeight = FontWeight.Normal,
          color = TextMuted
        )
      }
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      if (onDarkModeToggle != null) {
        IconButton(
          onClick = onDarkModeToggle,
          modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF334155) else Color(0xFFFEF3C7)).testTag("header_dark_mode_button")
        ) {
          Icon(
            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "Toggle Dark Mode",
            tint = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFD97706),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      if (onVoiceSearchClick != null) {
        IconButton(
          onClick = onVoiceSearchClick,
          modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF831843) else RoyalMagentaLight).testTag("header_voice_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Search",
            tint = if (isDarkMode) Color(0xFFF472B6) else RoyalMagenta,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      if (onSyncManagerClick != null) {
        IconButton(
          onClick = onSyncManagerClick,
          modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isDarkMode) Color(0xFF0F172A) else Color(0xFFE0F2FE)).testTag("header_sync_manager_button")
        ) {
          Icon(
            imageVector = Icons.Default.CloudSync,
            contentDescription = "Sync Manager",
            tint = if (isDarkMode) Color(0xFF38BDF8) else RoyalNavy,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      if (onLockClick != null) {
        IconButton(
          onClick = onLockClick,
          modifier = Modifier.size(36.dp).testTag("header_lock_button")
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock App",
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      if (onCartClick != null) {
        IconButton(
          onClick = onCartClick,
          modifier = Modifier.size(36.dp).testTag("header_cart_button")
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
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      IconButton(
        onClick = onNotificationClick,
        modifier = Modifier.size(36.dp).testTag("notification_bell_button")
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
            modifier = Modifier.size(20.dp)
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
