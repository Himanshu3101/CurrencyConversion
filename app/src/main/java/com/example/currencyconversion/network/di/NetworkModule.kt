package com.example.currencyconversion.network.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconversion.Utils.Constants.Companion.base_url
import com.example.currencyconversion.network.Database.CurrencyDataBase
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.Utils.Cache
import com.example.currencyconversion.repository.DataRepository
import com.example.currencyconversion.repository.LocalDataRepository
import com.example.currencyconversion.repository.ROOMRepository
import com.example.currencyconversion.repository.ServerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideServerRepository(@ApplicationContext context: Context, remoteDataSource: API): ServerRepository {
        return ServerRepository(context,remoteDataSource)
    }

    @Provides
    fun provideSurveyApi(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }

    @Provides
    fun getSQLRepository(currencyDataBase: CurrencyDataBase): LocalDataRepository {
        return ROOMRepository(currencyDataBase)
    }

    @Provides
    fun provideCache(): Cache {
        return Cache()
    }

    @Provides
    fun provideDataRepository(serverRepository: ServerRepository, cache: Cache): DataRepository {
        return DataRepository(serverRepository, cache)
    }
}