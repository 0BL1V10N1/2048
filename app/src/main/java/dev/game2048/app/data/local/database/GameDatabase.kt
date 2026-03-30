package dev.game2048.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.game2048.app.data.local.converters.GameConverters
import dev.game2048.app.data.local.dao.GameStateDao
import dev.game2048.app.data.local.dao.StatsDao
import dev.game2048.app.data.local.entity.GameStateEntity
import dev.game2048.app.data.local.entity.GameStatsEntity

@Database(
    entities = [GameStateEntity::class, GameStatsEntity::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(GameConverters::class)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameStateDao(): GameStateDao

    abstract fun statsDao(): StatsDao
}
