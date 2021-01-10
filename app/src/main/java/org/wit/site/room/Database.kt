package org.wit.site.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.wit.site.models.SiteModel

@Database(entities = arrayOf(SiteModel::class), version = 3,  exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract fun siteDao(): SiteDao
}