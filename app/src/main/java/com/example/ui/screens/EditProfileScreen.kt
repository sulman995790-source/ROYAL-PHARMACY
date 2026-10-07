package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.PharmacyViewModel
import com.example.viewmodel.Screen

@Composable
fun EditProfileScreen(
  viewModel: PharmacyViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.businessProfile.collectAsState()
  var name by remember(profile) { mutableStateOf(profile.ownerName) }
  var phone by remember(profile) { mutableStateOf(profile.phone) }
  var email by remember(profile) { mutableStateOf(profile.email) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.White)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      // Header (Screenshot 16)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.navigateTo(Screen.MORE) }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
          }
          Text(
            text = "Edit Profile",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        }

        Text(
          text = "Need help?",
          fontSize = 12.sp,
          color = RoyalMagenta,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(end = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Circle avatar with camera icon (Screenshot 16)
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(GrayBackground)
            .border(1.dp, CardBorder, CircleShape)
            .clickable { /* Select avatar */ },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(28.dp)
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Add Profile picture",
          fontSize = 12.sp,
          color = TextDark,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(30.dp))

      // Fields (Screenshot 16)
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Your Name*") },
        modifier = Modifier.fillMaxWidth().testTag("edit_profile_name")
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = phone,
        onValueChange = { if (it.length <= 10) phone = it },
        label = { Text("Mobile Number") },
        supportingText = { Text("${phone.length}/10") },
        modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone")
      )

      Spacer(modifier = Modifier.height(16.dp))

      OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your Email ID*") },
        modifier = Modifier.fillMaxWidth().testTag("edit_profile_email")
      )

      Spacer(modifier = Modifier.weight(1f))

      Button(
        onClick = {
          viewModel.saveBusinessProfile(profile.copy(ownerName = name, phone = phone, email = email))
          viewModel.navigateTo(Screen.MORE)
        },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .padding(bottom = 8.dp)
          .testTag("btn_update_profile")
      ) {
        Text("Update", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
