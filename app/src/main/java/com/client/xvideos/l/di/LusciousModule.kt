package com.client.xvideos.l.di

import com.client.common.di.ApplicationScope
import com.client.xvideos.BuildConfig
import com.client.xvideos.l.Luscious
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LusciousModule {

    @Singleton
    @Provides
    fun provideLuscious(
        @ApplicationScope scope: CoroutineScope
    ): Luscious {
        val email = BuildConfig.luscious_email
        val password = BuildConfig.luscious_password
        val luscious = Luscious(scope, email, password)
        return luscious
    }


}