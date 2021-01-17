package org.wit.site.views.photo

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import org.wit.site.R
import org.wit.site.views.BaseView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraPhotoView : BaseView() {

  lateinit var presenter: CameraPhotoPresenter
  lateinit var cameraExecutor: ExecutorService
  var imageCapture: ImageCapture? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    presenter = initPresenter(CameraPhotoPresenter(this)) as CameraPhotoPresenter

    camera_capture_button.setOnClickListener { takePhoto() }

    cameraExecutor = Executors.newSingleThreadExecutor()
  }

  private fun takePhoto() {

    val imageCapture = imageCapture ?: return

    val contentValues = ContentValues()
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE")
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
      override fun onError(exc: ImageCaptureException) {
        Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
      }

      override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = output.getSavedUri()
        val msg = "Photo capture succeeded: $savedUri"
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        Log.d("CameraX", msg)
        presenter.doSave(savedUri!!)

      }
    })

  }

  override fun startCamera() {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

    cameraProviderFuture.addListener(Runnable {
      val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

      val preview = Preview.Builder()
          .build()
          .also {
            it.setSurfaceProvider(viewFinder.createSurfaceProvider())
          }

      imageCapture = ImageCapture.Builder()
          .build()

      val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

      try {
        cameraProvider.unbindAll()

        cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture)

      } catch (exc: Exception) {
        Log.e("CameraX", "Use case binding failed", exc)
      }

    }, ContextCompat.getMainExecutor(this))
  }

  override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
  }


}