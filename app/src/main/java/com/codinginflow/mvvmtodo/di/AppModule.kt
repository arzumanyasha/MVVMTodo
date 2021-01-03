package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    //The reason because we use @Provides instead of @Inject - we didn't own this class
    // we can't go into Room and mark constructor with @Inject. If we use our own class and
    // there is no additional method calls like fallbackTo... etc. then we can use @Inject
    @Provides
    @Singleton
    fun provideDatabase(
            app: Application,
            callback: TaskDatabase.Callback
    ) =
            Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
                    //What should we do when DB schema is changed. In this case drops and creates new one
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    //this scope lives as long as application lives
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope