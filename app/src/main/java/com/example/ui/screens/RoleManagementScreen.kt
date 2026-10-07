package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.viewmodel.UserRole

@Composable
fun RoleManagementScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val currentUserRole by viewModel.currentUserRole.collectAsState()
  val ownerPin by viewModel.ownerPin.collectAsState()
  val staffMembers by viewModel.staffMembers.collectAsState()
  
  var showChangePinDialog by remember { mutableStateOf(false) }
  var showAddStaffDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(GrayBackground)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(RoyalNavy)
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
      }
      Spacer(modifier = Modifier.width(8.dp))
      Text("Role & Access Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Current Status
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(currentUserRole.badgeColorHex).copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (currentUserRole == UserRole.OWNER) Icons.Default.VerifiedUser else Icons.Default.Person,
                contentDescription = null,
                tint = Color(currentUserRole.badgeColorHex)
              )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
              Text("Active Role", fontSize = 12.sp, color = TextMuted)
              Text(currentUserRole.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
          }
        }
      }

      // Role Switcher
      item {
        Text("Switch Roles", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 8.dp))
        
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          RoleOptionCard(
            title = "Owner",
            description = "Full administrative access to settings and finance.",
            isSelected = currentUserRole == UserRole.OWNER,
            color = Color(UserRole.OWNER.badgeColorHex),
            icon = Icons.Default.AdminPanelSettings,
            onClick = { viewModel.switchUserRole(UserRole.OWNER) },
            modifier = Modifier.weight(1f)
          )
          
          RoleOptionCard(
            title = "Staff",
            description = "Billing and inventory access only. Restricted settings.",
            isSelected = currentUserRole == UserRole.STAFF,
            color = Color(UserRole.STAFF.badgeColorHex),
            icon = Icons.Default.Badge,
            onClick = { viewModel.switchUserRole(UserRole.STAFF) },
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Security Settings
      item {
        Text("Security Settings", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column {
            ListItem(
              headlineContent = { Text("Owner PIN Lock") },
              supportingContent = { Text("Required to switch from Staff to Owner mode") },
              leadingContent = { Icon(Icons.Default.Lock, contentDescription = null, tint = RoyalNavy) },
              trailingContent = {
                Button(
                  onClick = { showChangePinDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Change PIN")
                }
              }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            ListItem(
              headlineContent = { Text("Biometric Authentication") },
              supportingContent = { Text("Use fingerprint for restricted actions") },
              leadingContent = { Icon(Icons.Default.Fingerprint, contentDescription = null, tint = RoyalNavy) },
              trailingContent = {
                Switch(checked = true, onCheckedChange = {})
              }
            )
          }
        }
      }

      // Staff Activity Log (Simulated)
      item {
        Text("Staff Access Control", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text("Action Permissions:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            PermissionItem("Billing & Invoicing", true)
            PermissionItem("Inventory Scanning", true)
            PermissionItem("Delete/Edit Sales", false)
            PermissionItem("Export Financial Reports", false)
            PermissionItem("Database Backup", false)
            PermissionItem("Supplier Payments", false)
          }
        }
      }

      // Owner-Only Staff Management & Login Logs
      if (currentUserRole == UserRole.OWNER) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Active Staff & Logins", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Button(
              onClick = { showAddStaffDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Add Staff", fontSize = 11.sp, color = Color.White)
            }
          }

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
          ) {
            if (staffMembers.isEmpty()) {
              Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
              ) {
                Text("No staff registered yet. Add staff above.", fontSize = 12.sp, color = TextMuted)
              }
            } else {
              Column {
                staffMembers.forEachIndexed { index, staff ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(
                        modifier = Modifier
                          .size(36.dp)
                          .clip(CircleShape)
                          .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(staff.name.take(2).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                      }
                      Spacer(modifier = Modifier.width(10.dp))
                      Column {
                        Text(staff.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(
                          text = if (staff.phone.isNotBlank()) staff.phone else staff.email,
                          fontSize = 11.sp,
                          color = TextMuted
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                          Box(
                            modifier = Modifier
                              .clip(RoundedCornerShape(4.dp))
                              .background(if (staff.loginType == "GMAIL") Color(0xFFFBE4EE) else Color(0xFFEFF6FF))
                              .padding(horizontal = 4.dp, vertical = 1.dp)
                          ) {
                            Text(
                              text = staff.loginType,
                              fontSize = 8.sp,
                              fontWeight = FontWeight.Bold,
                              color = if (staff.loginType == "GMAIL") RoyalMagenta else Color(0xFF2563EB)
                            )
                          }
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Logged: ${staff.lastLoginTime}", fontSize = 9.5.sp, color = TextLight)
                        }
                      }
                    }

                    IconButton(onClick = { viewModel.removeStaffMember(staff.id) }) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(18.dp))
                    }
                  }
                  if (index < staffMembers.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = Color(0xFFF1F5F9))
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  if (showAddStaffDialog) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var loginType by remember { mutableStateOf("PHONE") } // PHONE or GMAIL

    AlertDialog(
      onDismissRequest = { showAddStaffDialog = false },
      title = { Text("Register Staff Member") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Staff Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = loginType == "GMAIL", onClick = { loginType = "GMAIL" })
            Text("Gmail", fontSize = 13.sp, modifier = Modifier.clickable { loginType = "GMAIL" })
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = loginType == "PHONE", onClick = { loginType = "PHONE" })
            Text("Phone", fontSize = 13.sp, modifier = Modifier.clickable { loginType = "PHONE" })
          }

          if (loginType == "GMAIL") {
            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Gmail Address") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Mobile Phone Number") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank()) {
              viewModel.addStaffMember(name, email, phone, loginType)
              showAddStaffDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalMagenta)
        ) {
          Text("Add Staff Access")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddStaffDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showChangePinDialog) {
    var newPin by remember { mutableStateOf("") }
    AlertDialog(
      onDismissRequest = { showChangePinDialog = false },
      title = { Text("Change Owner PIN") },
      text = {
        Column {
          Text("Current PIN: $ownerPin", fontSize = 12.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = newPin,
            onValueChange = { if (it.length <= 4) newPin = it },
            label = { Text("Enter New 4-Digit PIN") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newPin.length == 4) {
              viewModel.ownerPin.value = newPin
              showChangePinDialog = false
            }
          },
          enabled = newPin.length == 4
        ) {
          Text("Save PIN")
        }
      },
      dismissButton = {
        TextButton(onClick = { showChangePinDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun RoleOptionCard(
  title: String,
  description: String,
  isSelected: Boolean,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isSelected) 2.dp else 1.dp,
      color = if (isSelected) color else CardBorder
    ),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) color else TextMuted,
        modifier = Modifier.size(24.dp)
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) color else TextDark)
      Spacer(modifier = Modifier.height(4.dp))
      Text(description, fontSize = 10.sp, color = TextMuted, lineHeight = 14.sp)
    }
  }
}

@Composable
fun PermissionItem(label: String, granted: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, fontSize = 12.sp, color = TextDark)
    Icon(
      imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Cancel,
      contentDescription = null,
      tint = if (granted) StatusGreen else StatusRed,
      modifier = Modifier.size(16.dp)
    )
  }
}
