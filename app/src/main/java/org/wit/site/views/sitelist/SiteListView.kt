package org.wit.site.views.sitelist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_site_list.*
import org.wit.site.R
import org.wit.site.models.SiteModel
import org.wit.site.views.BaseView

class SiteListView: BaseView(), SiteListener {

  lateinit var presenter: SiteListPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_site_list)

    super.init(toolbar, false)

    presenter = initPresenter(SiteListPresenter(this)) as SiteListPresenter

    val layoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = layoutManager
    presenter.loadSites()
  }

  override fun showSites(sites: List<SiteModel>) {
    recyclerView.adapter = SiteAdapter(sites, this)
    recyclerView.adapter?.notifyDataSetChanged()
  }

  fun checkChoice(): String?{
    var choice: String? = null
    val selectedId = radioGrp.checkedRadioButtonId
    when(selectedId){
      R.id.radioName -> choice = "name"
      R.id.radioDesc -> choice = "desc"
      R.id.radioNote -> choice = "note"
    }
    return choice
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)

    val searchView = menu?.findItem(R.id.item_search)?.actionView as SearchView
    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String): Boolean {
        if(!searchView.isIconified()) {
          searchView.setIconified(true)
        }
        menu.findItem(R.id.item_search).collapseActionView()
        presenter.searchSites(checkChoice(), query)
        return false
      }

      override fun onQueryTextChange(newText: String): Boolean {
        presenter.searchSites(checkChoice(), newText)
        return false
      }
    })


    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item?.itemId) {
      R.id.item_add -> presenter.doAddSite()
      R.id.item_map -> presenter.doShowSitesMap()
      R.id.item_logout -> presenter.doLogout()
      R.id.item_settings -> presenter.doSettings()
      R.id.item_favourite -> presenter.doFavourite()
      R.id.item_navigator -> presenter.doNavigator()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onSiteClick(site: SiteModel) {
    presenter.doEditSite(site)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    presenter.loadSites()
    super.onActivityResult(requestCode, resultCode, data)
  }
}

