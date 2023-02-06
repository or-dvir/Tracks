package com.hotmail.or_dvir.tracks.di

import android.app.Application
import com.hotmail.or_dvir.tracks.MyApplication
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepository
import com.hotmail.or_dvir.tracks.database.repositories.TrackedEventsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindTrackedEventsRepository(
        impl: TrackedEventsRepositoryImpl
    ): TrackedEventsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModuleHelper {
    @Provides
    @Singleton
    fun provideCoroutineScopeThatShouldNotBeCancelled(app: Application) =
        (app as MyApplication).scopeThatShouldNotBeCancelled

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO
}
