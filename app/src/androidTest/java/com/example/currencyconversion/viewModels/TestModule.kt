package com.example.currencyconversion.viewModels

import com.example.currencyconversion.network.di.CoroutineDispatcherModule
import com.example.currencyconversion.network.di.IoDispatcher
import com.example.currencyconversion.repository.ROOMRepository
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import org.mockito.Mockito.mock


@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutineDispatcherModule::class]
)
@Module
object TestModule {
    @Provides
    @IoDispatcher
    fun provideTestDispatcher(): CoroutineDispatcher {
        return StandardTestDispatcher()
    }

    @Provides
    fun provideRoomRepository(): ROOMRepository {
        return mock(ROOMRepository::class.java)
    }
}