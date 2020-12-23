package org.wit.site.acivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_site_maps.*
import org.wit.site.R
import org.wit.site.main.MainApp

class SiteMapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener {

  lateinit var map: GoogleMap
  lateinit var app: MainApp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site_maps)
    toolbar.title = title
    setSupportActionBar(toolbar)
    mapView.onCreate(savedInstanceState)
    app = application as MainApp

    mapView.getMapAsync {
      map = it
      configureMap()
    }
  }

  fun configureMap() {
    map.uiSettings.setZoomControlsEnabled(true)
    app.sites.findAll().forEach{
      val loc = LatLng(it.lat, it.lng)
      val options = MarkerOptions().title(it.name).position(loc)
      map.addMarker(options).tag = it.id
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
    }
    map.setOnMarkerClickListener(this)
  }

  override fun onMarkerClick(marker: Marker): Boolean {
    currentName.text = marker.title
    return false
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}