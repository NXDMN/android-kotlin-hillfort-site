package org.wit.site.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.site.models.SiteJSONStore
import org.wit.site.models.SiteMemStore
import org.wit.site.models.SiteStore

class MainApp : Application(), AnkoLogger {

  lateinit var sites: SiteStore

  override fun onCreate() {
    super.onCreate()
    sites = SiteJSONStore(applicationContext)
    info("Site started")
  }
}