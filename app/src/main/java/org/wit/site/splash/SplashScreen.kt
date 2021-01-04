package org.wit.site.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import org.wit.site.views.login.LoginView

class SplashScreen : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    startActivity<LoginView>()
    finish()
  }
}