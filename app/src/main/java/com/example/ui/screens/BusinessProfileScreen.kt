package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.businessProfile.collectAsState()

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val tabs = listOf("BASIC", "LICENSE", "TAXATION", "LOCATION", "TIMINGS")

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
        timings = timings
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
                Column {
                  Text("Business Front Picture", fontSize = 12.sp, color = TextMuted)
                  Spacer(modifier = Modifier.height(6.dp))
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(180.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFF3F4F6))
                      .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                      .clickable { /* Select picture */ },
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Box(
                        modifier = Modifier
                          .size(54.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .background(Color(0xFFE1BEE7)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(
                          imageVector = Icons.Default.FileUpload,
                          contentDescription = "Upload",
                          tint = Color(0xFF6A1B9A),
                          modifier = Modifier.size(28.dp)
                        )
                      }
                      Spacer(modifier = Modifier.height(8.dp))
                      Text("Click to Upload", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Note: Store nameboard should be clearly visible in the picture",
                    fontSize = 11.sp,
                    color = TextMuted
                  )
                }

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
                Column {
                  Text("Form 20 Image*", fontSize = 12.sp, color = TextMuted)
                  Spacer(modifier = Modifier.height(6.dp))
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(140.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(Color(0xFFF3F4F6))
                      .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Box(
                        modifier = Modifier
                          .size(44.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .background(Color(0xFFE1BEE7)),
                        contentAlignment = Alignment.Center
                      ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(24.dp))
                      }
                      Spacer(modifier = Modifier.height(6.dp))
                      Text("Click to Upload", fontSize = 12.sp, color = TextDark)
                    }
                  }
                }

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

                // Update Location from Map Button (Screenshot 15)
                Button(
                  onClick = { /* simulated map location update */ },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  modifier = Modifier.fillMaxWidth().height(48.dp)
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
        }

        item {
          Spacer(modifier = Modifier.height(50.dp))
        }
      }
    }
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
