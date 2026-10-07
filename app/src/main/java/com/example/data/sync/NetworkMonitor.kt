package com.example.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(private val context: Context) {
  private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val _isOnline = MutableStateFlow(checkInitialConnectivity())
  val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

  private val _connectionType = MutableStateFlow(determineConnectionType())
  val connectionType: StateFlow<String> = _connectionType.asStateFlow()

  // Manual simulation mode for testing offline functionality inside app
  private val _isSimulationOffline = MutableStateFlow(false)
  val isSimulationOffline: StateFlow<Boolean> = _isSimulationOffline.asStateFlow()

  private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
      if (!_isSimulationOffline.value) {
        _isOnline.value = true
        _connectionType.value = determineConnectionType()
      }
    }

    override fun onLost(network: Network) {
      if (!_isSimulationOffline.value) {
        _isOnline.value = checkInitialConnectivity()
        _connectionType.value = if (_isOnline.value) determineConnectionType() else "Offline"
      }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
      val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
      if (!_isSimulationOffline.value) {
        _isOnline.value = hasInternet
        _connectionType.value = if (hasInternet) determineConnectionType() else "Offline"
      }
    }
  }

  init {
    val request = NetworkRequest.Builder()
      .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
      .build()
    connectivityManager.registerNetworkCallback(request, networkCallback)
  }

  fun setSimulationOffline(offline: Boolean) {
    _isSimulationOffline.value = offline
    if (offline) {
      _isOnline.value = false
      _connectionType.value = "Simulated Offline Mode"
    } else {
      _isOnline.value = checkInitialConnectivity()
      _connectionType.value = determineConnectionType()
    }
  }

  private fun checkInitialConnectivity(): Boolean {
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
  }

  private fun determineConnectionType(): String {
    val activeNetwork = connectivityManager.activeNetwork ?: return "Offline"
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return "Offline"
    return when {
      capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
      capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 4G/5G"
      capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
      else -> "Connected"
    }
  }
}
