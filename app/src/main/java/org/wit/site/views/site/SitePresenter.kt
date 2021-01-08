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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
    doAsync {
      if (edit) {
        app.sites.update(site)
      } else {
        app.sites.create(site)
      }
      uiThread {
        view?.finish()
      }
    }
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
    locationUpdate(site.location)
  }

  fun locationUpdate(location: Location) {
    site.location = location
    site.location.zoom = 15f
    map?.clear()
    val options = MarkerOptions().title(site.name).position(LatLng(site.location.lat, site.location.lng))
    map?.addMarker(options)
    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(site.location.lat, site.location.lng), site.location.zoom))
    view?.showLocation(site.location)
  }

  fun doCancel() {
    view?.finish()
  }

  fun doDelete() {
    doAsync {
      app.sites.delete(site)
      uiThread {
        view?.finish()
      }
    }
  }

  fun doSelectImage(code: Int) {
    view?.let {
      showImagePicker(view!!, code)
    }
  }

  fun doSetLocation() {
    view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(site.location.lat, site.location.lng, site.location.zoom))
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      locationUpdate(Location(it.latitude, it.longitude))
    }
  }

  @SuppressLint("MissingPermission")
  fun doResartLocationUpdates() {
    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val l = locationResult.locations.last()
          locationUpdate(Location(l.latitude, l.longitude))
        }
      }
    }
    if (!edit) {
      locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    } else {
      locationUpdate(defaultLocation)
    }
  }

  override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    when (requestCode) {
      IMAGE_REQUEST1 -> {
        site.image = data.data.toString()
        view?.showSite(site)
      }
      IMAGE_REQUEST2 -> {
        site.image2 = data.data.toString()
        view?.showSite(site)
      }
      IMAGE_REQUEST3 -> {
        site.image3 = data.data.toString()
        view?.showSite(site)
      }
      IMAGE_REQUEST4 -> {
        site.image4 = data.data.toString()
        view?.showSite(site)
      }
      LOCATION_REQUEST -> {
        val location = data.extras?.getParcelable<Location>("location")!!
        site.location = location
        locationUpdate(location)
      }
    }
  }


}