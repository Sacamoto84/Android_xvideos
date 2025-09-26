package com.client.xvideos.xvideos.feature.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedX @Inject constructor(
    //val db: AppLDatabase,
    snackBarEvent: SnackBarEvent,
    @ApplicationScope val scope: CoroutineScope,
    //kDownloader: KDownloader,
) {

    val favorites = SavedX_Favorites(snackBarEvent, scope)

    //val collection = SavedL_Collection(snackBarEvent)

    //val albums = SavedL_Albums(snackBarEvent, db, scope)
    //val likes = SavedL_Likes(snackBarEvent, kDownloader)
    //val crypto = SavedL_Crypto(snackBarEvent, kDownloader)
}

