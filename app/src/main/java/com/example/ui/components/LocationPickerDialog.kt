package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.CardBorder
import com.example.ui.theme.GrayBackground
import com.example.ui.theme.RoyalMagenta
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

data class MapPresetLocation(
  val name: String,
  val line1: String,
  val line2: String,
  val line3: String,
  val lat: Double,
  val lng: Double,
  val normalizedX: Float = 0.5f,
  val normalizedY: Float = 0.5f
)

@SuppressLint("MissingPermission")
@Composable
fun LocationPickerDialog(
  currentAddress1: String,
  currentAddress2: String,
  currentAddress3: String,
  onLocationSelected: (address1: String, address2: String, address3: String, lat: Double, lng: Double) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedAddress1 by remember { mutableStateOf(currentAddress1.ifBlank { "Darrang, Assam - 784146" }) }
  var selectedAddress2 by remember { mutableStateOf(currentAddress2.ifBlank { "Hospital Road, Near Civil Hospital" }) }
  var selectedAddress3 by remember { mutableStateOf(currentAddress3.ifBlank { "Darrang, Assam - 784146" }) }
  var currentLat by remember { mutableDoubleStateOf(26.4385) }
  var currentLng by remember { mutableDoubleStateOf(92.0305) }
  var isDetectingLocation by remember { mutableStateOf(false) }

  // Interactive map pin offset (0f..1f within canvas)
  var pinX by remember { mutableFloatStateOf(0.5f) }
  var pinY by remember { mutableFloatStateOf(0.45f) }
  var mapZoom by remember { mutableFloatStateOf(1.0f) }
  var activeViewTab by remember { mutableIntStateOf(0) } // 0: Interactive Map Pin, 1: Live Web Satellite/OSM Map

  val presets = listOf(
    MapPresetLocation(
      name = "Civil Hospital Hub",
      line1 = "Darrang, Assam - 784146",
      line2 = "Hospital Road, Opposite Civil Hospital",
      line3 = "Mangaldai, Darrang, Assam - 784146",
      lat = 26.4385,
      lng = 92.0305,
      normalizedX = 0.50f,
      normalizedY = 0.45f
    ),
    MapPresetLocation(
      name = "Commercial Market",
      line1 = "Daily Market Road, Ward No. 3",
      line2 = "Near Old ASTC Bus Stand",
      line3 = "Mangaldai, Assam - 784125",
      lat = 26.4350,
      lng = 92.0340,
      normalizedX = 0.65f,
      normalizedY = 0.65f
    ),
    MapPresetLocation(
      name = "NH-15 Bypass Crossing",
      line1 = "National Highway 15, Bypass Crossing",
      line2 = "Near Sanjeevani Heart & Medical Research",
      line3 = "Darrang District, Assam - 784146",
      lat = 26.4420,
      lng = 92.0280,
      normalizedX = 0.35f,
      normalizedY = 0.30f
    ),
    MapPresetLocation(
      name = "DC Court Road",
      line1 = "DC Court Road, Administrative Zone",
      line2 = "Opposite District Court Complex",
      line3 = "Mangaldai, Assam - 784125",
      lat = 26.4395,
      lng = 92.0375,
      normalizedX = 0.75f,
      normalizedY = 0.38f
    )
  )

  @Suppress("DEPRECATION")
  fun detectGpsLocation() {
    isDetectingLocation = true
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
      Toast.makeText(context, "Location service unavailable", Toast.LENGTH_SHORT).show()
      isDetectingLocation = false
      return
    }

    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)
    var bestLocation: Location? = null

    for (provider in providers) {
      try {
        if (locationManager.isProviderEnabled(provider)) {
          val loc = locationManager.getLastKnownLocation(provider)
          if (loc != null && (bestLocation == null || loc.accuracy < bestLocation.accuracy)) {
            bestLocation = loc
          }
        }
      } catch (_: SecurityException) {}
    }

    if (bestLocation != null) {
      currentLat = bestLocation.latitude
      currentLng = bestLocation.longitude
      pinX = 0.5f
      pinY = 0.5f

      scope.launch(Dispatchers.IO) {
        try {
          val geocoder = Geocoder(context, Locale.getDefault())
          val addresses = geocoder.getFromLocation(bestLocation.latitude, bestLocation.longitude, 1)
          val addr = addresses?.firstOrNull()
          withContext(Dispatchers.Main) {
            if (addr != null) {
              selectedAddress1 = listOfNotNull(addr.subLocality, addr.locality, addr.postalCode).joinToString(", ")
              selectedAddress2 = listOfNotNull(addr.thoroughfare, addr.subThoroughfare ?: "Near Main Road").joinToString(", ")
              selectedAddress3 = listOfNotNull(addr.adminArea, addr.countryName, addr.postalCode).joinToString(" - ")
            } else {
              selectedAddress1 = "Lat: %.4f, Lng: %.4f".format(bestLocation.latitude, bestLocation.longitude)
              selectedAddress2 = "GPS Verified Device Location"
              selectedAddress3 = "Darrang, Assam, India - 784146"
            }
            isDetectingLocation = false
            Toast.makeText(context, "Location detected via GPS", Toast.LENGTH_SHORT).show()
          }
        } catch (_: Exception) {
          withContext(Dispatchers.Main) {
            selectedAddress1 = "Lat: %.4f, Lng: %.4f".format(bestLocation.latitude, bestLocation.longitude)
            selectedAddress2 = "GPS Verified Location"
            selectedAddress3 = "Darrang, Assam - 784146"
            isDetectingLocation = false
            Toast.makeText(context, "Location updated from GPS", Toast.LENGTH_SHORT).show()
          }
        }
      }
    } else {
      // Default fallback
      currentLat = 26.4385
      currentLng = 92.0305
      pinX = 0.5f
      pinY = 0.45f
      selectedAddress1 = "Hospital Road, Civil Hospital Zone"
      selectedAddress2 = "Mangaldai, Darrang"
      selectedAddress3 = "Assam - 784146"
      isDetectingLocation = false
      Toast.makeText(context, "Pharmacy coordinates centered on Hospital Road", Toast.LENGTH_SHORT).show()
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (fineGranted || coarseGranted) {
      detectGpsLocation()
    } else {
      Toast.makeText(context, "Location permission not granted. You can tap on the interactive map directly.", Toast.LENGTH_SHORT).show()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.96f)
        .padding(vertical = 16.dp),
      shape = RoundedCornerShape(16.dp),
      color = Color.White
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // 1. Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFCE4EC)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Update Location from Map", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
              Text("Tap map or drag pin to position store", fontSize = 11.5.sp, color = TextMuted)
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // View Mode Selector: Visual Pin Map vs Web Map
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = activeViewTab == 0,
            onClick = { activeViewTab = 0 },
            label = { Text("Interactive Pin Map", fontSize = 11.5.sp) },
            leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp)) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalNavy,
              selectedLabelColor = Color.White
            )
          )

          FilterChip(
            selected = activeViewTab == 1,
            onClick = { activeViewTab = 1 },
            label = { Text("Street Web Map", fontSize = 11.5.sp) },
            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(14.dp)) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalNavy,
              selectedLabelColor = Color.White
            )
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Interactive Map Container
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, CardBorder, RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
        ) {
          if (activeViewTab == 0) {
            // Interactive Vector Canvas Map
            Canvas(
              modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                  detectTapGestures { offset ->
                    val newX = (offset.x / size.width).coerceIn(0.1f, 0.9f)
                    val newY = (offset.y / size.height).coerceIn(0.1f, 0.9f)
                    pinX = newX
                    pinY = newY

                    // Interpolate coordinates dynamically around Darrang district
                    val dLat = (newY - 0.5f) * -0.015
                    val dLng = (newX - 0.5f) * 0.015
                    currentLat = 26.4385 + dLat
                    currentLng = 92.0305 + dLng

                    // Update address description based on position
                    when {
                      newY < 0.35f -> {
                        selectedAddress1 = "NH-15 Corridor & Bypass"
                        selectedAddress2 = "Near Hospital Road Intersection"
                        selectedAddress3 = "Darrang District, Assam - 784146"
                      }
                      newX > 0.60f -> {
                        selectedAddress1 = "Ward No. 3, Commercial Market"
                        selectedAddress2 = "Main Bazar Road"
                        selectedAddress3 = "Mangaldai, Assam - 784125"
                      }
                      newX < 0.40f -> {
                        selectedAddress1 = "Civil Hospital Road West"
                        selectedAddress2 = "Near Red Cross Dispensary"
                        selectedAddress3 = "Mangaldai, Assam - 784146"
                      }
                      else -> {
                        selectedAddress1 = "Hospital Road Central"
                        selectedAddress2 = "Opposite District Civil Hospital"
                        selectedAddress3 = "Darrang, Assam - 784146"
                      }
                    }
                  }
                }
            ) {
              val w = size.width
              val h = size.height

              // Base background map grass/terrain
              drawRect(Color(0xFFE2E8F0), Offset.Zero, size)

              // Parks & Green zones
              drawRoundRect(
                Color(0xFFDCFCE7),
                Offset(w * 0.08f, h * 0.12f),
                Size(w * 0.28f, h * 0.28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
              )
              drawRoundRect(
                Color(0xFFE0F2FE),
                Offset(w * 0.65f, h * 0.65f),
                Size(w * 0.25f, h * 0.25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
              )

              // Main Highway (NH-15)
              drawLine(
                color = Color(0xFFFBBF24),
                start = Offset(0f, h * 0.26f),
                end = Offset(w, h * 0.26f),
                strokeWidth = 14f * mapZoom
              )
              drawLine(
                color = Color(0xFFFEF3C7),
                start = Offset(0f, h * 0.26f),
                end = Offset(w, h * 0.26f),
                strokeWidth = 3f * mapZoom
              )

              // Cross Avenue (Hospital Road)
              drawLine(
                color = Color.White,
                start = Offset(w * 0.50f, 0f),
                end = Offset(w * 0.50f, h),
                strokeWidth = 12f * mapZoom
              )

              // Secondary Market Street
              drawLine(
                color = Color.White,
                start = Offset(w * 0.20f, h * 0.70f),
                end = Offset(w * 0.85f, h * 0.70f),
                strokeWidth = 8f * mapZoom
              )

              // Diagonal Connector Road
              val diagPath = Path().apply {
                moveTo(w * 0.20f, h * 0.26f)
                lineTo(w * 0.50f, h * 0.70f)
              }
              drawPath(diagPath, Color.White, style = Stroke(width = 8f * mapZoom))

              // Hospital Building Footprint
              drawRoundRect(
                Color(0xFFFCE7F3),
                Offset(w * 0.53f, h * 0.35f),
                Size(w * 0.22f, h * 0.20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                style = Stroke(width = 2f)
              )

              // Interactive Pin Marker
              val markerPxX = w * pinX
              val markerPxY = h * pinY

              // Pin Shadow
              drawOval(
                color = Color.Black.copy(alpha = 0.25f),
                topLeft = Offset(markerPxX - 10f, markerPxY - 2f),
                size = Size(20f, 8f)
              )

              // Pin Pulse Circle
              drawCircle(
                color = Color(0xFFE11D48).copy(alpha = 0.25f),
                radius = 18f,
                center = Offset(markerPxX, markerPxY - 14f)
              )

              // Pin Center
              drawCircle(
                color = Color(0xFFE11D48),
                radius = 8f,
                center = Offset(markerPxX, markerPxY - 14f)
              )
              drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = Offset(markerPxX, markerPxY - 14f)
              )
            }

            // Map Landmark Label Pills
            Box(modifier = Modifier.fillMaxSize()) {
              // Hospital Marker Label
              Box(
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .padding(top = 10.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color.White.copy(alpha = 0.90f))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("🏥 Civil Hospital Corridor", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
              }

              // Map Controls: Zoom & GPS Center
              Column(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(8.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { mapZoom = (mapZoom + 0.2f).coerceAtMost(1.8f) },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextDark, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable { mapZoom = (mapZoom - 0.2f).coerceAtLeast(0.8f) },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextDark, modifier = Modifier.size(16.dp))
                }
              }

              // Hint pill in bottom-left
              Box(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color.Black.copy(alpha = 0.65f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Tap map to move store pin", fontSize = 9.sp, color = Color.White)
              }
            }
          } else {
            // Live Web Map (OpenStreetMap embedded view)
            AndroidView(
              factory = { ctx ->
                WebView(ctx).apply {
                  webViewClient = WebViewClient()
                  settings.javaScriptEnabled = true
                  val osmUrl = "https://www.openstreetmap.org/export/embed.html?bbox=${currentLng - 0.01}%2C${currentLat - 0.01}%2C${currentLng + 0.01}%2C${currentLat + 0.01}&layer=mapnik&marker=${currentLat}%2C${currentLng}"
                  loadUrl(osmUrl)
                }
              },
              modifier = Modifier.fillMaxSize()
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. GPS Detection & Quick Landmark Presets
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
              val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
              if (fine || coarse) {
                detectGpsLocation()
              } else {
                permissionLauncher.launch(
                  arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
              }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).height(38.dp)
          ) {
            Icon(Icons.Default.MyLocation, contentDescription = null, tint = RoyalNavy, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isDetectingLocation) "Detecting GPS..." else "Use Current GPS",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = RoyalNavy
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Landmark Presets Row
        Text("Quick Landmark Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(presets) { preset ->
            val isSelected = selectedAddress2 == preset.line2
            Card(
              onClick = {
                selectedAddress1 = preset.line1
                selectedAddress2 = preset.line2
                selectedAddress3 = preset.line3
                currentLat = preset.lat
                currentLng = preset.lng
                pinX = preset.normalizedX
                pinY = preset.normalizedY
              },
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFFFCE4EC) else GrayBackground
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) RoyalMagenta else CardBorder
              )
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Place,
                  contentDescription = null,
                  tint = if (isSelected) RoyalMagenta else TextMuted,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(preset.name, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = TextDark)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Selected Address Live Preview Card
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = GrayBackground),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = RoyalMagenta, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Address Pinned on Map:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
              }
              Text(
                "%.4f° N, %.4f° E".format(currentLat, currentLng),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0369A1)
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Line 1: $selectedAddress1", fontSize = 11.5.sp, color = TextDark, maxLines = 1)
            Text("Line 2: $selectedAddress2", fontSize = 11.5.sp, color = TextMuted, maxLines = 1)
            Text("Line 3: $selectedAddress3", fontSize = 11.sp, color = TextMuted, maxLines = 1)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Apply Location Button
        Button(
          onClick = {
            onLocationSelected(selectedAddress1, selectedAddress2, selectedAddress3, currentLat, currentLng)
            Toast.makeText(context, "Store Location updated & saved!", Toast.LENGTH_SHORT).show()
            onDismiss()
          },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Apply Location to Profile", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}
