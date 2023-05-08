package com.google.android.fhir.demo.factory

import android.view.View
import android.widget.Button
import android.widget.TextView
// import com.google.android.fhir.demo.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.factories.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.demo.R
import com.google.android.fhir.demo.external.PPGCaptureUtils
import com.google.android.material.snackbar.Snackbar
import org.hl7.fhir.r4.model.DocumentReference
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.w3c.dom.Text

object PPGSensorCaptureFactory :
  QuestionnaireItemViewHolderFactory(R.layout.sensor_capture_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      override lateinit var questionnaireViewItem: QuestionnaireViewItem
      private lateinit var capturePpgButton: Button
      private lateinit var textView: TextView
      // private lateinit var context: AppCompatActivity

      override fun init(itemView: View) {
        capturePpgButton = itemView.findViewById(R.id.sensor_capture_ppg)
        textView = itemView.findViewById(R.id.sensor_capture_status)
        // context = itemView.context.tryUnwrapContext()!!
      }


      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        displayCapturePpgButton(/*questionnaireItem*/)
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

      private fun displayCapturePpgButton(/*questionnaireItem: Questionnaire.QuestionnaireItemComponent*/) {
        capturePpgButton.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
        textView.text = "Not captured yet"
      }

      private fun onCapturePpgButtonClicked(
        view: View,
        questionnaireViewItem: QuestionnaireViewItem
      ) {
        val resource: Resource = PPGCaptureUtils.capturePPG()

        if (resource is DocumentReference) {
          Snackbar.make(view, "PPG captured", Snackbar.LENGTH_SHORT).show()
          textView.text = "Capture ID: " + resource.id
        } else {
          Snackbar.make(view, "Could not capture PPG data", Snackbar.LENGTH_SHORT).show()
        }

      }

    }
  const val WIDGET_EXTENSION = "http://external-api-call/sensing-backbone"
  const val WIDGET_TYPE = "ppg-capture"

}