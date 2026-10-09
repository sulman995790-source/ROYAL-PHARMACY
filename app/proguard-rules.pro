# Add project specific ProGuard rules here.
-keep class com.example.model.** { *; }
-keep class com.example.data.model.** { *; }
-keep class com.example.data.** { *; }
-keep class com.example.viewmodel.** { *; }

# Apache POI & Excel
-dontwarn org.apache.poi.**
-keep class org.apache.poi.** { *; }

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Moshi
-keep class com.squareup.moshi.** { *; }
-keepattributes *Annotation*,Signature,EnclosingMethod
