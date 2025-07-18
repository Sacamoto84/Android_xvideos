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