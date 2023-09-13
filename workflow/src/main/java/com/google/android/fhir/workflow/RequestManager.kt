package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import java.util.UUID
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.RequestGroup.RequestIntent
import org.hl7.fhir.r4.model.RequestGroup.RequestStatus
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.Task
import org.hl7.fhir.r4.model.Task.TaskIntent
import org.hl7.fhir.r4.model.Task.TaskStatus

class RequestManager(
  private var fhirEngine: FhirEngine,
  private val fhirContext: FhirContext
) {
  private val jsonParser = fhirContext.newJsonParser()

  /** Ensure the Task has a status and intent defined */
  private fun validateTask(task: Task) {
    task.id = UUID.randomUUID().toString()
    if (task.status == null)
      task.status = TaskStatus.DRAFT
    if (task.intent == null)
      task.intent = TaskIntent.PROPOSAL
  }

  /** Creates the request given a RequestGroup or RequestOrchestration? */
  suspend fun createRequestFromRequestGroup(requestGroup: RequestGroup) {
    for (request in requestGroup.contained) {
        when (request.fhirType()) {
          "Task" -> validateTask(request as Task)
          "MedicationRequest" -> TODO("Implement validateMedicationRequest()")
          "ServiceRequest" -> TODO("Implement validateServiceRequest()")
          "CommunicationRequest" -> TODO("Implement validateCommunicationRequest()")
          "ImmunizationRecommendation" -> TODO("Implement validateImmunizationRecommendation()")
          else -> TODO("Not a valid request")
      }
      fhirEngine.create(request)
    }
  }

  /** Updates the request status */
  suspend fun updateStatus(request: Resource, status: RequestStatus) { // should this be RequestStatus mapped to the Request
    when (request.fhirType()) {
      "Task" -> updateTaskStatus(request as Task, RequestUtils.mapRequestStatusToTaskStatus(status))
      "MedicationRequest" -> TODO("Implement updateMedicationRequestStatus()")
      "ServiceRequest" -> TODO("Implement updateServiceRequestStatus()")
      "CommunicationRequest" -> TODO("Implement updateCommunicationRequestStatus()")
      "ImmunizationRecommendation" -> TODO("Implement updateImmunizationRecommendationStatus()")
      else -> TODO("Not a valid request")
    }
  }

  /** Update intent */
  suspend fun updateIntent(request: Resource, intent: RequestIntent) {
    when (request.fhirType()) {
      "Task" -> updateTaskIntent(request as Task, RequestUtils.mapRequestIntentToTaskIntent(intent))
      "MedicationRequest" -> TODO("Implement updateMedicationRequestStatus()")
      "ServiceRequest" -> TODO("Implement updateServiceRequestStatus()")
      "CommunicationRequest" -> TODO("Implement updateCommunicationRequestStatus()")
      "ImmunizationRequest" -> TODO("Implement updateImmunizationRequestStatus()")
      else -> TODO("Not a valid request")
    }
  }

  private fun isValidStatusTransition(currentStatus: RequestStatus?, newStatus: RequestStatus): Boolean {
    if (newStatus == RequestStatus.DRAFT)
      return currentStatus == RequestStatus.NULL

    if (newStatus == RequestStatus.ACTIVE)
      return (currentStatus == RequestStatus.DRAFT || currentStatus == RequestStatus.ONHOLD)

    if (newStatus == RequestStatus.ONHOLD)
      return currentStatus == RequestStatus.ACTIVE

    if (newStatus == RequestStatus.COMPLETED)
      return currentStatus == RequestStatus.ACTIVE

    if (newStatus == RequestStatus.REVOKED)
      return currentStatus == RequestStatus.ACTIVE

    return newStatus == RequestStatus.ENTEREDINERROR
  }

  private suspend fun updateTaskStatus(task: Task, status: TaskStatus) {
    // is transition valid - refer: https://www.hl7.org/fhir/request.html#statemachine
    val currentStatus = task.status
    if (isValidStatusTransition(RequestUtils.mapTaskStatusToRequestStatus(currentStatus), RequestUtils.mapTaskStatusToRequestStatus(status))) {
      task.status = status
      fhirEngine.update(task)
    }
    // else do nothing
  }

  private suspend fun updateTaskIntent(task: Task, intent: TaskIntent) {
    // a new Task has to be created if the intent is updateda
    // a new instance 'basedOn' the prior instance should be created with the new 'intent' value.
    val newTask: Task = Task().apply {
      id = UUID.randomUUID().toString()
      status = TaskStatus.DRAFT
      task.intent = intent
      basedOn.add(Reference(task))
    }
    fhirEngine.create(newTask)
  }
}