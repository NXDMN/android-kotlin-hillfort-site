package org.wit.site.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.site.R
import org.wit.site.helpers.readImageFromPath
import org.wit.site.models.SiteModel
import org.wit.site.views.BaseView

class SiteView : BaseView(), AnkoLogger {

  lateinit var presenter: SitePresenter
  var site = SiteModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site)

    init(toolbarAdd)

    presenter = initPresenter(SitePresenter(this)) as SitePresenter

    visited.setOnCheckedChangeListener() { buttonView, isChecked ->
      if (isChecked) {
        dateVisited.setVisibility(View.VISIBLE)
      }
      else{
        dateVisited.setVisibility(View.INVISIBLE)
        dateVisited.text.clear()
      }
    }

    chooseImage.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
                          description.text.toString(),
                          visited.isChecked,
                          dateVisited.text.toString(),
                          additionalNotes.text.toString())
      presenter.doSelectImage()
    }

    siteLocation.setOnClickListener {
      presenter.cacheSite(siteName.text.toString(),
                          description.text.toString(),
                          visited.isChecked,
                          dateVisited.text.toString(),
                          additionalNotes.text.toString())
      presenter.doSetLocation()
    }
  }

  override fun showSite(site: SiteModel){
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
                                additionalNotes.text.toString())
        }
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
}