package org.wit.site.views.sitelist

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.site.models.SiteModel
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView
import org.wit.site.views.VIEW

class SiteListPresenter(view: BaseView) : BasePresenter(view) {

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
    view?.navigateTo(VIEW.LOGIN)
  }

  fun loadSites() {
    doAsync {
      val sites = app.sites.findAll()
      uiThread {
        view?.showSites(sites)
      }
    }
  }
}