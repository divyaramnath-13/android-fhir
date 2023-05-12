package com.google.android.fhir.demo.external

class FingernailsPhotoCaptureUtils {

  companion object {
    fun capturePhoto(captureId: String): Boolean {

      // capture photo here and return status
      return true
    }

    fun getPhotoPath(captureId: String): String {

      // logic to fetch photo path using captureId
      return "/storage/emulated/0/DCIM/$captureId/SENSOR_PHOTO/001.jpg"
    }

  }
}