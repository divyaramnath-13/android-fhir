package com.google.android.fhir.demo.factory

import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.tryUnwrapContext
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.material.snackbar.Snackbar

object SensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.attachment_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var callApiButton: Button
      private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        callApiButton = itemView.findViewById(R.id.take_photo)
        context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        // header.bind(questionnaireViewItem)
        val questionnaireItem = questionnaireViewItem.questionnaireItem
        callApiButton.setOnClickListener { view -> onCallApiButtonClicked(view) }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        callApiButton.isEnabled = !isReadOnly
      }

      private fun onCallApiButtonClicked(view: View/*, questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        Snackbar.make(view, "Sensing API called!", Snackbar.LENGTH_SHORT).show()
      }
    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "sensor-capture"
}