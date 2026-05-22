package com.uade.costaapp.di

import android.content.Context
import androidx.room.Room
import com.uade.costaapp.data.local.CostaAppDatabase
import com.uade.costaapp.data.local.dao.PropertyDao
import com.uade.costaapp.data.mapper.PropertyMapper
import com.uade.costaapp.data.remote.CostaAppApiService
import com.uade.costaapp.data.repository.PropertyRepositoryImpl
import com.uade.costaapp.domain.repository.PropertyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        ).build()
    }

    @Provides
    fun providePropertyDao(database: CostaAppDatabase): PropertyDao {
        return database.propertyDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            // BASE_URL ajustada para apuntar al entorno servido por GitHub Pages
            .baseUrl("https://razayas15.github.io/DA1-TPO-2026-CostaApp/")
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
        mapper: PropertyMapper
    ): PropertyRepository {
        return PropertyRepositoryImpl(api, dao, mapper)
    }
}
