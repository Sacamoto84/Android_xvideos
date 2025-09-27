package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedL @Inject constructor(
    val db: AppDatabase,
    @ApplicationScope val scope: CoroutineScope,
    kDownloader: KDownloader,
) {

    val collection = SavedL_Collection()

    val albums = SavedL_Albums(db, scope)
    val likes = SavedL_Likes(kDownloader)
    val crypto =  SavedL_Crypto(kDownloader)
}

