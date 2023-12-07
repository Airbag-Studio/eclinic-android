package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.database.DriverFactory
import ch.ticare.eclinic.library.database.createDatabase
import ch.ticare.eclinic.library.db.Database
import ch.ticare.eclinic.library.entity.Wound
import ch.ticare.eclinic.library.network.APIClient
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.AgendaTaskRepository
import ch.ticare.eclinic.library.repository.CaseAllergiesRepository
import ch.ticare.eclinic.library.repository.ConsumptionRepository
import ch.ticare.eclinic.library.repository.DiaryRepository
import ch.ticare.eclinic.library.repository.HomeCareActivitiesRepository
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import ch.ticare.eclinic.library.repository.OtherServiceRepository
import ch.ticare.eclinic.library.repository.SyncDataRepository
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import ch.ticare.eclinic.library.repository.UserMarkingRepository
import ch.ticare.eclinic.library.repository.UserRepository
import ch.ticare.eclinic.library.repository.WorkingHourRepository
import ch.ticare.eclinic.library.repository.WoundRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.airbagstudio.ticare.LoginRedirect
import it.airbagstudio.ticare.pages.diary.DiaryUIState
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return createDatabase(DriverFactory(context))
    }

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): APIClient {
        return APIClient(provideAuthRepository(context),LoginRedirect)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiClient: APIClient,@ApplicationContext context: Context): UserRepository {
        return UserRepository(apiClient, provideDatabase(context))
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(apiClient: APIClient): DiaryRepository {
        return DiaryRepository(apiClient)
    }

    @Provides
    @Singleton
    fun syncDataRepository(apiClient: APIClient,@ApplicationContext context: Context): SyncDataRepository {
        return SyncDataRepository(apiClient, provideDatabase(context))
    }

    @Provides
    @Singleton
    fun provideUserListRepository(apiClient: APIClient,@ApplicationContext context: Context): UserListRepository {
        return UserListRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun otherServiceRepository(apiClient: APIClient,@ApplicationContext context: Context): OtherServiceRepository{
        return OtherServiceRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun provideCaseAllergiesRepository(apiClient: APIClient): CaseAllergiesRepository{
        return CaseAllergiesRepository(apiClient)
    }

    @Provides
    @Singleton
    fun providesAgendaTaskRepositoryRepository(apiClient: APIClient,@ApplicationContext context: Context): AgendaTaskRepository{
        return AgendaTaskRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun provideUserDetailsRepository(apiClient: APIClient): UserDetailRepository {
        return UserDetailRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideWoundRepository(apiClient: APIClient,@ApplicationContext context: Context): WoundRepository {
        return WoundRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun provideHomeCareActivitiesRepository(apiClient: APIClient,@ApplicationContext context: Context): HomeCareActivitiesRepository {
        return HomeCareActivitiesRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun providesNursingCourseRepository(apiClient: APIClient): NursingCourseRepository {
        return NursingCourseRepository(apiClient)
    }

    @Provides
    @Singleton
    fun providesUserMarkingRepositoryRepository(apiClient: APIClient,@ApplicationContext context: Context): UserMarkingRepository {
        return UserMarkingRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun providesConsumptionRepository(apiClient: APIClient,@ApplicationContext context: Context): ConsumptionRepository {
        return ConsumptionRepository(apiClient,provideDatabase(context))
    }

    @Provides
    @Singleton
    fun providesWorkingHoursRepository(apiClient: APIClient,@ApplicationContext context: Context): WorkingHourRepository {
        return WorkingHourRepository(apiClient, provideDatabase(context))
    }

}