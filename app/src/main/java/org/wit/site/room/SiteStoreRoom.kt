package org.wit.site.room

import android.content.Context
import androidx.room.Room
import org.wit.site.models.SiteModel
import org.wit.site.models.SiteStore

class SiteStoreRoom(val context: Context): SiteStore {

    var dao: SiteDao

    init{
        val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
            .fallbackToDestructiveMigration()
            .build()
        dao = database.siteDao()
    }

    override fun findAll(): List<SiteModel> {
        return dao.findAll()
    }

    override fun findById(id: Long): SiteModel? {
        return dao.findById(id)
    }

    override fun create(site: SiteModel) {
        dao.create(site)
    }

    override fun update(site: SiteModel) {
        dao.update(site)
    }

    override fun delete(site: SiteModel) {
        dao.deleteSite(site)
    }
}