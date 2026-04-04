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
import com.hubenko.data.repository.AuthRepositoryImpl
import com.hubenko.data.repository.EmployeeRepositoryImpl
import com.hubenko.data.repository.ReminderRepositoryImpl
import com.hubenko.data.repository.RoleRepositoryImpl
import com.hubenko.data.repository.SettingsRepositoryImpl
import com.hubenko.data.repository.StatusRepositoryImpl
import com.hubenko.data.repository.StatusTypeRepositoryImpl
import com.hubenko.data.worker.AlarmScheduler
import com.hubenko.domain.manager.ReminderManager
import com.hubenko.domain.repository.AuthRepository
import com.hubenko.domain.repository.EmployeeRepository
import com.hubenko.domain.repository.ReminderRepository
import com.hubenko.domain.repository.RoleRepository
import com.hubenko.domain.repository.SettingsRepository
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.repository.StatusTypeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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

    @Provides
    @Singleton
    fun provideStatusRepository(impl: StatusRepositoryImpl): StatusRepository = impl

    @Provides
    @Singleton
    fun provideEmployeeRepository(impl: EmployeeRepositoryImpl): EmployeeRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository = impl

    @Provides
    @Singleton
    fun provideReminderManager(impl: AlarmScheduler): ReminderManager = impl

    @Provides
    @Singleton
    fun provideRoleRepository(impl: RoleRepositoryImpl): RoleRepository = impl

    @Provides
    @Singleton
    fun provideStatusTypeRepository(impl: StatusTypeRepositoryImpl): StatusTypeRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository = impl
}
