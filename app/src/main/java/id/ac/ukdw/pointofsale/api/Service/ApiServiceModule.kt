package id.ac.ukdw.pointofsale.api.Service

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideApiClient(): ApiClientInterface {
        return ApiClient
    }

    @Provides
    @Singleton
    fun provideApiService(apiClient: ApiClientInterface): ApiService {
        return apiClient.instance
    }
}
