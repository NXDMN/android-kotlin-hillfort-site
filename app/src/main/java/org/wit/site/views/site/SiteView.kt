package org.wit.site.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.site.R
import org.wit.site.helpers.readImageFromPath
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.*


class SiteView : BaseView(), AnkoLogger {

  lateinit var presenter: SitePresenter
  var site = SiteModel()
  lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site)
    super.init(toolbarAdd, true)

    presenter = initPresenter(SitePresenter(this)) as SitePresenter

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      map = it
      presenter.doConfigureMap(map)
      it.setOnMapClickListener { presenter.doSetLocation() }
    }

    visited.setOnCheckedChangeListener() { buttonView, isChecked ->
      if (isChecked) {
        dateVisited.visibility = View.VISIBLE
      }
      else{
        dateVisited.visibility = View.INVISIBLE
        dateVisited.text.clear()
      }
    }

    chooseImage.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)
      presenter.doSelectImage(IMAGE_REQUEST1)
    }

    chooseImage2.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)
      presenter.doSelectImage(IMAGE_REQUEST2)
    }

    chooseImage3.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)
      presenter.doSelectImage(IMAGE_REQUEST3)
    }

    chooseImage4.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)
      presenter.doSelectImage(IMAGE_REQUEST4)
    }

  }

  override fun showSite(site: SiteModel){
    if(siteName.text.isEmpty()) siteName.setText(site.name)
    if(description.text.isEmpty()) description.setText(site.description)
    if(!visited.isChecked) visited.setChecked(site.visited)
    if (visited.isChecked) {
      dateVisited.visibility = View.VISIBLE
    }
    if(dateVisited.text.isEmpty()) dateVisited.setText(site.date)
    if(additionalNotes.text.isEmpty()) additionalNotes.setText(site.notes)
    siteRate.rating = site.rating
    if(!favourite.isChecked) favourite.setChecked(site.favourite)
    siteImage.setImageBitmap(readImageFromPath(this, site.image))
    siteImage2.setImageBitmap(readImageFromPath(this, site.image2))
    siteImage3.setImageBitmap(readImageFromPath(this, site.image3))
    siteImage4.setImageBitmap(readImageFromPath(this, site.image4))

    if (site.image != "") {
      chooseImage.setText(R.string.change_site_image)
    }
    if (site.image2 != "") {
      chooseImage2.setText(R.string.change_site_image)
    }
    if (site.image3 != "") {
      chooseImage3.setText(R.string.change_site_image)
    }
    if (site.image4 != "") {
      chooseImage4.setText(R.string.change_site_image)
    }
    this.showLocation(site.location)
  }

  override fun showLocation(location: Location) {
    lat.setText("%.6f".format(location.lat))
    lng.setText("%.6f".format(location.lng))
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_site, menu)
    if (presenter.edit) menu.getItem(1).setVisible(true)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
      R.id.item_delete -> {
        presenter.doDelete()
      }
      R.id.item_save -> {
        if (siteName.text.toString().isEmpty()) {
          toast(R.string.enter_site_name)
        } else {
          presenter.doAddOrSave(siteName.text.toString(),
              description.text.toString(),
              visited.isChecked,
              dateVisited.text.toString(),
              additionalNotes.text.toString(),
              siteRate.rating,
              favourite.isChecked)
        }
      }
      R.id.item_share -> {
        presenter.doShare()
      }
      R.id.item_cancel -> {
        finish()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      presenter.doActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onBackPressed() {
    presenter.doCancel()
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
    presenter.doResartLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}