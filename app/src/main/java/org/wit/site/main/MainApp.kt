package org.wit.site.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.wit.site.models.SiteMemStore

class MainApp : Application(), AnkoLogger {

  val sites = SiteMemStore()

  override fun onCreate() {
    super.onCreate()
    info("Site started")

  }
}