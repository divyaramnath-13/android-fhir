package com.google.android.fhir.demo.external

import java.io.FileInputStream
import java.io.InputStream
import java.util.Date
import java.util.UUID
import java.util.zip.ZipInputStream
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource

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