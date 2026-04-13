package com.hubenko.data.di

import com.hubenko.data.repository.DataStoreSettingsDataSource
import com.hubenko.data.repository.FirebaseAuthDataSource
import com.hubenko.data.repository.FirestoreBaseRateDataSource
import com.hubenko.data.repository.FirestoreHourlyRateDataSource
import com.hubenko.data.repository.FirestoreRoleDataSource
import com.hubenko.data.repository.FirestoreStatusTypeDataSource
import com.hubenko.data.repository.OfflineFirstEmployeeRepository
import com.hubenko.data.repository.OfflineFirstReminderRepository
import com.hubenko.data.repository.OfflineFirstStatusRepository
import com.hubenko.data.worker.AlarmScheduler
import com.hubenko.domain.manager.ReminderManager
import com.hubenko.domain.repository.AuthDataSource
import com.hubenko.domain.repository.BaseRateDataSource
import com.hubenko.domain.repository.EmployeeRepository
import com.hubenko.domain.repository.HourlyRateDataSource
import com.hubenko.domain.repository.ReminderRepository
import com.hubenko.domain.repository.RoleDataSource
import com.hubenko.domain.repository.SettingsDataSource
import com.hubenko.domain.repository.StatusRepository
import com.hubenko.domain.repository.StatusTypeDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(impl: FirebaseAuthDataSource): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(impl: OfflineFirstEmployeeRepository): EmployeeRepository

    @Binds
    @Singleton
    abstract fun bindStatusRepository(impl: OfflineFirstStatusRepository): StatusRepository

    @Binds
    @Singleton
    abstract fun bindStatusTypeDataSource(impl: FirestoreStatusTypeDataSource): StatusTypeDataSource

    @Binds
    @Singleton
    abstract fun bindRoleDataSource(impl: FirestoreRoleDataSource): RoleDataSource

    @Binds
    @Singleton
    abstract fun bindBaseRateDataSource(impl: FirestoreBaseRateDataSource): BaseRateDataSource

    @Binds
    @Singleton
    abstract fun bindHourlyRateDataSource(impl: FirestoreHourlyRateDataSource): HourlyRateDataSource

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: OfflineFirstReminderRepository): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindReminderManager(impl: AlarmScheduler): ReminderManager

    @Binds
    @Singleton
    abstract fun bindSettingsDataSource(impl: DataStoreSettingsDataSource): SettingsDataSource
}
