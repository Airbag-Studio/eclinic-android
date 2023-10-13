package it.airbagstudio.ticare.di

import android.content.Context
import ch.ticare.eclinic.library.network.APIClient
import ch.ticare.eclinic.library.network.AuthRepository
import ch.ticare.eclinic.library.network.CredentialsListener
import ch.ticare.eclinic.library.repository.UserDetailRepository
import ch.ticare.eclinic.library.repository.UserListRepository
import ch.ticare.eclinic.library.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.airbagstudio.ticare.BuildConfig
import it.airbagstudio.ticare.LoginRedirect
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
/*
    @Provides
    @Singleton
    fun provideCredentialListener(@ApplicationContext context: Context): CredentialListenerImpl{
        return CredentialListenerImpl(provideAuthRepository(context))
    }
*/
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
    fun provideUserRepository(apiClient: APIClient): UserRepository {
        return UserRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideUserListRepository(apiClient: APIClient): UserListRepository {
        return UserListRepository(apiClient)
    }

    @Provides
    @Singleton
    fun provideUserDetailsRepository(apiClient: APIClient): UserDetailRepository {
        return UserDetailRepository(apiClient)
    }

}