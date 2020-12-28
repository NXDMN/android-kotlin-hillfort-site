package org.wit.site.views.site

import android.content.Intent
import org.wit.site.helpers.showImagePicker
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.*

class SitePresenter(view: BaseView) : BasePresenter(view) {

  var site = SiteModel()
  var defaultLocation = Location(52.245696, -7.139102, 15f)
  var edit = false;


  init{
    if (view.intent.hasExtra("site_edit")) {
      edit = true
      site = view.intent.extras?.getParcelable<SiteModel>("site_edit")!!
      view.showSite(site)
    }
  }

  fun doAddOrSave(name: String, description: String, visited: Boolean, date: String, notes: String) {
    site.name = name
    site.description = description
    site.visited = visited
    site.date = date
    site.notes = notes
    if (edit) {
      app.sites.update(site)
    } else {
      app.sites.create(site)
    }
    view?.finish()
  }

  fun doCancel() {
    view?.finish()
  }

  fun doDelete() {
    app.sites.delete(site)
    view?.finish()
  }

  fun doSelectImage() {
    view?.let {
      showImagePicker(view!!, IMAGE_REQUEST)
    }
  }

  fun doSetLocation() {
    if (!edit) {
      view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", defaultLocation)
    }else{
      view?.navigateTo(
          VIEW.LOCATION,
          LOCATION_REQUEST,
          "location",
          Location(site.lat, site.lng, site.zoom)
      )
    }
  }

  fun cacheSite (name: String, description: String, visited: Boolean, date: String, notes: String) {
    site.name = name;
    site.description = description
    site.visited = visited
    site.date = date
    site.notes = notes
  }

  override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    when (requestCode) {
      IMAGE_REQUEST -> {
        site.image = data.data.toString()
        view?.showSite(site)
      }
      LOCATION_REQUEST -> {
        val location = data.extras?.getParcelable<Location>("location")!!
        site.lat = location.lat
        site.lng = location.lng
        site.zoom = location.zoom
      }
    }
  }
}