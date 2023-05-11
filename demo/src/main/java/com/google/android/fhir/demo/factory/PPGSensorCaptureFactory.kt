package com.google.android.fhir.demo.factory

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.external.PPGCaptureUtils
import com.google.android.fhir.demo.external.PPGCaptureUtils.Companion.getPPGDirectory
import com.google.android.material.snackbar.Snackbar
import java.util.UUID
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.w3c.dom.Text

object PPGSensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.sensor_capture_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var capturePpgButton: Button
      private lateinit var textView: TextView
      private lateinit var captureId: String
      // private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        capturePpgButton = itemView.findViewById(R.id.sensor_capture_ppg)
        textView = itemView.findViewById(R.id.sensor_capture_status)
        // context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        captureId = if (questionnaireViewItem.answers.isEmpty())
          UUID.randomUUID().toString()
        else
          questionnaireViewItem.answers.first().valueCoding.code
        displayCapturePpgButton(questionnaireViewItem.questionnaireItem)
        capturePpgButton.setOnClickListener { view ->
          onCapturePpgButtonClicked(
            view,
            questionnaireViewItem
          )
        }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        capturePpgButton.isEnabled = !isReadOnly
      }

      private fun displayCapturePpgButton(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
        capturePpgButton.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
      }

      private fun onCapturePpgButtonClicked(
        view: View,
        questionnaireViewItem: QuestionnaireViewItem
      ) {
        val status = PPGCaptureUtils.capturePPG(captureId)
        if (status) {
          Snackbar.make(view, "PPG captured", Snackbar.LENGTH_SHORT).show()

          val answer = QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = Coding().apply {
              code = captureId
              system = "PPGSensorCaptureFactory"
            }
          }
          questionnaireViewItem.setAnswer(answer)

          val ppgDirectory = getPPGDirectory(captureId)
          textView.text = "PPG captured at: " + ppgDirectory
        }
      }

    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "ppg-capture"

}