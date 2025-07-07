package com.client.xvideos.xvideos.screens.tags

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.client.xvideos.feature.net.readHtmlFromURLDirect
import com.client.xvideos.xvideos.screens.tags.model.ModelScreenTag
import com.client.xvideos.xvideos.parcer.parserScreenTags
import com.client.xvideos.urlStart
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.runBlocking


class ScreenTagsViewModel @AssistedInject constructor(
    @Assisted val tag: String,
): ScreenModel {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(tag: String): ScreenTagsViewModel
    }

    var screen = ModelScreenTag("", "", emptyList())

    init{
        val url = "$urlStart/tags/$tag"
        runBlocking {
            val html = readHtmlFromURLDirect(url)
            screen = parserScreenTags(html)
        }
    }





}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleTags {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(ScreenTagsViewModel.Factory::class)
    abstract fun bindScreenTagsScreenModel(
        hiltDetailsScreenModelFactory: ScreenTagsViewModel.Factory,
    ): ScreenModelFactory

}