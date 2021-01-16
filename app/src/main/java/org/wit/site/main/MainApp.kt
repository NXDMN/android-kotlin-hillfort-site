package org.wit.site.main

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//    sites = SiteMemStore()
//    sites = SiteJSONStore(applicationContext)
//    sites = SiteStoreRoom(applicationContext)
    sites = SiteFireStore(applicationContext)
    info("Site started")
  }
}