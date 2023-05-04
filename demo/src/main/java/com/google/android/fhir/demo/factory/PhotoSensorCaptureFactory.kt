package com.google.android.fhir.demo.factory

import android.view.View
import android.widget.Button
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.material.snackbar.Snackbar

object PhotoSensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var capturePhotoButton: Button
      // private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        capturePhotoButton = itemView.findViewById(R.id.take_photo)
        // context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        // val questionnaireItem = questionnaireViewItem.questionnaireItem
        displayTakePhotoButton(/*questionnaireItem*/)
        capturePhotoButton.setOnClickListener { view -> onTakePhotoButtonClicked(view) }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        capturePhotoButton.isEnabled = !isReadOnly
      }

      private fun displayTakePhotoButton(/*questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        capturePhotoButton.visibility = View.VISIBLE
        capturePhotoButton.setText("Capture Photo")
      }

      private fun onTakePhotoButtonClicked(view: View/*, questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        Snackbar.make(view, "Sensing API for photo capture called!", Snackbar.LENGTH_SHORT).show()
      }
    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "photo-capture"
}