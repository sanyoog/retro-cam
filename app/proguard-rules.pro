# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }

# Keep data classes used with Room
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep Compose
-keep class androidx.compose.** { *; }
