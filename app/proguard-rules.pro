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

-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }

-dontwarn com.client.common.AppPath
-dontwarn com.client.common.connectivityObserver.ConnectivityObserver
-dontwarn com.client.common.connectivityObserver.modileConnectivityObserver_ProvideConnectivityObserverFactory
-dontwarn com.client.common.di.CoroutinesModule_ProvideApplicationScopeFactory
-dontwarn com.client.common.preference.PreferencesRepository
-dontwarn com.client.common.preference.di.PreferencesModule_ProvidePreferencesDataStoreFactory
-dontwarn com.client.common.preference.di.PreferencesModule_ProvidePreferencesRepositoryFactory
-dontwarn com.client.common.util.KeepScreenOnKt
-dontwarn com.client.common.videoplayer.util.PlaybackPreference$Companion
-dontwarn com.client.common.videoplayer.util.PlaybackPreference
-dontwarn com.example.ui.screens.ScreenRedRoot
-dontwarn com.example.ui.screens.ScreenRedRootSM
-dontwarn com.example.ui.screens.explorer.tab.gifs.ScreenRedExplorerGifsSM
-dontwarn com.example.ui.screens.explorer.tab.niches.ScreenRedExplorerNichesSM
-dontwarn com.example.ui.screens.explorer.tab.saved.tab.ScreenSavedCollectionSM
-dontwarn com.example.ui.screens.explorer.tab.saved.tab.ScreenSavedCreatorSM
-dontwarn com.example.ui.screens.explorer.tab.saved.tab.ScreenSavedDownloadSM
-dontwarn com.example.ui.screens.explorer.tab.saved.tab.ScreenSavedLikesSM
-dontwarn com.example.ui.screens.explorer.tab.saved.tab.ScreenSavedNichesSM
-dontwarn com.example.ui.screens.explorer.tab.search.ScreenRedExplorerSearchSM
-dontwarn com.example.ui.screens.explorer.tab.setting.ScreenRedExplorerSettingSM
-dontwarn com.example.ui.screens.fullscreen.ScreenRedFullScreenSM
-dontwarn com.example.ui.screens.manager_block.ScreenRedManageBlockSM
-dontwarn com.example.ui.screens.niche.ScreenNicheSM$Factory
-dontwarn com.example.ui.screens.profile.ScreenRedProfileSM$Factory
-dontwarn com.example.ui.screens.top_this_week.ScreenRedTopThisWeekSM
-dontwarn com.redgifs.common.block.BlockRed
-dontwarn com.redgifs.common.di.HostDI
-dontwarn com.redgifs.common.downloader.DownloadRed
-dontwarn com.redgifs.common.downloader.Downloader
-dontwarn com.redgifs.common.downloader.di.moduleKDownloader_ProvideKDownloaderFactory
-dontwarn com.redgifs.common.saved.SavedRed
-dontwarn com.redgifs.common.saved.SavedRed_Collection
-dontwarn com.redgifs.common.saved.SavedRed_Creator
-dontwarn com.redgifs.common.saved.SavedRed_Likes
-dontwarn com.redgifs.common.saved.SavedRed_Niches
-dontwarn com.redgifs.common.search.SearchNichesRed
-dontwarn com.redgifs.common.search.SearchRed
-dontwarn com.redgifs.common.snackBar.SnackBarEvent
-dontwarn com.redgifs.network.api.RedApi

-keep class com.client.common.videoplayer.util.PlaybackPreference { *; }
-keepclassmembers class com.client.common.videoplayer.util.PlaybackPreference$* { *; }
-keepclassmembers class com.client.common.videoplayer.util.PlaybackPreference$Companion { *; }
-keep class com.client.common.** { *; }

# Сохраняем SavedRed и всё, что внутри пакета common.saved
-keep class com.redgifs.common.saved.SavedRed { *; }
-keep class com.redgifs.common.saved.SavedRed$* { *; }
-keep class com.redgifs.common.saved.** { *; }