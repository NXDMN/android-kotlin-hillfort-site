package org.wit.site.acivities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.wit.site.R
import org.wit.site.helpers.readImage
import org.wit.site.helpers.readImageFromPath
import org.wit.site.helpers.showImagePicker
import org.wit.site.main.MainApp
import org.wit.site.models.Location
import org.wit.site.models.SiteModel

class SiteActivity : AppCompatActivity(), AnkoLogger {

  var site = SiteModel()
  lateinit var app: MainApp
  val IMAGE_REQUEST = 1
  val LOCATION_REQUEST = 2
  var edit = false
//  var location = Location(52.245696, -7.139102, 15f)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site)
    toolbarAdd.title = title
    setSupportActionBar(toolbarAdd)
    info("Site Activity started..")

    app = application as MainApp

    if (intent.hasExtra("site_edit")) {
      edit = true
      site = intent.extras?.getParcelable<SiteModel>("site_edit")!!
      siteName.setText(site.name)
      description.setText(site.description)
      visited.setChecked(site.visited)
      if (visited.isChecked) {
        dateVisited.setVisibility(View.VISIBLE)
      }
      dateVisited.setText(site.date)
      additionalNotes.setText(site.notes)
      siteImage.setImageBitmap(readImageFromPath(this, site.image))
      if (site.image != null) {
        chooseImage.setText(R.string.change_site_image)
      }
      btnAdd.setText(R.string.save_site)
    }

    visited.setOnCheckedChangeListener() { buttonView, isChecked ->
      if (isChecked) {
        dateVisited.setVisibility(View.VISIBLE)
      }
      else{
        dateVisited.setVisibility(View.INVISIBLE)
        dateVisited.text.clear()
      }
    }

    btnAdd.setOnClickListener() {
      site.name = siteName.text.toString()
      site.description = description.text.toString()
      site.visited = visited.isChecked
      site.date = dateVisited.text.toString()
      site.notes = additionalNotes.text.toString()
      if (site.name.isEmpty()) {
        toast(R.string.enter_site_name)
      } else {
        if (edit) {
          app.sites.update(site.copy())
        } else {
          app.sites.create(site.copy())
        }
      }
      info("add Button Pressed: $siteName")
      setResult(AppCompatActivity.RESULT_OK)
      finish()
    }

    chooseImage.setOnClickListener {
      showImagePicker(this, IMAGE_REQUEST)
    }

    siteLocation.setOnClickListener {
      val location = Location(52.245696, -7.139102, 15f)
      if(site.zoom != 0f){
        location.lat = site.lat
        location.lng = site.lng
        location.zoom = site.zoom
      }
      startActivityForResult(intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_site, menu)
    if (edit && menu != null) menu.getItem(0).setVisible(true)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
      R.id.item_delete -> {
        app.sites.delete(site)
        finish()
      }
      R.id.item_cancel -> {
        finish()
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      IMAGE_REQUEST -> {
        if (data != null) {
          site.image = data.getData().toString()
          siteImage.setImageBitmap(readImage(this, resultCode, data))
          chooseImage.setText(R.string.change_site_image)
        }
      }
      LOCATION_REQUEST -> {
        if (data != null) {
          val location = data.extras?.getParcelable<Location>("location")!!
          site.lat = location.lat
          site.lng = location.lng
          site.zoom = location.zoom
        }
      }
    }
  }
}