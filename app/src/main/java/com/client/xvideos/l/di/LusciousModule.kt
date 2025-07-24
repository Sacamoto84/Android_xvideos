package com.client.xvideos.l.di

import com.client.xvideos.Secrets
import com.client.xvideos.l.Luscious
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LusciousModule {

    @Singleton
    @Provides
    fun provideLuscious(): Luscious {
        val email = Secrets.lusciousEmail
        val password = Secrets.lusciousPassword
        val luscious = Luscious(email, password)
        return luscious
    }


}