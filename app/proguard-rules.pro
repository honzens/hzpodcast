# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep model classes for Gson serialization/deserialization and reflection
-keep class com.honzens.hzpodcast.classes.** { *; }
-keep class com.honzens.hzpodcast.ui.radio.RadioStation { *; }
-keep class com.honzens.hzpodcast.ui.download.DownloadedEpisode { *; }

# Preserve line number information and source files for debugging stack traces
-keepattributes SourceFile,LineNumberTable