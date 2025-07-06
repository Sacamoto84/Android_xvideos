# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature
-keepattributes *Annotation*

-dontobfuscate

#-keep class com.client.xvideos.feature.redgifs.types.MediaItem { *; }
#-keep class com.client.xvideos.feature.redgifs.types.GifInfoItem { *; }
#-keep class com.client.xvideos.feature.redgifs.types.ImageInfoItem { *; }

# MediaType.Companion is object → keep its ctor
-keepclassmembers class okhttp3.MediaType$Companion {
    <init>();
}

-keep class org.slf4j.** { *; }
-keep class **$$ExternalSyntheticLambda* { *; }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class io.ktor.util.collections.** { *; }

-dontwarn java.lang.management.**

# Не обфусцировать и не удалять androidx.tracing.Trace
-keep class androidx.tracing.Trace { *; }

# Сохраняем классы coil.size.Dimension и все его подклассы
-keep class coil.size.** { *; }

# Сохраняем конструкторы без изменений
-keepclassmembers class coil.size.** {
    public <init>(...);
}

# Иногда полезно сохранить все модели, которые сериализует Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn coil.**
-keep class coil.** { *; }
