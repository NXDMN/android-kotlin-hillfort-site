package org.wit.site.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.site.models.SiteModel
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView

class SiteMapPresenter(view: BaseView) : BasePresenter(view) {

  fun doPopulateMap(map: GoogleMap, sites: List<SiteModel>){
    map.uiSettings.setZoomControlsEnabled(true)
    sites.forEach{
      val loc = LatLng(it.location.lat, it.location.lng)
      val options = MarkerOptions().title(it.name).position(loc)
      map.addMarker(options).tag = it
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))
    }
  }

  fun doMarkerSelected(marker: Marker){
    val site = marker.tag as SiteModel
    doAsync {
      uiThread {
        if (site != null) view?.showSite(site)
      }
    }
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