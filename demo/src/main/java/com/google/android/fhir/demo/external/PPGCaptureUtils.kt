package com.google.android.fhir.demo.external

class PPGCaptureUtils {

  companion object {
    fun capturePPG(captureId: String): Boolean {

      // capture PPG here and return status
      return true
    }

    fun getPPGDirectory(captureId: String): String {
      // val filePathList: MutableList<String> = mutableListOf()

      // logic to fetch PPG directory using captureId
      // filePathList.add("/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/frame_001.jpg")
      // filePathList.add("/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/frame_002.jpg")
      // filePathList.add("/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/frame_003.jpg")
      // filePathList.add("/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/frame_004.jpg")
      // filePathList.add("/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/frame_005.jpg")

      return "/storage/emulated/0/DCIM/$captureId/SENSOR_PPG/"
    }

  }
}