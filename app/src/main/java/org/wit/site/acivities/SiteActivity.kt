package org.wit.site.acivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
  var location = Location(52.245696, -7.139102, 15f)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site)
    toolbarAdd.title = title
    setSupportActionBar(toolbarAdd)
    info("Placemark Activity started..")

    app = application as MainApp

    var edit = false

    if (intent.hasExtra("site_edit")) {
      edit = true
      site = intent.extras?.getParcelable<SiteModel>("site_edit")!!
      siteName.setText(site.name)
      description.setText(site.description)
      siteImage.setImageBitmap(readImageFromPath(this, site.image))
      if (site.image != null) {
        chooseImage.setText(R.string.change_site_image)
      }
      btnAdd.setText(R.string.save_site)
    }

    btnAdd.setOnClickListener() {
      site.name = siteName.text.toString()
      site.description = description.text.toString()
      if (site.name.isEmpty()) {
        toast (R.string.enter_site_name)
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
      startActivityForResult(intentFor<MapActivity>().putExtra("location", location), LOCATION_REQUEST)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_site, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
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
          location = data.extras?.getParcelable<Location>("location")!!
        }
      }
    }
  }
}