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
import ch.ticare.eclinic.library.repository.LocalStorageApi
import ch.ticare.eclinic.library.repository.NursingCourseRepository
import ch.ticare.eclinic.library.repository.OfflineOnlineRepository
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
    fun provideApiService(authRepository: AuthRepository): APIClient {
        return APIClient(authRepository,LoginRedirect)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiClient: APIClient,database: Database): UserRepository {
        return UserRepository(apiClient, database)
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(apiClient: APIClient): DiaryRepository {
        return DiaryRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideOfflineOnlineRepository(database: Database): OfflineOnlineRepository{
        return OfflineOnlineRepository(database)
    }

    @Provides
    @Singleton
    fun provideLocalStorageApi(@ApplicationContext context: Context): LocalStorageImpl{
        return LocalStorageImpl(context)
    }

    @Provides
    @Singleton
    fun syncDataRepository(apiClient: APIClient,database: Database,localStorageImpl: LocalStorageImpl,offlineOnlineRepository: OfflineOnlineRepository): SyncDataRepository {
        return SyncDataRepository(apiClient,database,localStorageImpl,offlineOnlineRepository)
    }

    @Provides
    @Singleton
    fun provideUserListRepository(apiClient: APIClient,database: Database,offlineOnlineRepository: OfflineOnlineRepository): UserListRepository {
        return UserListRepository(apiClient,database,offlineOnlineRepository)
    }

    @Provides
    @Singleton
    fun otherServiceRepository(apiClient: APIClient,database: Database): OtherServiceRepository{
        return OtherServiceRepository(apiClient,database)
    }

    @Provides
    @Singleton
    fun provideCaseAllergiesRepository(apiClient: APIClient): CaseAllergiesRepository{
        return CaseAllergiesRepository(apiClient)
    }

    @Provides
    @Singleton
    fun providesAgendaTaskRepositoryRepository(apiClient: APIClient,database: Database): AgendaTaskRepository{
        return AgendaTaskRepository(apiClient,database)
    }

    @Provides
    @Singleton
    fun provideUserDetailsRepository(apiClient: APIClient,database: Database,offlineOnlineRepository: OfflineOnlineRepository): UserDetailRepository {
        return UserDetailRepository(apiClient,database,offlineOnlineRepository)
    }

    @Provides
    @Singleton
    fun provideWoundRepository(apiClient: APIClient,database: Database,offlineOnlineRepository: OfflineOnlineRepository,localStorageImpl: LocalStorageImpl): WoundRepository {
        return WoundRepository(apiClient,database,offlineOnlineRepository,localStorageImpl)
    }

    @Provides
    @Singleton
    fun provideHomeCareActivitiesRepository(apiClient: APIClient,database: Database): HomeCareActivitiesRepository {
        return HomeCareActivitiesRepository(apiClient,database)
    }

    @Provides
    @Singleton
    fun providesNursingCourseRepository(apiClient: APIClient): NursingCourseRepository {
        return NursingCourseRepository(apiClient)
    }

    @Provides
    @Singleton
    fun providesUserMarkingRepositoryRepository(apiClient: APIClient,database: Database): UserMarkingRepository {
        return UserMarkingRepository(apiClient,database)
    }

    @Provides
    @Singleton
    fun providesConsumptionRepository(apiClient: APIClient,database: Database): ConsumptionRepository {
        return ConsumptionRepository(apiClient,database)
    }

    @Provides
    @Singleton
    fun providesWorkingHoursRepository(apiClient: APIClient,database: Database): WorkingHourRepository {
        return WorkingHourRepository(apiClient, database)
    }

}