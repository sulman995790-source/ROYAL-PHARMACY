package com.example.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.ImagePickerDialog
import com.example.ui.components.ImagePickerType
import com.example.ui.components.LocationPickerDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalMagentaLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextLight
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.businessProfile.collectAsState()

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val tabs = listOf("BASIC", "LICENSE", "TAXATION", "LOCATION", "TIMINGS", "DATA IMPORT")

  // Editable Form states initialized from persistent profile
  var businessName by remember(profile) { mutableStateOf(profile.businessName) }
  var ayushmanHfrId by remember(profile) { mutableStateOf(profile.ayushmanHfrId) }
  var dlForm20 by remember(profile) { mutableStateOf(profile.drugLicenseForm20) }
  var form20Expiry by remember(profile) { mutableStateOf(profile.form20Expiry) }
  var dlForm21 by remember(profile) { mutableStateOf(profile.drugLicenseForm21) }
  var form21Expiry by remember(profile) { mutableStateOf(profile.form21Expiry) }
  var entityType by remember(profile) { mutableStateOf(profile.entityType) }
  var isTurnoverBelowLimit by remember(profile) { mutableStateOf(profile.isTurnoverBelowGstLimit) }
  var gstin by remember(profile) { mutableStateOf(profile.gstin) }
  var pan by remember(profile) { mutableStateOf(profile.pan) }
  var tradeName by remember(profile) { mutableStateOf(profile.tradeName) }
  var isCompositionScheme by remember(profile) { mutableStateOf(profile.isCompositionScheme) }
  var address1 by remember(profile) { mutableStateOf(profile.addressLine1) }
  var address2 by remember(profile) { mutableStateOf(profile.addressLine2) }
  var address3 by remember(profile) { mutableStateOf(profile.addressLine3) }
  var timings by remember(profile) { mutableStateOf(profile.timings) }
  var businessFrontImage by remember(profile) { mutableStateOf(profile.businessFrontImageUri) }
  var form20Image by remember(profile) { mutableStateOf(profile.form20ImageUri) }
  var form21Image by remember(profile) { mutableStateOf(profile.form21ImageUri) }
  var currentLat by remember(profile) { mutableDoubleStateOf(profile.latitude) }
  var currentLng by remember(profile) { mutableDoubleStateOf(profile.longitude) }

  var showLocationPicker by remember { mutableStateOf(false) }
  var showSavedBanner by remember { mutableStateOf(false) }

  fun saveProfile() {
    viewModel.saveBusinessProfile(
      profile.copy(
        businessName = businessName,
        ayushmanHfrId = ayushmanHfrId,
        drugLicenseForm20 = dlForm20,
        form20Expiry = form20Expiry,
        drugLicenseForm21 = dlForm21,
        form21Expiry = form21Expiry,
        entityType = entityType,
        isTurnoverBelowGstLimit = isTurnoverBelowLimit,
        gstin = gstin,
        pan = pan,
        tradeName = tradeName,
        isCompositionScheme = isCompositionScheme,
        addressLine1 = address1,
        addressLine2 = address2,
        addressLine3 = address3,
        timings = timings,
        businessFrontImageUri = businessFrontImage,
        form20ImageUri = form20Image,
        form21ImageUri = form21Image,
        latitude = currentLat,
        longitude = currentLng
      )
    )
    showSavedBanner = true
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // 1. Header (Screenshot 12)
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
          text = "Business Profile",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
      }

      // 2. Tabs Row: BASIC | LICENSE | TAXATION | LOCATION | TIMINGS (Screenshots 12-15)
      ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = RoyalNavy,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = RoyalNavy
          )
        }
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTabIndex == index) RoyalNavy else TextMuted
              )
            },
            modifier = Modifier.testTag("tab_profile_$title")
          )
        }
      }

      // Save confirmation notification
      if (showSavedBanner) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(StatusGreen)
            .padding(vertical = 6.dp, horizontal = 16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Business Profile saved successfully!", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 3. Tab Contents
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        when (selectedTabIndex) {
          0 -> {
            // TAB 0: BASIC (Screenshot 12)
            item {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                  value = businessName,
                  onValueChange = { businessName = it },
                  label = { Text("Business Name*") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_business_name")
                )

                OutlinedTextField(
                  value = ayushmanHfrId,
                  onValueChange = { if (it.length <= 12) ayushmanHfrId = it },
                  label = { Text("Ayushman HFR ID") },
                  placeholder = { Text("Enter HFR Id") },
                  supportingText = { Text("${ayushmanHfrId.length}/12") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_hfr_id")
                )

                // Business Front Picture Box (Screenshot 12)
                ProfileImageUploadBox(
                  label = "Business Front Picture",
                  imagePath = businessFrontImage,
                  pickerType = ImagePickerType.STORE_FRONT,
                  onImagePicked = {
                    businessFrontImage = it
                    viewModel.saveBusinessProfile(profile.copy(businessFrontImageUri = it))
                    showSavedBanner = true
                  },
                  onRemove = {
                    businessFrontImage = ""
                    viewModel.saveBusinessProfile(profile.copy(businessFrontImageUri = ""))
                  },
                  note = "Note: Store nameboard should be clearly visible in the picture"
                )

                Spacer(modifier = Modifier.height(10.dp))
                SaveButton(onClick = { saveProfile() })
              }
            }
          }

          1 -> {
            // TAB 1: LICENSE (Screenshot 13)
            item {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                  value = dlForm20,
                  onValueChange = { dlForm20 = it },
                  label = { Text("Drug License No - Form 20*") },
                  placeholder = { Text("Enter Drug License Number - Form 20") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_dl_form_20")
                )

                OutlinedTextField(
                  value = form20Expiry,
                  onValueChange = { form20Expiry = it },
                  label = { Text("Form 20 Expiry*") },
                  placeholder = { Text("Select Expiry Date") },
                  trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                  },
                  modifier = Modifier.fillMaxWidth().testTag("profile_form_20_expiry")
                )

                // Form 20 Image Upload Box (Screenshot 13)
                ProfileImageUploadBox(
                  label = "Form 20 Image*",
                  imagePath = form20Image,
                  pickerType = ImagePickerType.LICENSE_DOCUMENT,
                  onImagePicked = {
                    form20Image = it
                    viewModel.saveBusinessProfile(profile.copy(form20ImageUri = it))
                    showSavedBanner = true
                  },
                  onRemove = {
                    form20Image = ""
                    viewModel.saveBusinessProfile(profile.copy(form20ImageUri = ""))
                  },
                  note = "Upload clear copy of Drug License Form 20"
                )

                OutlinedTextField(
                  value = dlForm21,
                  onValueChange = { dlForm21 = it },
                  label = { Text("Drug License No - Form 21") },
                  placeholder = { Text("Enter Drug License Number - Form 21") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_dl_form_21")
                )

                OutlinedTextField(
                  value = form21Expiry,
                  onValueChange = { form21Expiry = it },
                  label = { Text("Form 21 Expiry") },
                  placeholder = { Text("Select Expiry Date") },
                  trailingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                  },
                  modifier = Modifier.fillMaxWidth().testTag("profile_form_21_expiry")
                )

                // Form 21 Image Upload Box
                ProfileImageUploadBox(
                  label = "Form 21 Image",
                  imagePath = form21Image,
                  pickerType = ImagePickerType.LICENSE_DOCUMENT,
                  onImagePicked = {
                    form21Image = it
                    viewModel.saveBusinessProfile(profile.copy(form21ImageUri = it))
                    showSavedBanner = true
                  },
                  onRemove = {
                    form21Image = ""
                    viewModel.saveBusinessProfile(profile.copy(form21ImageUri = ""))
                  },
                  note = "Upload clear copy of Drug License Form 21"
                )

                Spacer(modifier = Modifier.height(10.dp))
                SaveButton(onClick = { saveProfile() })
              }
            }
          }

          2 -> {
            // TAB 2: TAXATION (Screenshot 14)
            item {
              var entityExpanded by remember { mutableStateOf(false) }
              val entities = listOf("Proprietorship", "Partnership", "Private Limited Company", "LLP", "Public Limited")

              Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("ENTITY TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)

                ExposedDropdownMenuBox(
                  expanded = entityExpanded,
                  onExpandedChange = { entityExpanded = !entityExpanded }
                ) {
                  OutlinedTextField(
                    value = entityType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Business Entity") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = entityExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                  )
                  ExposedDropdownMenu(
                    expanded = entityExpanded,
                    onDismissRequest = { entityExpanded = false }
                  ) {
                    entities.forEach { opt ->
                      DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                          entityType = opt
                          entityExpanded = false
                        }
                      )
                    }
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { isTurnoverBelowLimit = !isTurnoverBelowLimit }
                ) {
                  Checkbox(
                    checked = isTurnoverBelowLimit,
                    onCheckedChange = { isTurnoverBelowLimit = it },
                    colors = CheckboxDefaults.colors(checkedColor = RoyalMagenta)
                  )
                  Text("Our annual turnover is less than GST prescribed limit", fontSize = 12.sp, color = TextDark)
                }

                OutlinedTextField(
                  value = gstin,
                  onValueChange = { if (it.length <= 15) gstin = it },
                  label = { Text("GSTIN*") },
                  placeholder = { Text("Enter GSTIN") },
                  supportingText = { Text("${gstin.length}/15") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_gstin")
                )

                OutlinedTextField(
                  value = pan,
                  onValueChange = { if (it.length <= 10) pan = it },
                  label = { Text("BUSINESS PAN") },
                  placeholder = { Text("Enter Business PAN Here") },
                  supportingText = { Text("${pan.length}/10") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_pan")
                )

                OutlinedTextField(
                  value = tradeName,
                  onValueChange = { tradeName = it },
                  label = { Text("TRADE NAME ON GST RECORDS") },
                  placeholder = { Text("Business trade name will appear here.") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_trade_name")
                )

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { isCompositionScheme = !isCompositionScheme }
                ) {
                  Checkbox(
                    checked = isCompositionScheme,
                    onCheckedChange = { isCompositionScheme = it },
                    colors = CheckboxDefaults.colors(checkedColor = RoyalMagenta)
                  )
                  Text("Are you registered under the Composition scheme?", fontSize = 12.sp, color = TextDark)
                }

                Spacer(modifier = Modifier.height(10.dp))
                SaveButton(onClick = { saveProfile() })
              }
            }
          }

          3 -> {
            // TAB 3: LOCATION (Screenshot 15)
            item {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                  value = address1,
                  onValueChange = { if (it.length <= 56) address1 = it },
                  label = { Text("BUSINESS ADDRESS LINE 1*") },
                  placeholder = { Text("Darrang, Assam - 784146") },
                  supportingText = { Text("${address1.length}/56") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_address_1")
                )

                OutlinedTextField(
                  value = address2,
                  onValueChange = { address2 = it },
                  label = { Text("BUSINESS ADDRESS LINE 2") },
                  placeholder = { Text("Enter Street Name, Area Name Here...") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_address_2")
                )

                OutlinedTextField(
                  value = address3,
                  onValueChange = { address3 = it },
                  label = { Text("BUSINESS ADDRESS LINE 3") },
                  placeholder = { Text("Darrang, Assam - 784146") },
                  modifier = Modifier.fillMaxWidth().testTag("profile_address_3")
                )

                // Coordinates preview card
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = GrayBackground),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFCE4EC)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text("Pinned Store Coordinates:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                      Text(
                        "%.4f° N, %.4f° E (Darrang District, Assam)".format(currentLat, currentLng),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0369A1)
                      )
                    }
                  }
                }

                // Update Location from Map Button (Screenshot 15)
                Button(
                  onClick = { showLocationPicker = true },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_update_location_map")
                ) {
                  Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Update Location from Map", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                SaveButton(onClick = { saveProfile() })
              }
            }
          }

          4 -> {
            // TAB 4: TIMINGS
            item {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                  value = timings,
                  onValueChange = { timings = it },
                  label = { Text("Store Operating Hours") },
                  placeholder = { Text("08:00 AM - 10:30 PM (Mon-Sun)") },
                  modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                SaveButton(onClick = { saveProfile() })
              }
            }
          }

          5 -> {
            // TAB 5: DATA IMPORT
            item {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                  "Bulk Update Inventory",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextDark
                )
                Text(
                  "Import an Excel file (.xlsx) to bulk update medicine prices and purchase costs. Matching is done by medicine name (case-insensitive).",
                  fontSize = 12.sp,
                  color = TextMuted
                )

                val context = LocalContext.current
                val excelPickerLauncher = rememberLauncherForActivityResult(
                  contract = ActivityResultContracts.OpenDocument(),
                  onResult = { uri ->
                    uri?.let { viewModel.importMedicinesFromExcel(context, it) }
                  }
                )

                Button(
                  onClick = {
                    excelPickerLauncher.launch(
                      arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    )
                  },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_import_excel")
                ) {
                  Icon(
                    Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    "Select Excel File (.xlsx)",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                  )
                }

                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = GrayBackground),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      "Excel Format Requirements:",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = TextDark
                    )
                    Text("• Column A: Medicine Name (Exact match)", fontSize = 10.sp, color = TextMuted)
                    Text("• Column B: MRP (Numeric)", fontSize = 10.sp, color = TextMuted)
                    Text("• Column C: Purchase Rate (Numeric)", fontSize = 10.sp, color = TextMuted)
                    Text("• Column D: Sale Rate (Numeric)", fontSize = 10.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      "Note: Ensure your Excel file has a header row. Data should start from Row 2.",
                      fontSize = 9.sp,
                      color = RoyalMagenta,
                      fontWeight = FontWeight.SemiBold
                    )
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(50.dp))
        }
      }
    }

    if (showLocationPicker) {
      LocationPickerDialog(
        currentAddress1 = address1,
        currentAddress2 = address2,
        currentAddress3 = address3,
        onLocationSelected = { newA1, newA2, newA3, lat, lng ->
          address1 = newA1
          address2 = newA2
          address3 = newA3
          currentLat = lat
          currentLng = lng
          showLocationPicker = false

          // Auto-save immediately to database
          viewModel.saveBusinessProfile(
            profile.copy(
              businessName = businessName,
              addressLine1 = newA1,
              addressLine2 = newA2,
              addressLine3 = newA3,
              latitude = lat,
              longitude = lng,
              businessFrontImageUri = businessFrontImage,
              form20ImageUri = form20Image,
              form21ImageUri = form21Image
            )
          )
          showSavedBanner = true
        },
        onDismiss = { showLocationPicker = false }
      )
    }
  }
}

