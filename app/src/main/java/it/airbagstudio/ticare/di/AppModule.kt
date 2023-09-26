package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.network.APIClient
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.airbagstudio.ticare.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepositoryImpl(context)
    }

    @Provides
    fun provideApiService(@ApplicationContext context: Context): APIClient {
        return APIClient(BuildConfig.BASE_URL,provideAuthRepository(context))
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiClient: APIClient): UserRepository {
        return UserRepository(apiClient)
    }
}