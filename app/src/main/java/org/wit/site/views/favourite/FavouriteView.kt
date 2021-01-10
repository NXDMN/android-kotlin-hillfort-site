package org.wit.site.views.favourite

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_site_list.*
import org.wit.site.R
import org.wit.site.models.SiteModel
import org.wit.site.views.BaseView
import org.wit.site.views.sitelist.SiteAdapter
import org.wit.site.views.sitelist.SiteListener

class FavouriteView: BaseView(), SiteListener {

  lateinit var presenter: FavouritePresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site_list)

    super.init(toolbar, true)

    presenter = initPresenter(FavouritePresenter(this)) as FavouritePresenter

    val layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    presenter.loadSites()
  }

  override fun showSites(favouriteSites: List<SiteModel>) {
    recyclerView.adapter = SiteAdapter(favouriteSites, this)
    recyclerView.adapter?.notifyDataSetChanged()
  }

  override fun onSiteClick(site: SiteModel) {
    presenter.doEditSite(site)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    presenter.loadSites()
    super.onActivityResult(requestCode, resultCode, data)
  }
}