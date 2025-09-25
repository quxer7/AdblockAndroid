# ProGuard rules for Kotlin Serialization

-keepattributes InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Change here com.yourcompany.yourpackage
-keep,includedescriptorclasses class com.quxer7.adfilter.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.quxer7.adfilter.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.quxer7.adfilter.adfilter.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}