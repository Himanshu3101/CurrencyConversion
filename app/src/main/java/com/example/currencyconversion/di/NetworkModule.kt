package com.example.currencyconversion.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconversion.utils.Constants.Companion.base_url
import com.example.currencyconversion.network.database.CurrencyDataBase
import com.example.currencyconversion.network.server.API
import com.example.currencyconversion.repository.ROOMRepository
import com.example.currencyconversion.repository.ServerRepository
import com.example.currencyconversion.repository.interfaces.LocalDataRepository
import com.example.currencyconversion.utils.NetworkConnectivityImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CurrencyDataBase {
        return Room.databaseBuilder(
            context,
            CurrencyDataBase::class.java,
            "Currency_Conversiondb"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(base_url)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideServerRepository(
        @ApplicationContext context: Context,
        remoteDataSource: API,
        roomRepository: LocalDataRepository,
        dispatcher: CoroutineDispatcher,
        networkConnectivity: NetworkConnectivityImpl
    ): ServerRepository {
        return ServerRepository(
            context,
            remoteDataSource,
            roomRepository,
            dispatcher,
            networkConnectivity
        )
    }

    @Provides
    fun provideSurveyApi(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }

    @Provides
    fun provideDataRepository(currencyDataBase: CurrencyDataBase): LocalDataRepository {
        return ROOMRepository(currencyDataBase)
    }

    @Provides
    fun provideDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivityImpl {
        return NetworkConnectivityImpl(context)
    }


}