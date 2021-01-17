package org.wit.site.views.photo

import android.content.Intent
import android.net.Uri
import org.wit.site.helpers.checkCameraPermissions
import org.wit.site.helpers.isCameraPermissionGranted
import org.wit.site.views.BasePresenter
import org.wit.site.views.BaseView

class CameraPhotoPresenter(view: BaseView) : BasePresenter(view) {

  init{
    if(checkCameraPermissions(view)){
      view.startCamera()
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if(isCameraPermissionGranted(requestCode, grantResults)){
      view?.startCamera()
    }
  }

  fun doSave(savedUri: Uri){
    val resultIntent = Intent()
    resultIntent.putExtra("image", savedUri)
    view?.setResult(10, resultIntent)
    view?.finish()
  }
}