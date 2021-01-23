package org.wit.site.views.sitelist

import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.site.main.MainApp
import org.wit.site.models.SiteModel
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView
import org.wit.site.views.VIEW

class SiteListPresenter(view: BaseView): BasePresenter(view){

  fun doAddSite(){
    view?.navigateTo(VIEW.SITE)
  }

  fun doEditSite(site: SiteModel){
    view?.navigateTo(VIEW.SITE, 0, "site_edit", site)
  }

  fun doShowSitesMap(){
    view?.navigateTo(VIEW.MAPS)
  }

  fun doLogout() {
    FirebaseAuth.getInstance().signOut()
    app.sites.clear()
    view?.navigateTo(VIEW.LOGIN)
  }

  fun doSettings(){
    view?.navigateTo(VIEW.SETTINGS)
  }

  fun doFavourite(){
    view?.navigateTo(VIEW.FAVOURITE)
  }

  fun doNavigator(){
    view?.navigateTo(VIEW.NAVIGATOR)
  }

  fun loadSites() {
    doAsync {
      val sites = app.sites.findAll()
      uiThread {
        view?.showSites(sites)
      }
    }
  }

  fun searchSites(choice: String?, text: String) {
    doAsync {
      var sites = app.sites.findAll().filter { it.name.toLowerCase().contains(text.toLowerCase()) }
      if(choice == "desc") {
        sites = app.sites.findAll().filter { it.description.toLowerCase().contains(text.toLowerCase()) }
      }else if(choice == "note"){
        sites = app.sites.findAll().filter { it.notes.toLowerCase().contains(text.toLowerCase()) }
      }
      uiThread {
        view?.showSites(sites)
      }
    }
  }
}