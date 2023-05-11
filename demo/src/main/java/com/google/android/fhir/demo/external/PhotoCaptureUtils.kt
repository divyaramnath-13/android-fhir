package com.google.android.fhir.demo.external

import java.util.Date
import java.util.UUID
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource

class PhotoCaptureUtils {

  companion object {
    fun capturePhoto(captureId: String): Boolean {

      // capture photo here and return status
      return true
    }

    fun getPhotoPath(captureId: String): String {

      // logic to fetch photo path using captureId
      return "/storage/emulated/0/DCIM/$captureId/SENSOR_PHOTO/001.jpg"
    }

    private fun getBlobStoreUrl(): String {
      val uuid = UUID.randomUUID().toString()
      return "http://localhost:9001/ppg-data/$uuid"
    }

  }
}