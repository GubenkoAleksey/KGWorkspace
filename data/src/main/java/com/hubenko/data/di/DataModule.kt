package com.hubenko.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hubenko.data.local.AppDatabase
import com.hubenko.data.local.dao.EmployeeDao
import com.hubenko.data.local.dao.EmployeeStatusDao
import com.hubenko.data.repository.AuthRepositoryImpl
import com.hubenko.data.repository.EmployeeRepositoryImpl
import com.hubenko.data.repository.StatusRepositoryImpl
import com.hubenko.domain.repository.AuthRepository
import com.hubenko.domain.repository.EmployeeRepository
import com.hubenko.domain.repository.StatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Migration from version 10 to 11: removes the plaintext `password` column from the
     * `employees` table. SQLite does not support DROP COLUMN directly on older API levels,
     * so we recreate the table without the sensitive column.
     */
    private val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS employees_new (
                    id TEXT NOT NULL PRIMARY KEY,
                    lastName TEXT NOT NULL,
                    firstName TEXT NOT NULL,
                    middleName TEXT NOT NULL,
                    phoneNumber TEXT NOT NULL,
                    role TEXT NOT NULL,
                    email TEXT NOT NULL DEFAULT ''
                )
                """.trimIndent()
            )
            db.execSQL(
                "INSERT INTO employees_new (id, lastName, firstName, middleName, phoneNumber, role, email) " +
                        "SELECT id, lastName, firstName, middleName, phoneNumber, role, email FROM employees"
            )
            db.execSQL("DROP TABLE employees")
            db.execSQL("ALTER TABLE employees_new RENAME TO employees")
        }
    }

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
        .addMigrations(MIGRATION_10_11)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideEmployeeDao(db: AppDatabase): EmployeeDao = db.employeeDao()

    @Provides
    fun provideStatusDao(db: AppDatabase): EmployeeStatusDao = db.employeeStatusDao()

    @Provides
    @Singleton
    fun provideStatusRepository(impl: StatusRepositoryImpl): StatusRepository = impl

    @Provides
    @Singleton
    fun provideEmployeeRepository(impl: EmployeeRepositoryImpl): EmployeeRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
}
