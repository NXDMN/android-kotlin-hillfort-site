package org.wit.site.views.navigator

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_map.toolbar
import kotlinx.android.synthetic.main.activity_navigator.*
import org.wit.site.R
import org.wit.site.models.Location
import org.wit.site.views.BaseView

class SiteNavigatorView : BaseView(), GoogleMap.OnMarkerClickListener {

  lateinit var presenter: SiteNavigatorPresenter
  lateinit var map : GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_navigator)
    super.init(toolbar, true)

    presenter = initPresenter(SiteNavigatorPresenter(this)) as SiteNavigatorPresenter

    navMapView.onCreate(savedInstanceState)
    navMapView.getMapAsync{
      map = it
      map.setOnMarkerClickListener(this)
      presenter.doConfigureMap(map)
    }
  }

  override fun showLocation(location: Location) {
    currentLat.setText("%.6f".format(location.lat))
    currentLng.setText("%.6f".format(location.lng))
  }

  override fun onMarkerClick(marker: Marker): Boolean {
    presenter.doMarkerSelected(marker)
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    navMapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    navMapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    navMapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    navMapView.onResume()
    presenter.doRestartLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    navMapView.onSaveInstanceState(outState)
  }
}