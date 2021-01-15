package org.wit.site.views.navigator

import android.annotation.SuppressLint
import android.graphics.Color
import com.beust.klaxon.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.wit.site.helpers.*
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView
import java.net.URL

class SiteNavigatorPresenter(view: BaseView) : BasePresenter(view) {

  var defaultLocation = Location(52.245696, -7.139102, 15f)
  var map: GoogleMap? = null
  var currentLocationMarker: Marker? = null
  var currentLocation: LatLng? = null
  var destLocation: LatLng? = null
  var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
  val locationRequest = createDefaultLocationRequest()
  val latLongB = LatLngBounds.Builder()
  var directions: Polyline? = null
  var centerCurrent: Boolean = true

  init {
    if (checkLocationPermissions(view)) {
      doSetCurrentLocation()
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    } else {
      addCurrentLocation(defaultLocation)
    }
  }

  fun doConfigureMap(m: GoogleMap) {
    map = m

    doAsync {
      val sites = app.sites.findAll()
      uiThread {
        addSitesMarkers(sites)
      }
    }
  }

  fun addSitesMarkers(sites: List<SiteModel>){
    map?.uiSettings?.setZoomControlsEnabled(true)
    sites.forEach{
      val loc = LatLng(it.location.lat, it.location.lng)
      val options = MarkerOptions().title(it.name).position(loc)
      map?.addMarker(options)?.tag = it
    }
  }

  fun doMarkerSelected(marker: Marker){
    if(marker != currentLocationMarker) {
      val site = marker.tag as SiteModel
      destLocation = LatLng(site.location.lat, site.location.lng)

      if (currentLocation != null) {
        getDirection()
      } else {
        view?.toast("Cannot get current location")
      }
    }else{
      view?.toast("Cannot select current location marker")
    }
  }

  fun addCurrentLocation(loc: Location) {
    currentLocation = LatLng(loc.lat, loc.lng)
    val options = MarkerOptions()
                  .title("Current")
                  .position(LatLng(loc.lat, loc.lng))
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
    currentLocationMarker?.remove()
    currentLocationMarker = map?.addMarker(options)
    if(centerCurrent)
      map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.lat, loc.lng), 15f))
    view?.showLocation(loc)
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      addCurrentLocation(Location(it.latitude, it.longitude))
    }
  }

  @SuppressLint("MissingPermission")
  fun doRestartLocationUpdates() {
    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val l = locationResult.locations.last()
          addCurrentLocation(Location(l.latitude, l.longitude))
        }
      }
    }
    locationService.requestLocationUpdates(locationRequest, locationCallback, null)
  }

  fun getDirection(){
    centerCurrent = false
    val options = PolylineOptions()
    options.color(Color.BLUE)
    options.width(13f)
    val url = getDirectionsUrl(currentLocation, destLocation)

    doAsync {
      val result = URL(url).readText()
      uiThread {
        val parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(result)
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
        val routes = json.array<JsonObject>("routes")!!
        val legs = routes[0]["legs"] as JsonArray<JsonObject>
        val points = legs[0]["steps"] as JsonArray<JsonObject>
        val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
        options.add(currentLocation)
        latLongB.include(currentLocation)
        for (point in polypts) {
          options.add(point)
          latLongB.include(point)
        }
        options.add(destLocation)
        latLongB.include(destLocation)

        val bounds = latLongB.build()
        directions?.remove()
        directions = map?.addPolyline(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
      }
    }
  }
}

