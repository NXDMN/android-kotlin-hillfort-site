package org.wit.site.views.favourite

import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.site.models.SiteModel
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView
import org.wit.site.views.VIEW

class FavouritePresenter(view: BaseView): BasePresenter(view) {

  fun loadSites() {
    doAsync {
      val favouriteSites = app.sites.findAll().filter { it.favourite }
      uiThread {
        view?.showSites(favouriteSites)
      }
    }
  }

  fun doEditSite(site: SiteModel){
    view?.navigateTo(VIEW.SITE, 0, "site_edit", site)
  }
}