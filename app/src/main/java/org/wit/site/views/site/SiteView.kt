package org.wit.site.views.site

import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.site.R
import org.wit.site.models.Location
import org.wit.site.models.SiteModel
import org.wit.site.views.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


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

      openDialog(CAMERA_REQUEST1, IMAGE_REQUEST1)
    }

    chooseImage2.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)

      openDialog(CAMERA_REQUEST2, IMAGE_REQUEST2)
    }

    chooseImage3.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)

      openDialog(CAMERA_REQUEST3, IMAGE_REQUEST3)
    }

    chooseImage4.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
          description.text.toString(),
          visited.isChecked,
          dateVisited.text.toString(),
          additionalNotes.text.toString(),
          siteRate.rating,
          favourite.isChecked)

      openDialog(CAMERA_REQUEST4, IMAGE_REQUEST4)
    }

  }

  fun openDialog(camera: Int, image: Int){
    val items = arrayOf("Camera", "Gallery", "Cancel")
    val builder = AlertDialog.Builder(this)
    with(builder){
      setTitle("Add from")
      setItems(items){ dialog, which ->
        when(which) {
          0 -> presenter.doImageFromCamera(camera)
          1 -> presenter.doSelectImage(image)
          2 -> dialog.dismiss()
        }
      }
      show()
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
    Glide.with(this).load(site.image).into(siteImage)
    Glide.with(this).load(site.image2).into(siteImage2)
    Glide.with(this).load(site.image3).into(siteImage3)
    Glide.with(this).load(site.image4).into(siteImage4)

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
    presenter.doRestartLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}