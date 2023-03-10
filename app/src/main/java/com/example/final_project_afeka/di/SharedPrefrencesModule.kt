package com.example.final_project_afeka.di

import android.content.Context
import android.content.SharedPreferences
import com.example.final_project_afeka.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences(
            appContext.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )


}