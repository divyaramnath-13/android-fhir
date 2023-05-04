package com.google.android.fhir.demo.factory

import android.content.Intent
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.material.snackbar.Snackbar

object PPGSensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var capturePpgButton: Button
      // private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        capturePpgButton = itemView.findViewById(R.id.take_photo)
        // context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        // val questionnaireItem = questionnaireViewItem.questionnaireItem
        displayCapturePpgButton(/*questionnaireItem*/)
        capturePpgButton.setOnClickListener { view -> onCapturePpgButtonClicked(view) }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        capturePpgButton.isEnabled = !isReadOnly
      }

      private fun displayCapturePpgButton(/*questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        capturePpgButton.visibility = View.VISIBLE
        capturePpgButton.setText("Capture PPG Data")
      }

      private fun onCapturePpgButtonClicked(view: View/*, questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        Snackbar.make(view, "Sensing API for PPG capture called!", Snackbar.LENGTH_SHORT).show()
      }

    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "ppg-capture"
}