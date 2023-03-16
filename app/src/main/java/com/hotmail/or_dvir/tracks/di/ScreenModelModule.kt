package com.hotmail.or_dvir.tracks.di

import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.hotmail.or_dvir.tracks.ui.eventOccurrenceScreen.EventOccurrencesViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class ScreenModelModule {
    @Binds
    @IntoMap
    @ScreenModelFactoryKey(EventOccurrencesViewModel.Factory::class)
    abstract fun bindEventOccurrencesViewModelFactory(
        factory: EventOccurrencesViewModel.Factory
    ): ScreenModelFactory
}
