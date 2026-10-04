# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve generic signatures and annotations for Gson reflection and TypeToken
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses

# Keep Gson TypeToken subclasses
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers class * extends com.google.gson.reflect.TypeToken { *; }

# Keep model classes and fields for Gson serialization/deserialization and reflection
-keep class com.honzens.hzpodcast.classes.** { *; }
-keepclassmembers class com.honzens.hzpodcast.classes.** { *; }
-keep class com.honzens.hzpodcast.ui.radio.RadioStation { *; }
-keepclassmembers class com.honzens.hzpodcast.ui.radio.RadioStation { *; }
-keep class com.honzens.hzpodcast.ui.download.DownloadedEpisode { *; }
-keepclassmembers class com.honzens.hzpodcast.ui.download.DownloadedEpisode { *; }

# Preserve line number information and source files for debugging stack traces
-keepattributes SourceFile,LineNumberTable