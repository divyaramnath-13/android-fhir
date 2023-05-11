package com.google.android.fhir.demo.factory

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.demo.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.demo.external.PPGCaptureUtils
import com.google.android.fhir.demo.external.PhotoCaptureUtils
import com.google.android.material.snackbar.Snackbar
import java.util.UUID
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource

object PhotoSensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.sensor_capture_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var capturePhotoButton: Button
      private lateinit var textView: TextView
      private lateinit var captureId: String
      // private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        capturePhotoButton = itemView.findViewById(R.id.sensor_capture_photo)
        textView = itemView.findViewById(R.id.photo_capture_status)
        // context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        captureId = if (questionnaireViewItem.answers.isEmpty())
          UUID.randomUUID().toString()
        else
          questionnaireViewItem.answers.first().valueCoding.code
        displayTakePhotoButton(/*questionnaireItem*/)
        capturePhotoButton.setOnClickListener { view -> onTakePhotoButtonClicked(view) }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        capturePhotoButton.isEnabled = !isReadOnly
      }

      private fun displayTakePhotoButton(/*questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        capturePhotoButton.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
      }

      private fun onTakePhotoButtonClicked(view: View/*, questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        val status = PhotoCaptureUtils.capturePhoto(captureId)
        if (status) {
          Snackbar.make(view, "PPG captured", Snackbar.LENGTH_SHORT).show()
          val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = Coding().apply {
              code = captureId
              system = "PhotoSensorCaptureFactory"
            }
          }
          questionnaireViewItem.setAnswer(answer)

          val photoFilePath = PhotoCaptureUtils.getPhotoPath(captureId)
          textView.text = "Photo captured at: " + photoFilePath
        }
      }
    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "photo-capture"
}