# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep the launcher activity and related components
-keep public class com.bblauncher.** { *; }

# Keep all Compose classes
-keep class androidx.compose.** { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep Material3
-keep class androidx.compose.material3.** { *; }

# Keep Lifecycle
-keep class androidx.lifecycle.** { *; }

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Don't obfuscate launcher-specific classes
-keepnames class com.bblauncher.**
