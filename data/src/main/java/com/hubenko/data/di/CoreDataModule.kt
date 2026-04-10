package com.hubenko.data.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.AppDatabase
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.local.dao.ReminderSettingsDao
import com.hubenko.data.local.dao.StatusTypeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreDataModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "employee_status_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideEmployeeDao(db: AppDatabase): EmployeeDao = db.employeeDao()

    @Provides
    fun provideStatusDao(db: AppDatabase): EmployeeStatusDao = db.employeeStatusDao()

    @Provides
    fun provideReminderSettingsDao(db: AppDatabase): ReminderSettingsDao = db.reminderSettingsDao()

    @Provides
    fun provideStatusTypeDao(db: AppDatabase): StatusTypeDao = db.statusTypeDao()
}
