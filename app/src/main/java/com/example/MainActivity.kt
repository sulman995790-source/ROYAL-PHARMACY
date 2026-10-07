package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.service.StockAlertNotificationService
import com.example.ui.components.OwnerPinVerificationDialog
import com.example.ui.components.QuickActionsBottomSheet
import com.example.ui.components.RoyalBottomNav
import com.example.ui.components.UserRoleAuthDialog
import com.example.ui.components.VisualSyncStatusDialog
import com.example.ui.screens.AddSaleScreen
import com.example.ui.screens.AiChatbotScreen
import com.example.ui.screens.AppLockScreen
import com.example.ui.screens.AutomatedTaxCalculatorScreen
import com.example.ui.screens.BackupRestoreScreen
import com.example.ui.screens.BatchExpiryDashboardScreen
import com.example.ui.screens.BatchTrackingScreen
import com.example.ui.screens.BrandCatalogScreen
import com.example.ui.screens.BusinessProfileScreen
import com.example.ui.screens.CashBankAccountsScreen
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.CriticalStockAlertsScreen
import com.example.ui.screens.CustomerHistoryScreen
import com.example.ui.screens.DailyHuddleScreen
import com.example.ui.screens.DailySalesReportScreen
import com.example.ui.screens.DiseaseTrackerScreen
import com.example.ui.screens.DistributorCartScreen
import com.example.ui.screens.DoctorManagementScreen
import com.example.ui.screens.DrugInteractionCheckerScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventoryDashboardScreen
import com.example.ui.screens.InventoryQrScreen
import com.example.ui.screens.InvoicePrinterScreen
import com.example.ui.screens.InvoiceReceiptDialog
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.PatientManagementScreen
import com.example.ui.screens.PrescriptionHistoryScreen
import com.example.ui.screens.PrescriptionScannerScreen
import com.example.ui.screens.PurchaseOrdersScreen
import com.example.ui.screens.PurchasesScreen
import com.example.ui.screens.QuickScanScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SearchAnythingScreen
import com.example.ui.screens.SmartDosageCalculatorScreen
import com.example.ui.screens.SmartInventorySuggestionScreen
import com.example.ui.screens.SmartSalesAnalyticsScreen
import com.example.ui.screens.StockAnalyticsScreen
import com.example.ui.screens.StockScreen
import com.example.ui.screens.SubstitutesScreen
import com.example.ui.screens.SupplierManagementScreen
import com.example.ui.screens.SyncManagerScreen
import com.example.ui.screens.UdharKhataScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.screens.VoiceSearchScreen
import com.example.ui.theme.RoyalPharmacyTheme
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
  private val viewModel: PharmacyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    handleIntent(intent)
    setContent {
      val isDarkMode by viewModel.isDarkMode.collectAsState()
      RoyalPharmacyTheme(darkTheme = isDarkMode) {
        RoyalPharmacyApp(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    val target = intent?.getStringExtra(StockAlertNotificationService.EXTRA_TARGET_SCREEN)
    when {
      target == "CRITICAL_STOCK" -> viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS)
      target == "CLOUD_SYNC" -> viewModel.navigateTo(Screen.CLOUD_SYNC)
      target == "CART" || intent?.action == StockAlertNotificationService.ACTION_ADD_TO_CART -> viewModel.navigateTo(Screen.CART)
      target == "BRAND_CATALOG" -> viewModel.navigateTo(Screen.BRAND_CATALOG)
      target == "BATCH_EXPIRY" || target == "EXPIRY_DASHBOARD" -> viewModel.navigateTo(Screen.BATCH_EXPIRY_DASHBOARD)
      target == "SMART_SALES" -> viewModel.navigateTo(Screen.SMART_SALES_ANALYTICS)
      target == "SUPPLIERS" || intent?.action == StockAlertNotificationService.ACTION_CREATE_PO -> viewModel.navigateTo(Screen.SUPPLIERS)
      target == "STOCK" || intent?.action == StockAlertNotificationService.ACTION_VIEW_STOCK -> viewModel.navigateTo(Screen.STOCK)
    }
  }
}