@Composable
fun ProfileImageUploadBox(
  label: String,
  imagePath: String,
  pickerType: ImagePickerType = ImagePickerType.STORE_FRONT,
  onImagePicked: (String) -> Unit,
  onRemove: () -> Unit,
  note: String? = null
) {
  var showPicker by remember { mutableStateOf(false) }

  Column {
    Text(label, fontSize = 12.sp, color = TextMuted)
    Spacer(modifier = Modifier.height(6.dp))

    if (imagePath.isNotBlank()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(RoundedCornerShape(8.dp))
          .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
      ) {
        AsyncImage(
          model = java.io.File(imagePath).takeIf { it.exists() } ?: imagePath,
          contentDescription = label,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Row(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { showPicker = true },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Edit, contentDescription = "Change", tint = Color.White, modifier = Modifier.size(16.dp))
          }
          IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
          }
        }
      }
    } else {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFF3F4F6))
          .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
          .clickable { showPicker = true },
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFE1BEE7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.FileUpload,
              contentDescription = "Upload",
              tint = Color(0xFF6A1B9A),
              modifier = Modifier.size(26.dp)
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text("Click to Upload", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
          Text("Camera, Gallery & Sample Photos", fontSize = 10.sp, color = TextMuted)
        }
      }
    }

    if (note != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(note, fontSize = 11.sp, color = TextMuted)
    }
  }

  if (showPicker) {
    ImagePickerDialog(
      title = label,
      pickerType = pickerType,
      onImageSelected = { path ->
        onImagePicked(path)
        showPicker = false
      },
      onDismiss = { showPicker = false }
    )
  }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
  Button(
    onClick = onClick,
    shape = RoundedCornerShape(8.dp),
    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .testTag("btn_save_business_profile")
  ) {
    Text(
      text = "SAVE",
      fontSize = 14.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White
    )
  }
}
