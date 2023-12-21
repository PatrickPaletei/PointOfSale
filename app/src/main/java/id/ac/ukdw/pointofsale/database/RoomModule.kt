package id.ac.ukdw.pointofsale.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.ac.ukdw.pointofsale.database.dao.MenuDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MenuDatabase {
        return Room.databaseBuilder(
            context,
            MenuDatabase::class.java, "menu_database"
        ).build()
    }

    @Provides
    fun provideMenuDao(database: MenuDatabase): MenuDao {
        return database.menuDao()
    }
}
