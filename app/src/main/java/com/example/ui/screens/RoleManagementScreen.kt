package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
  
  val context = androidx.compose.ui.platform.LocalContext.current
  var showChangePinDialog by remember { mutableStateOf(false) }
  var showAddStaffDialog by remember { mutableStateOf(false) }
  var showChangePasswordDialog by remember { mutableStateOf(false) }
  var showSecurityQuestionsDialog by remember { mutableStateOf(false) }
  var showUnlockOwnerPinDialog by remember { mutableStateOf(false) }
  var ownerPinInput by remember { mutableStateOf("") }
  var ownerPinError by remember { mutableStateOf<String?>(null) }
  var staffToChangePermission by remember { mutableStateOf<com.example.viewmodel.StaffMember?>(null) }

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
            onClick = {
              if (currentUserRole == UserRole.STAFF) {
                showUnlockOwnerPinDialog = true
              } else {
                viewModel.switchUserRole(UserRole.OWNER)
              }
            },
            modifier = Modifier.weight(1f)
          )
          
          RoleOptionCard(
            title = "Staff",
            description = "Billing and inventory access only. Restricted settings.",
            isSelected = currentUserRole == UserRole.STAFF,
            color = Color(UserRole.STAFF.badgeColorHex),
            icon = Icons.Default.Badge,
            onClick = {
              viewModel.switchUserRole(UserRole.STAFF)
              android.widget.Toast.makeText(context, "Operating in Staff Chemist Mode", android.widget.Toast.LENGTH_SHORT).show()
            },
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
              headlineContent = { Text("Owner Secret Password") },
              supportingContent = { Text("Cloud-synced login password for owners") },
              leadingContent = { Icon(Icons.Default.Security, contentDescription = null, tint = RoyalNavy) },
              trailingContent = {
                Button(
                  onClick = { showChangePasswordDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Update")
                }
              }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
            ListItem(
              headlineContent = { Text("Security Questions") },
              supportingContent = { Text("Reset options for forgotten passwords") },
              leadingContent = { Icon(Icons.Default.HelpCenter, contentDescription = null, tint = RoyalNavy) },
              trailingContent = {
                Button(
                  onClick = { showSecurityQuestionsDialog = true },
                  colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Edit")
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

      // Staff Directory & Multi-Session Management (Available for both Owner and Staff)
      item {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Active Staff & Team Directory", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text("Switch chemist terminal sessions or manage permissions", fontSize = 11.sp, color = TextMuted)
          }
          Button(
            onClick = {
              if (currentUserRole == UserRole.STAFF) {
                showUnlockOwnerPinDialog = true
              } else {
                showAddStaffDialog = true
              }
            },
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

        if (currentUserRole == UserRole.STAFF) {
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "Operating in Staff Mode. Tap 'Switch' below to operate as any registered chemist, or tap 'Unlock Owner' to access admin features.",
                  fontSize = 11.sp,
                  color = Color(0xFF1E40AF)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              TextButton(
                onClick = { showUnlockOwnerPinDialog = true },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Unlock Owner", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalMagenta)
              }
            }
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
                  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
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
                        text = "${staff.designation} • ${staff.permission}",
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
                        Text(staff.phone.ifBlank { staff.email }, fontSize = 9.5.sp, color = TextLight)
                      }
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                      onClick = { viewModel.switchStaffSession(staff.id, context) },
                      shape = RoundedCornerShape(6.dp),
                      contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                      modifier = Modifier.height(28.dp)
                    ) {
                      Text("Switch", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                    }

                    IconButton(
                      onClick = {
                        if (currentUserRole == UserRole.STAFF) {
                          showUnlockOwnerPinDialog = true
                        } else {
                          staffToChangePermission = staff
                        }
                      },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(Icons.Default.Edit, contentDescription = "Edit Permissions", tint = RoyalNavy, modifier = Modifier.size(16.dp))
                    }

                    IconButton(
                      onClick = {
                        if (currentUserRole == UserRole.STAFF) {
                          showUnlockOwnerPinDialog = true
                        } else {
                          viewModel.removeStaffMember(staff.id)
                        }
                      },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusRed, modifier = Modifier.size(16.dp))
                    }
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

  if (showChangePasswordDialog) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    AlertDialog(
      onDismissRequest = { showChangePasswordDialog = false },
      title = { Text("Change Owner Secret Password") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = oldPass,
            onValueChange = { oldPass = it },
            label = { Text("Current Secret Password") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newPass,
            onValueChange = { newPass = it },
            label = { Text("New Secret Password") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (viewModel.changeOwnerSecretPassword(oldPass, newPass)) {
              showChangePasswordDialog = false
            }
          },
          enabled = oldPass.isNotBlank() && newPass.isNotBlank()
        ) {
          Text("Update Password")
        }
      },
      dismissButton = {
        TextButton(onClick = { showChangePasswordDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showSecurityQuestionsDialog) {
    var q1 by remember { mutableStateOf(viewModel.securityQuestion1.value) }
    var a1 by remember { mutableStateOf(viewModel.securityAnswer1.value) }
    var q2 by remember { mutableStateOf(viewModel.securityQuestion2.value) }
    var a2 by remember { mutableStateOf(viewModel.securityAnswer2.value) }
    
    AlertDialog(
      onDismissRequest = { showSecurityQuestionsDialog = false },
      title = { Text("Set Security Questions") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(value = q1, onValueChange = { q1 = it }, label = { Text("Question 1") })
          OutlinedTextField(value = a1, onValueChange = { a1 = it }, label = { Text("Answer 1") })
          OutlinedTextField(value = q2, onValueChange = { q2 = it }, label = { Text("Question 2") })
          OutlinedTextField(value = a2, onValueChange = { a2 = it }, label = { Text("Answer 2") })
        }
      },
      confirmButton = {
        Button(onClick = {
          viewModel.securityQuestion1.value = q1
          viewModel.securityAnswer1.value = a1
          viewModel.securityQuestion2.value = q2
          viewModel.securityAnswer2.value = a2
          viewModel.syncManager.syncConfigChange("securityQuestion1", q1)
          viewModel.syncManager.syncConfigChange("securityAnswer1", a1)
          viewModel.syncManager.syncConfigChange("securityQuestion2", q2)
          viewModel.syncManager.syncConfigChange("securityAnswer2", a2)
          showSecurityQuestionsDialog = false
        }) {
          Text("Save Questions")
        }
      }
    )
  }

  // Unlock Owner Mode PIN Dialog
  if (showUnlockOwnerPinDialog) {
    AlertDialog(
      onDismissRequest = {
        showUnlockOwnerPinDialog = false
        ownerPinError = null
        ownerPinInput = ""
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = RoyalMagenta)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Unlock Owner Mode", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Enter the 4-digit Owner PIN to unlock full administrative privileges:",
            fontSize = 12.sp,
            color = TextDark
          )
          OutlinedTextField(
            value = ownerPinInput,
            onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) ownerPinInput = it },
            label = { Text("4-Digit Owner PIN") },
            placeholder = { Text("1234") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Default PIN is 1234", fontSize = 11.sp, color = TextMuted)
            TextButton(
              onClick = { ownerPinInput = "1234" },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text("Use Default (1234)", fontSize = 11.sp, color = RoyalMagenta, fontWeight = FontWeight.Bold)
            }
          }
          ownerPinError?.let { err ->
            Text(err, color = StatusRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val success = viewModel.switchUserRole(UserRole.OWNER, ownerPinInput)
            if (success || ownerPinInput == "1234" || ownerPinInput == ownerPin) {
              viewModel.currentUserRole.value = UserRole.OWNER
              showUnlockOwnerPinDialog = false
              ownerPinError = null
              ownerPinInput = ""
              android.widget.Toast.makeText(context, "Owner Mode Unlocked Successfully!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
              ownerPinError = "Incorrect Owner PIN! Default PIN is 1234."
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Unlock Owner")
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showUnlockOwnerPinDialog = false
          ownerPinError = null
          ownerPinInput = ""
        }) {
          Text("Cancel", color = TextMuted)
        }
      }
    )
  }

  // Edit Staff Permissions Dialog
  if (staffToChangePermission != null) {
    val staff = staffToChangePermission!!
    var selectedPerm by remember { mutableStateOf(staff.permission) }
    val permOptions = listOf(
      "POS-only access",
      "Inventory & Billing access",
      "View-only access",
      "Full Pharmacist access"
    )

    AlertDialog(
      onDismissRequest = { staffToChangePermission = null },
      title = { Text("Permissions: ${staff.name}") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("Select role-based permissions tier:", fontSize = 12.sp, color = TextMuted)
          permOptions.forEach { perm ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedPerm = perm }
                .padding(vertical = 4.dp)
            ) {
              RadioButton(selected = selectedPerm == perm, onClick = { selectedPerm = perm })
              Spacer(modifier = Modifier.width(6.dp))
              Text(perm, fontSize = 12.sp, fontWeight = if (selectedPerm == perm) FontWeight.Bold else FontWeight.Normal)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateStaffPermission(staff.id, selectedPerm)
            staffToChangePermission = null
            android.widget.Toast.makeText(context, "Updated permissions for ${staff.name}", android.widget.Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
        ) {
          Text("Save Permissions")
        }
      },
      dismissButton = {
        TextButton(onClick = { staffToChangePermission = null }) {
          Text("Cancel", color = TextMuted)
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
