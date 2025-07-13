package com.redgifs.common.downloader.di

import android.content.Context
import com.kdownloader.KDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object moduleKDownloader {
    @Singleton
    @Provides
    fun provideKDownloader(@ApplicationContext applicationContext: Context): KDownloader {
        return KDownloader.create(applicationContext)
    }
}