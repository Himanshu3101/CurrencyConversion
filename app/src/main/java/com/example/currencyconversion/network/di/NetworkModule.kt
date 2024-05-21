package com.example.currencyconversion.network.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconversion.Utils.Constants.Companion.base_url
import com.example.currencyconversion.network.Database.CurrencyDataBase
import com.example.currencyconversion.network.server.retrofit.API
import com.example.currencyconversion.repository.LocalDataRepository
import com.example.currencyconversion.repository.ROOMRepository
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
class NetworkModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CurrencyDataBase {
        return Room.databaseBuilder(context,
            CurrencyDataBase::class.java,
            "Currency_Conversiondb"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(base_url)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun getSQLRepository(currencyDataBase: CurrencyDataBase): LocalDataRepository {
        return ROOMRepository(currencyDataBase)
    }


    @Provides
    fun provideSurveyApi(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }
}