package dev.game2048.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.game2048.app.data.local.dao.GameStateDao
import dev.game2048.app.data.local.dao.StatsDao
import dev.game2048.app.data.local.database.GameDatabase
import dev.game2048.app.data.local.datastore.SettingsDataStore
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GameDatabase =
        Room.databaseBuilder(context, GameDatabase::class.java, "game_2048_db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideGameStateDao(database: GameDatabase): GameStateDao = database.gameStateDao()

    @Provides
    fun provideStatsDao(database: GameDatabase): StatsDao = database.statsDao()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore = SettingsDataStore(context)
}
