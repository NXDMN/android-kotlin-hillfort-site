package org.wit.site.views

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.jetbrains.anko.AnkoLogger
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.favourite.FavouriteView
import org.wit.site.views.location.EditLocationView
import org.wit.site.views.login.LoginView
import org.wit.site.views.map.SiteMapView
import org.wit.site.views.navigator.SiteNavigatorView
import org.wit.site.views.settings.SettingsView
import org.wit.site.views.site.SiteView
import org.wit.site.views.sitelist.SiteListView

val IMAGE_REQUEST1 = 1
val IMAGE_REQUEST2 = 2
val IMAGE_REQUEST3 = 3
val IMAGE_REQUEST4 = 4
val LOCATION_REQUEST = 5

enum class VIEW {
  LOCATION, SITE, MAPS, LIST, LOGIN, SETTINGS, FAVOURITE, NAVIGATOR
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

  var basePresenter: BasePresenter? = null

  fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
    var intent = Intent(this, SiteListView::class.java)
    when (view) {
      VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
      VIEW.SITE -> intent = Intent(this, SiteView::class.java)
      VIEW.MAPS -> intent = Intent(this, SiteMapView::class.java)
      VIEW.LIST -> intent = Intent(this, SiteListView::class.java)
      VIEW.LOGIN -> intent = Intent(this, LoginView::class.java)
      VIEW.SETTINGS -> intent = Intent(this, SettingsView::class.java)
      VIEW.FAVOURITE -> intent = Intent(this, FavouriteView::class.java)
      VIEW.NAVIGATOR -> intent = Intent(this, SiteNavigatorView::class.java)
    }
    if (key != "") {
      intent.putExtra(key, value)
    }
    startActivityForResult(intent, code)
  }

  fun initPresenter(presenter: BasePresenter): BasePresenter {
    basePresenter = presenter
    return presenter
  }

  fun init(toolbar: Toolbar, upEnabled: Boolean) {
    toolbar.title = title
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
  }

  override fun onDestroy() {
    basePresenter?.onDestroy()
    super.onDestroy()
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      basePresenter?.doActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  open fun showSite(site: SiteModel) {}
  open fun showSites(sites: List<SiteModel>) {}
  open fun showLocation(location: Location) {}
  open fun showProgress() {}
  open fun hideProgress() {}
}
