package org.wit.site.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.site.models.SiteStore
import org.wit.site.models.firebase.SiteFireStore
import org.wit.site.models.json.SiteJSONStore
import org.wit.site.models.mem.SiteMemStore
import org.wit.site.room.SiteStoreRoom

class MainApp : Application(), AnkoLogger {

  lateinit var sites: SiteStore

  override fun onCreate() {
    super.onCreate()
//    sites = SiteMemStore()
//    sites = SiteJSONStore(applicationContext)
//    sites = SiteStoreRoom(applicationContext)
    sites = SiteFireStore(applicationContext)
    info("Site started")
  }
}