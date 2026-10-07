package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AuthLoginType
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isDarkMode by viewModel.isDarkMode.collectAsState()

  var isSignUpMode by remember { mutableStateOf(false) }
  var phoneInput by remember { mutableStateOf("") }
  var nameInput by remember { mutableStateOf("") }
  var emailInput by remember { mutableStateOf("") }
  var otpInput by remember { mutableStateOf("") }
  var selectedLoginRole by remember { mutableStateOf(UserRole.OWNER) }
  var ownerPasswordInput by remember { mutableStateOf("") }

  var showGoogleChooser by remember { mutableStateOf(false) }
  var showOtpField by remember { mutableStateOf(false) }
  var isAuthenticating by remember { mutableStateOf(false) }

  val ownerSecretPassword by viewModel.ownerSecretPassword.collectAsState()

  val bgThemeColor = if (isDarkMode) Color(0xFF0F172A) else Color.White
  val textThemeColor = if (isDarkMode) Color.White else TextDark
  val cardThemeColor = if (isDarkMode) Color(0xFF1E293B) else GrayBackground

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(bgThemeColor)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      item {
        Spacer(modifier = Modifier.height(40.dp))

        // App Logo Icon
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(RoyalMagentaLight),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.HealthAndSafety,
            contentDescription = "Royal Pharmacy Logo",
            tint = RoyalMagenta,
            modifier = Modifier.size(42.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
          text = "ROYAL PHARMACY",
          fontSize = 24.sp,
          fontWeight = FontWeight.ExtraBold,
          color = RoyalMagenta,
          letterSpacing = 1.sp
        )

        Text(
          text = "Comprehensive Retail ERP & POS System",
          fontSize = 12.sp,
          color = TextMuted,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tab Row (Sign In vs Sign Up)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(cardThemeColor)
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (!isSignUpMode) RoyalNavy else Color.Transparent)
              .clickable {
                isSignUpMode = false
                showOtpField = false
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign In",
              color = if (!isSignUpMode) Color.White else textThemeColor,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSignUpMode) RoyalNavy else Color.Transparent)
              .clickable {
                isSignUpMode = true
                showOtpField = false
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "Sign Up",
              color = if (isSignUpMode) Color.White else textThemeColor,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(28.dp))
      }

      // Input Form Section
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
          border = CardDefaults.outlinedCardBorder(),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Text(
              text = if (isSignUpMode) "Create Pharmacy Account" else "Welcome Back",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = textThemeColor
            )

            // Role Selector (Owner vs Staff)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { selectedLoginRole = UserRole.OWNER },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedLoginRole == UserRole.OWNER) RoyalMagenta.copy(alpha = 0.1f) else Color.Transparent),
                modifier = Modifier.weight(1f)
              ) {
                Text("👑 Owner Mode", color = if (selectedLoginRole == UserRole.OWNER) RoyalMagenta else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
              OutlinedButton(
                onClick = { selectedLoginRole = UserRole.STAFF },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedLoginRole == UserRole.STAFF) RoyalNavy.copy(alpha = 0.1f) else Color.Transparent),
                modifier = Modifier.weight(1f)
              ) {
                Text("💼 Staff Mode", color = if (selectedLoginRole == UserRole.STAFF) RoyalNavy else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            if (selectedLoginRole == UserRole.OWNER) {
              OutlinedTextField(
                value = ownerPasswordInput,
                onValueChange = { ownerPasswordInput = it },
                label = { Text("Owner Secret Password*") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = RoyalMagenta) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("owner_secret_password_input")
              )
            }

            if (isSignUpMode) {
              // Sign Up - Name Field
              OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Full Owner / Partner Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("signup_name_field")
              )

              // Sign Up - Email Field
              OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Business Email Address (Optional if phone used)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("signup_email_field")
              )
            }

            // Mobile Phone Field
            OutlinedTextField(
              value = phoneInput,
              onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 10) phoneInput = it },
              label = { Text("10-Digit Mobile Number") },
              prefix = { Text("+91 ") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("login_phone_field")
            )

            // OTP Entrance Fields (if requested)
            AnimatedVisibility(visible = showOtpField) {
              Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                  value = otpInput,
                  onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 6) otpInput = it },
                  label = { Text("Enter 6-Digit OTP Code") },
                  supportingText = { Text("Default verification code is: 123456") },
                  leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted) },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  singleLine = true,
                  modifier = Modifier.fillMaxWidth().testTag("login_otp_field")
                )
              }
            }

            // Actions Button
            Button(
              onClick = {
                if (selectedLoginRole == UserRole.OWNER && ownerPasswordInput != ownerSecretPassword) {
                  Toast.makeText(context, "Incorrect Owner Secret Password!", Toast.LENGTH_LONG).show()
                } else if (isSignUpMode && (nameInput.isBlank() || phoneInput.length != 10)) {
                  Toast.makeText(context, "Please fulfill all Sign Up parameters correctly.", Toast.LENGTH_SHORT).show()
                } else if (!isSignUpMode && phoneInput.length != 10) {
                  Toast.makeText(context, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show()
                } else if (!showOtpField) {
                  showOtpField = true
                  Toast.makeText(context, "OTP Code (123456) successfully generated to +91 $phoneInput", Toast.LENGTH_LONG).show()
                } else {
                  if (otpInput == "123456" || otpInput.isBlank()) {
                    isAuthenticating = true
                    viewModel.loginWithPhone(
                      phone = "+91 $phoneInput",
                      userName = if (isSignUpMode) nameInput else "Suleman Hoque",
                      role = selectedLoginRole
                    )
                    Toast.makeText(context, "Sign In Successful as ${selectedLoginRole.label}!", Toast.LENGTH_SHORT).show()
                  } else {
                    Toast.makeText(context, "Invalid Security Code! Please enter '123456'", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_auth_primary")
            ) {
              if (isAuthenticating) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
              } else {
                val actionLabel = if (showOtpField) "Verify & Access ERP" else "Request 6-Digit OTP"
                Text(actionLabel, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }

      // One-click Google Sign-In Chooser Link
      item {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
          Text(
            text = "OR CONTINUE WITH",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 12.dp)
          )
          HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Google Authentication trigger Card
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
          border = BorderStroke(1.dp, CardBorder),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { showGoogleChooser = true }
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = "Google Sign In",
              tint = RoyalMagenta,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Sign in with Google Account",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = textThemeColor
            )
          }
        }

        Spacer(modifier = Modifier.height(40.dp))
      }
    }

    // Google Account Chooser bottom-sheet modal dialog (Screenshot 1 & 3 inspired)
    if (showGoogleChooser) {
      AlertDialog(
        onDismissRequest = { showGoogleChooser = false },
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = RoyalMagenta,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Choose a Google Account",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = textThemeColor,
              textAlign = TextAlign.Center
            )
            Text(
              text = "to continue to Royal Pharmacy Retail Portal",
              fontSize = 11.sp,
              color = TextMuted,
              textAlign = TextAlign.Center
            )
          }
        },
        text = {
          Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            // Account 1: Sk Sk
            GoogleAccountRow(
              avatarSeed = "Sk",
              displayName = "Sk Sk",
              email = "sk786sk9957@gmail.com",
              isDarkMode = isDarkMode,
              onClick = {
                showGoogleChooser = false
                viewModel.loginWithGmail("sk786sk9957@gmail.com", "Sk Sk", UserRole.OWNER)
                Toast.makeText(context, "Logged in as Sk Sk", Toast.LENGTH_SHORT).show()
              }
            )

            // Account 2: Slmn Z (Suleman)
            GoogleAccountRow(
              avatarSeed = "Slmn",
              displayName = "Slmn Z",
              email = "sulman995790@gmail.com",
              isDarkMode = isDarkMode,
              onClick = {
                showGoogleChooser = false
                viewModel.loginWithGmail("sulman995790@gmail.com", "Suleman Hoque", UserRole.OWNER)
                Toast.makeText(context, "Logged in as Suleman Hoque", Toast.LENGTH_SHORT).show()
              }
            )

            // Account 3: Nur Nime
            GoogleAccountRow(
              avatarSeed = "Nime",
              displayName = "Nur Nime",
              email = "nimenur931@gmail.com",
              isDarkMode = isDarkMode,
              onClick = {
                showGoogleChooser = false
                viewModel.loginWithGmail("nimenur931@gmail.com", "Nur Nime", UserRole.STAFF)
                Toast.makeText(context, "Logged in as Nur Nime (Staff Mode)", Toast.LENGTH_SHORT).show()
              }
            )

            // Option 4: Use Google Without Account / Guest Mode
            Card(
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(0.5.dp, CardBorder),
              colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF334155) else GrayBackground),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  showGoogleChooser = false
                  viewModel.loginWithGmail("guest@royalchemist.com", "Guest Chemist", UserRole.STAFF)
                  Toast.makeText(context, "Operating in Guest Staff Mode", Toast.LENGTH_SHORT).show()
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.NoAccounts,
                  contentDescription = null,
                  tint = TextMuted,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = "Use Google without an account",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = textThemeColor
                )
              }
            }
          }
        },
        confirmButton = {
          TextButton(onClick = { showGoogleChooser = false }) {
            Text("Cancel", color = RoyalMagenta, fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  }
}

@Composable
fun GoogleAccountRow(
  avatarSeed: String,
  displayName: String,
  email: String,
  isDarkMode: Boolean,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(0.5.dp, CardBorder),
    colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E293B) else Color.White),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(RoyalMagentaLight),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = avatarSeed.take(2),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = RoyalMagenta
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = displayName,
          fontSize = 12.5.sp,
          fontWeight = FontWeight.Bold,
          color = if (isDarkMode) Color.White else TextDark
        )
        Text(
          text = email,
          fontSize = 10.5.sp,
          color = TextMuted
        )
      }
    }
  }
}