@Composable
fun RoyalPharmacyApp(
  viewModel: PharmacyViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val isQuickActionsOpen by viewModel.isQuickActionsOpen.collectAsState()
  val showReceiptDialog by viewModel.showReceiptDialog.collectAsState()
  val lastInvoice by viewModel.lastGeneratedInvoice.collectAsState()
  val profile by viewModel.businessProfile.collectAsState()
  val isAppLocked by viewModel.isAppLocked.collectAsState()

  // User Role and Visual Sync Dialog States
  val showUserRoleAuthDialog by viewModel.showUserRoleAuthDialog.collectAsState()
  val showVisualSyncStatusSheet by viewModel.showVisualSyncStatusSheet.collectAsState()
  val showOwnerPinAuthDialog by viewModel.showOwnerPinAuthDialog.collectAsState()
  val currentUserRole by viewModel.currentUserRole.collectAsState()
  val visualSyncState by viewModel.visualSyncState.collectAsState()
  val pendingSyncQueueCount by viewModel.pendingSyncQueueCount.collectAsState()
  val lastSyncTimeDisplay by viewModel.lastSyncTimeDisplay.collectAsState()
  val currentUserEmail by viewModel.currentUserEmail.collectAsState()
  val currentUserPhone by viewModel.currentUserPhone.collectAsState()
  val currentUserName by viewModel.currentUserName.collectAsState()
  val authLoginType by viewModel.authLoginType.collectAsState()
  val ownerPin by viewModel.ownerPin.collectAsState()
  val ownerPinErrorMessage by viewModel.ownerPinErrorMessage.collectAsState()
  val pendingRestrictedActionName by viewModel.pendingRestrictedActionName.collectAsState()

  // Full-screen Biometric & PIN Security Lock
  if (isAppLocked) {
    AppLockScreen(viewModel = viewModel)
    return
  }

  // Handle system back navigation according to Guidelines
  BackHandler(enabled = currentScreen != Screen.HOME) {
    when (currentScreen) {
      Screen.ADD_SALE, Screen.BILLING -> viewModel.navigateTo(Screen.SALES)
      Screen.QUICK_SCAN -> viewModel.navigateTo(Screen.HOME)
      Screen.BUSINESS_PROFILE -> viewModel.navigateTo(Screen.HOME)
      Screen.EDIT_PROFILE -> viewModel.navigateTo(Screen.MORE)
      Screen.SEARCH_ANYTHING -> viewModel.navigateTo(Screen.HOME)
      Screen.REPORTS -> viewModel.navigateTo(Screen.HOME)
      Screen.SUPPLIERS -> viewModel.navigateTo(Screen.PURCHASE)
      Screen.PATIENTS -> viewModel.navigateTo(Screen.MORE)
      Screen.SUBSTITUTES -> viewModel.navigateTo(Screen.STOCK)
      Screen.UDHAR_KHATA -> viewModel.navigateTo(Screen.SALES)
      Screen.AI_CHATBOT -> viewModel.navigateTo(Screen.HOME)
      Screen.CRITICAL_STOCK_ALERTS -> viewModel.navigateTo(Screen.HOME)
      Screen.CLOUD_SYNC -> viewModel.navigateTo(Screen.HOME)
      Screen.BRAND_CATALOG -> viewModel.navigateTo(Screen.HOME)
      Screen.CART -> viewModel.navigateTo(Screen.HOME)
      Screen.SMART_SALES_ANALYTICS -> viewModel.navigateTo(Screen.SALES)
      Screen.BATCH_EXPIRY_DASHBOARD, Screen.EXPIRY_TRACKER -> viewModel.navigateTo(Screen.HOME)
      Screen.INVOICE_PRINTER, Screen.DAILY_SALES_REPORT, Screen.CUSTOMER_HISTORY, Screen.AUTOMATED_TAX_CALC -> viewModel.navigateTo(Screen.SALES)
      Screen.INVENTORY_DASHBOARD, Screen.BATCH_TRACKING, Screen.STOCK_ANALYTICS -> viewModel.navigateTo(Screen.STOCK)
      Screen.PURCHASE_ORDERS -> viewModel.navigateTo(Screen.PURCHASE)
      Screen.VOICE_SEARCH, Screen.SYNC_MANAGER, Screen.PRESCRIPTION_SCANNER, Screen.DAILY_HUDDLE, Screen.BACKUP_RESTORE, Screen.INVENTORY_QR -> viewModel.navigateTo(Screen.HOME)
      Screen.CASH_BANK_ACCOUNTS, Screen.PRESCRIPTION_HISTORY, Screen.SMART_DOSAGE_CALCULATOR, Screen.UNIT_CONVERTER, Screen.DRUG_INTERACTION_CHECKER, Screen.SMART_INVENTORY_SUGGESTIONS, Screen.DOCTOR_MANAGEMENT, Screen.SYMPTOM_DISEASE_TRACKER -> viewModel.navigateTo(Screen.MORE)
      else -> viewModel.navigateTo(Screen.HOME)
    }
  }

  val isMainTab = currentScreen in listOf(
    Screen.HOME,
    Screen.PURCHASE,
    Screen.STOCK,
    Screen.INVENTORY,
    Screen.SALES,
    Screen.MORE
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (isMainTab) {
        RoyalBottomNav(
          currentScreen = currentScreen,
          onNavigate = { screen -> viewModel.navigateTo(screen) }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        Screen.HOME -> HomeScreen(viewModel = viewModel)
        Screen.PURCHASE -> PurchasesScreen(viewModel = viewModel)
        Screen.STOCK, Screen.INVENTORY -> StockScreen(viewModel = viewModel)
        Screen.SALES -> SalesScreen(viewModel = viewModel)
        Screen.MORE -> MoreScreen(viewModel = viewModel)
        Screen.ADD_SALE, Screen.BILLING -> AddSaleScreen(viewModel = viewModel)
        Screen.QUICK_SCAN -> QuickScanScreen(viewModel = viewModel)
        Screen.BUSINESS_PROFILE -> BusinessProfileScreen(viewModel = viewModel)
        Screen.EDIT_PROFILE -> EditProfileScreen(viewModel = viewModel)
        Screen.SEARCH_ANYTHING -> SearchAnythingScreen(viewModel = viewModel)
        Screen.REPORTS -> ReportsScreen(viewModel = viewModel)
        Screen.SUPPLIERS -> SupplierManagementScreen(viewModel = viewModel)
        Screen.PATIENTS -> PatientManagementScreen(viewModel = viewModel)
        Screen.SUBSTITUTES -> SubstitutesScreen(viewModel = viewModel)
        Screen.UDHAR_KHATA -> UdharKhataScreen(viewModel = viewModel)
        Screen.AI_CHATBOT -> AiChatbotScreen(viewModel = viewModel)
        Screen.CRITICAL_STOCK_ALERTS -> CriticalStockAlertsScreen(viewModel = viewModel)
        Screen.CLOUD_SYNC -> CloudSyncScreen(viewModel = viewModel)
        Screen.BRAND_CATALOG -> BrandCatalogScreen(viewModel = viewModel)
        Screen.CART -> DistributorCartScreen(viewModel = viewModel)
        Screen.SMART_SALES_ANALYTICS -> SmartSalesAnalyticsScreen(viewModel = viewModel)
        Screen.BATCH_EXPIRY_DASHBOARD, Screen.EXPIRY_TRACKER -> BatchExpiryDashboardScreen(viewModel = viewModel)
        Screen.INVOICE_PRINTER -> InvoicePrinterScreen(viewModel = viewModel)
        Screen.INVENTORY_DASHBOARD -> InventoryDashboardScreen(viewModel = viewModel)
        Screen.BATCH_TRACKING -> BatchTrackingScreen(viewModel = viewModel)
        Screen.PURCHASE_ORDERS -> PurchaseOrdersScreen(viewModel = viewModel)
        Screen.DAILY_SALES_REPORT -> DailySalesReportScreen(viewModel = viewModel)
        Screen.CUSTOMER_HISTORY -> CustomerHistoryScreen(viewModel = viewModel)
        Screen.STOCK_ANALYTICS -> StockAnalyticsScreen(viewModel = viewModel)
        Screen.AUTOMATED_TAX_CALC -> AutomatedTaxCalculatorScreen(viewModel = viewModel)
        Screen.VOICE_SEARCH -> VoiceSearchScreen(viewModel = viewModel)
        Screen.SYNC_MANAGER -> SyncManagerScreen(viewModel = viewModel)
        Screen.PRESCRIPTION_SCANNER -> PrescriptionScannerScreen(viewModel = viewModel)
        Screen.DAILY_HUDDLE -> DailyHuddleScreen(viewModel = viewModel)
        Screen.BACKUP_RESTORE -> BackupRestoreScreen(viewModel = viewModel)
        Screen.INVENTORY_QR -> InventoryQrScreen(viewModel = viewModel)
        Screen.CASH_BANK_ACCOUNTS -> CashBankAccountsScreen(viewModel = viewModel)
        Screen.PRESCRIPTION_HISTORY -> PrescriptionHistoryScreen(viewModel = viewModel)
        Screen.SMART_DOSAGE_CALCULATOR -> SmartDosageCalculatorScreen(viewModel = viewModel)
        Screen.UNIT_CONVERTER -> UnitConverterScreen(viewModel = viewModel)
        Screen.DRUG_INTERACTION_CHECKER -> DrugInteractionCheckerScreen(viewModel = viewModel)
        Screen.SMART_INVENTORY_SUGGESTIONS -> SmartInventorySuggestionScreen(viewModel = viewModel)
        Screen.DOCTOR_MANAGEMENT -> DoctorManagementScreen(viewModel = viewModel)
        Screen.SYMPTOM_DISEASE_TRACKER -> DiseaseTrackerScreen(viewModel = viewModel)
      }

      // Quick Actions Bottom Sheet
      if (isQuickActionsOpen) {
        QuickActionsBottomSheet(
          onDismiss = { viewModel.toggleQuickActions(false) },
          onActionClick = { actionId ->
            viewModel.toggleQuickActions(false)
            when (actionId) {
              "add_stock" -> viewModel.navigateTo(Screen.STOCK)
              "import_purchase" -> viewModel.navigateTo(Screen.PURCHASE)
              "create_bill" -> viewModel.navigateTo(Screen.ADD_SALE)
              "quickscan_billing" -> viewModel.navigateTo(Screen.QUICK_SCAN)
              "prescription_scanner" -> viewModel.navigateTo(Screen.PRESCRIPTION_SCANNER)
              "daily_huddle" -> viewModel.navigateTo(Screen.DAILY_HUDDLE)
              "backup_restore" -> viewModel.navigateTo(Screen.BACKUP_RESTORE)
              "inventory_qr" -> viewModel.navigateTo(Screen.INVENTORY_QR)
              "drug_interactions" -> viewModel.navigateTo(Screen.DRUG_INTERACTION_CHECKER)
              "smart_inventory" -> viewModel.navigateTo(Screen.SMART_INVENTORY_SUGGESTIONS)
              "profile" -> viewModel.navigateTo(Screen.BUSINESS_PROFILE)
              "reports" -> viewModel.navigateTo(Screen.REPORTS)
              "daily_sales" -> viewModel.navigateTo(Screen.DAILY_SALES_REPORT)
              "suppliers" -> viewModel.navigateTo(Screen.SUPPLIERS)
              "patients" -> viewModel.navigateTo(Screen.PATIENTS)
              "substitutes" -> viewModel.navigateTo(Screen.SUBSTITUTES)
              "udhar_khata" -> viewModel.navigateTo(Screen.UDHAR_KHATA)
              "ai_chatbot" -> viewModel.navigateTo(Screen.AI_CHATBOT)
              "critical_alerts" -> viewModel.navigateTo(Screen.CRITICAL_STOCK_ALERTS)
              "cloud_sync" -> viewModel.navigateTo(Screen.CLOUD_SYNC)
              "brand_catalog" -> viewModel.navigateTo(Screen.BRAND_CATALOG)
              "cart" -> viewModel.navigateTo(Screen.CART)
              else -> viewModel.navigateTo(Screen.HOME)
            }
          }
        )
      }

      // Tax Invoice Receipt Print/Share Dialog
      if (showReceiptDialog && lastInvoice != null) {
        InvoiceReceiptDialog(
          invoice = lastInvoice!!,
          profile = profile,
          onDismiss = { viewModel.showReceiptDialog.value = false },
          onOpenPrinterStation = {
            viewModel.openInvoicePrinter(lastInvoice)
          }
        )
      }

      // Visual Sync Status Dialog Overlay
      if (showVisualSyncStatusSheet) {
        VisualSyncStatusDialog(
          syncState = visualSyncState,
          pendingCount = pendingSyncQueueCount,
          lastSyncTime = lastSyncTimeDisplay,
          userEmail = currentUserEmail,
          onTriggerSync = { viewModel.triggerManualVisualSync() },
          onDismiss = { viewModel.showVisualSyncStatusSheet.value = false }
        )
      }

      // User Role & Login Options Dialog Overlay
      if (showUserRoleAuthDialog) {
        UserRoleAuthDialog(
          currentRole = currentUserRole,
          currentEmail = currentUserEmail,
          currentPhone = currentUserPhone,
          currentName = currentUserName,
          authType = authLoginType,
          ownerPin = ownerPin,
          onLoginPhone = { phone, name, role -> viewModel.loginWithPhone(phone, name, role) },
          onLoginGmail = { email, name, role -> viewModel.loginWithGmail(email, name, role) },
          onRoleSwitched = { newRole, pin -> viewModel.switchUserRole(newRole, pin) },
          onDismiss = { viewModel.showUserRoleAuthDialog.value = false }
        )
      }

      // Owner Security PIN Verification Dialog Overlay
      if (showOwnerPinAuthDialog) {
        OwnerPinVerificationDialog(
          actionTitle = pendingRestrictedActionName,
          errorMessage = ownerPinErrorMessage,
          onVerifyPin = { pin -> viewModel.verifyOwnerPinAndProceed(pin) },
          onDismiss = { viewModel.showOwnerPinAuthDialog.value = false }
        )
      }
    }
  }
}
