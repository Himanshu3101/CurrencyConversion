package com.example.currencyconversion.di

import com.example.currencyconversion.di.CoroutineDispatcherModule
import com.example.currencyconversion.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher


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
}