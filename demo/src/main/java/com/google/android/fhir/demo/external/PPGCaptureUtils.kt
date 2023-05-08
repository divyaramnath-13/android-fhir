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

class PPGCaptureUtils {

  companion object {
    fun capturePPG(): Resource {

      val resourceId = UUID.randomUUID().toString()
      val ppgSnomedCode = Coding().apply {
        system = "http://snomed.info/sct"
        display = "Photoplethysmography"
        code = "72075005"
      }

      val ppgData = Attachment().apply {
        contentType = "video/mp4"
        url = getBlobStoreUrl()
        title = "PPG data collected for 30 seconds"
        creation = Date()
      }

      val data: MutableList<DocumentReferenceContentComponent> = mutableListOf(DocumentReferenceContentComponent(ppgData))
      val documentType = CodeableConcept(ppgSnomedCode)

      val ppgDocumentReference = DocumentReference().apply {
        id = resourceId
        status = Enumerations.DocumentReferenceStatus.CURRENT
        type = documentType
        date = Date()
        description = "Reference to PPG data collected for 30 seconds"
        content = data
      }
      return ppgDocumentReference
    }

    private fun getBlobStoreUrl(): String {
      val uuid = UUID.randomUUID().toString()
      return "http://localhost:9001/ppg-data/$uuid"
    }

  }
}