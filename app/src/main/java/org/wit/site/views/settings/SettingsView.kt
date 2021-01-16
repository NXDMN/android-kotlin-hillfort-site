package org.wit.site.views.settings

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_site_list.*
import org.wit.site.R
import org.wit.site.views.BaseView


class SettingsView : BaseView() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    super.init(toolbar, true)

    supportFragmentManager
      .beginTransaction()
      .replace(R.id.content_preferences, SettingsFragment())
      .commit()

  }


}