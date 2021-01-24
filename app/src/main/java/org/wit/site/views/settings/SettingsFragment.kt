package org.wit.site.views.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import org.wit.site.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener{

  lateinit var presenter: SettingsPresenter

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.preferences_main, rootKey)

    presenter = SettingsPresenter(this)

    initSummary(preferenceScreen);
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
    updatePrefSummary(findPreference(key))
  }

  override fun onResume() {
    super.onResume()
    preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
  }

  override fun onPause() {
    super.onPause()
    preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
  }

  fun initSummary(p: Preference){
    if (p is PreferenceGroup) {
      val pGrp: PreferenceGroup = p
      for (i in 0 until pGrp.getPreferenceCount()) {
        initSummary(pGrp.getPreference(i))
      }
    } else {
      updatePrefSummary(p)
    }
  }

  fun updatePrefSummary(p: Preference?){
    when(p?.key) {
      "email" -> presenter.doGetEmail {summary -> p.setSummary(summary)}
      "password" -> p.setSummary("*******")
      "totalSites" -> presenter.doGetTotalSites{summary -> p.setSummary(summary)}
      "visitedSites" -> presenter.doGetVisitedSites { summary -> p.setSummary(summary) }
    }

  }
}