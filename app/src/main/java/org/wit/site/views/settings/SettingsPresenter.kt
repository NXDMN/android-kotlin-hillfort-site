package org.wit.site.views.settings

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.site.main.MainApp

class SettingsPresenter(val view: SettingsFragment){

  var app: MainApp

  init{
    app = view.activity?.application as MainApp
  }

  fun doGetEmail(callback: (String) -> Unit){
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
      callback(user.email.toString())
    }
  }

  fun doGetTotalSites(callback: (String) -> Unit) {
    doAsync {
      val sites = app.sites.findAll()
      uiThread {
        callback(sites.size.toString())
      }
    }
  }

  fun doGetVisitedSites(callback: (String) -> Unit) {
    doAsync {
      val visitedSites = app.sites.findAll().filter { it.visited }
      uiThread {
        callback(visitedSites.size.toString())
      }
    }
  }

}