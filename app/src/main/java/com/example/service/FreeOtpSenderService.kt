package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class OtpChannel {
    ALL_AUTO,
    SMS,
    WHATSAPP,
    GMAIL
}

data class OtpDispatchResult(
    val success: Boolean,
    val otpCode: String,
    val channel: OtpChannel,
    val message: String
)

object FreeOtpSenderService {

    /**
     * Prepares OTP dispatch state cleanly without prohibited SEND_SMS background calls
     * or hardcoded external gateway URLs; actual device send actions use user-initiated intents.
     */
    suspend fun autoDispatchOtp(
        context: Context,
        phoneNumber: String,
        emailAddress: String,
        otpCode: String,
        channel: OtpChannel = OtpChannel.ALL_AUTO
    ): OtpDispatchResult = withContext(Dispatchers.Default) {
        val cleanPhone = phoneNumber.replace(Regex("\\D"), "")
        val targetEmail = if (emailAddress.isNotBlank()) emailAddress else "contact@royalpharmacy.in"

        val resultMsg = when (channel) {
            OtpChannel.ALL_AUTO -> "Verification code $otpCode ready via SMS, WhatsApp, or Email."
            OtpChannel.SMS -> "Verification code $otpCode ready for +91 $cleanPhone via Messaging app."
            OtpChannel.WHATSAPP -> "WhatsApp OTP link prepared for +91 $cleanPhone"
            OtpChannel.GMAIL -> "Email OTP prepared for $targetEmail"
        }

        OtpDispatchResult(
            success = true,
            otpCode = otpCode,
            channel = channel,
            message = resultMsg
        )
    }

    /**
     * Opens native WhatsApp intent with pre-filled OTP message.
     */
    fun launchWhatsAppOtp(context: Context, phoneNumber: String, otpCode: String) {
        val cleanPhone = phoneNumber.replace(Regex("\\D"), "")
        val formattedPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val message = "Your ROYAL PHARMACY security OTP is: $otpCode. Valid for 10 minutes."
        val encodedMsg = Uri.encode(message)
        val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMsg"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Toast.makeText(context, "Opening WhatsApp to send OTP: $otpCode", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp is not installed on this device", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Opens SMS app with pre-filled OTP message using zero-permission ACTION_SENDTO intent.
     */
    fun launchSmsOtp(context: Context, phoneNumber: String, otpCode: String) {
        val cleanPhone = phoneNumber.replace(Regex("\\D"), "")
        val message = "Your ROYAL PHARMACY security OTP is: $otpCode. Valid for 10 minutes."
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:+91$cleanPhone")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            Toast.makeText(context, "Opening Messaging app to send OTP: $otpCode", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Messaging app not available", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens Gmail / Mail app with pre-filled OTP message.
     */
    fun launchGmailOtp(context: Context, emailAddress: String, otpCode: String) {
        val targetEmail = if (emailAddress.isNotBlank()) emailAddress else "contact@royalpharmacy.in"
        val subject = Uri.encode("Royal Pharmacy Verification OTP Code")
        val body = Uri.encode("Your ROYAL PHARMACY security OTP code is: $otpCode\n\nValid for 10 minutes. Please enter this code in the login screen.")
        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$targetEmail?subject=$subject&body=$body")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(mailIntent)
            Toast.makeText(context, "Opening Email app to send OTP to $targetEmail", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Email app not available", Toast.LENGTH_SHORT).show()
        }
    }
}
