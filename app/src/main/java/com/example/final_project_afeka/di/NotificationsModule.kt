package com.example.final_project_afeka.di

import android.app.NotificationManager
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.final_project_afeka.utils.notifications.NotificationHandler
import com.example.final_project_afeka.utils.notifications.NotificationHandlerImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {

    @Binds
    abstract fun bindNotificationManager(impl: NotificationHandlerImpl): NotificationHandler
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationManagerProvider {
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}