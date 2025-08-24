package com.client.xvideos.l

import com.client.common.di.ApplicationScope
import com.client.xvideos.l.db.AppLDatabase
import com.client.xvideos.l.net.Luscious
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedL @Inject constructor(
    val db: AppLDatabase,
    val api : Luscious,
    //snackBarEvent : SnackBarEvent,
    @ApplicationScope val scope : CoroutineScope
) {







}