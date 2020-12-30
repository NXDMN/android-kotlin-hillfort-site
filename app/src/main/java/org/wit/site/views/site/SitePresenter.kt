package org.wit.site.views.site

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.site.helpers.checkLocationPermissions
import org.wit.site.helpers.createDefaultLocationRequest
import org.wit.site.helpers.isPermissionGranted
import org.wit.site.helpers.showImagePicker
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.*

class SitePresenter(view: BaseView) : BasePresenter(view) {

  var site = SiteModel()
  var defaultLocation = Location(52.245696, -7.139102, 15f)
  var edit = false;
  var map: GoogleMap? = null
  var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
  val locationRequest = createDefaultLocationRequest()

  init{
    if (view.intent.hasExtra("site_edit")) {
      edit = true
      site = view.intent.extras?.getParcelable<SiteModel>("site_edit")!!
      view.showSite(site)
    }else{
      if (checkLocationPermissions(view)) {
        doSetCurrentLocation()
      }
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

  fun cacheSite (name: String, description: String, visited: Boolean, date: String, notes: String) {
    site.name = name;
    site.description = description
    site.visited = visited
    site.date = date
    site.notes = notes
  }

  fun doConfigureMap(m: GoogleMap) {
    map = m
    locationUpdate(site.lat, site.lng)
  }

  fun locationUpdate(lat: Double, lng: Double) {
    site.lat = lat
    site.lng = lng
    site.zoom = 15f
    map?.clear()
    map?.uiSettings?.setZoomControlsEnabled(true)
    val options = MarkerOptions().title(site.name).position(LatLng(site.lat, site.lng))
    map?.addMarker(options)
    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(site.lat, site.lng), site.zoom))
    view?.showSite(site)
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
    view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(site.lat, site.lng, site.zoom))
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      locationUpdate(it.latitude, it.longitude)
    }
  }

  @SuppressLint("MissingPermission")
  fun doResartLocationUpdates() {
    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val l = locationResult.locations.last()
          locationUpdate(l.latitude, l.longitude)
        }
      }
    }
    if (!edit) {
      locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }
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
        locationUpdate(site.lat, site.lng)
      }
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    } else {
      locationUpdate(defaultLocation.lat, defaultLocation.lng)
    }
  }
}