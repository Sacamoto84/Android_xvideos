package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.fileDB.folder.AppFileDatabase
import com.client.xvideos.l.net.Luscious
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedL @Inject constructor(
    val db: AppFileDatabase,
    @ApplicationScope val scope: CoroutineScope,
    luscious: Luscious
) {

    val collection = SavedL_Collection(scope, luscious)

    val albums = SavedL_Albums(db, scope)
    val likes = SavedL_Likes(luscious, scope)
}
