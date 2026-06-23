package com.uade.costaapp.di

import android.content.Context
import androidx.room.Room
import com.uade.costaapp.data.local.CostaAppDatabase
import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.data.repository.PropertyRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CostaAppDatabase {
        return Room.databaseBuilder(
            context,
            CostaAppDatabase::class.java,
            "costaapp_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun providePropertyDao(database: CostaAppDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/razayas15/costaapp-api/refs/heads/main/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCostaAppApiService(retrofit: Retrofit): CostaAppApiService {
        return retrofit.create(CostaAppApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePropertyRepository(
        api: CostaAppApiService,
        dao: PropertyDao,
        mapper: PropertyMapper,
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): PropertyRepository {
        return PropertyRepositoryImpl(api, dao, mapper, auth, firestore)
    }
}
