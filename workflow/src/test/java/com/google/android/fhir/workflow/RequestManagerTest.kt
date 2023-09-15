package com.google.android.fhir.workflow

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.knowledge.ImplementationGuide
import com.google.android.fhir.knowledge.KnowledgeManager
import com.google.android.fhir.workflow.testing.CqlBuilder
import com.google.android.fhir.workflow.testing.FhirEngineProviderTestRule
import java.io.File
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.util.TimeZone
import kotlin.reflect.KSuspendFunction1
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.MetadataResource
import org.hl7.fhir.r4.model.RequestGroup
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RequestManagerTest {
  @get:Rule val fhirEngineProviderRule = FhirEngineProviderTestRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val knowledgeManager = KnowledgeManager.createInMemory(context)
  private val fhirContext = FhirContext.forR4()
  private val jsonParser = fhirContext.newJsonParser()
  private val xmlParser = fhirContext.newXmlParser()

  private lateinit var fhirEngine: FhirEngine
  private lateinit var fhirOperator: FhirOperator

  @Before
  fun setUp() = runBlockingOnWorkerThread {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    fhirEngine = FhirEngineProvider.getInstance(context)
    fhirOperator = FhirOperator(fhirContext, fhirEngine, knowledgeManager)

    // Installing ANC CDS to the IGManager
    val rootDirectory = File(javaClass.getResource("/anc-cds")!!.file)
    knowledgeManager.install(
      ImplementationGuide(
        "com.google.android.fhir",
        "1.0.0",
        "http://github.com/google/android-fhir"
      ),
      rootDirectory
    )
  }

  @After
  fun tearDown() {
    knowledgeManager.close()
  }

  @Test
  fun testRequestCreationFromCarePlan() = runBlockingOnWorkerThread {
    loadFile("/plan-definition/cql-applicability-condition/patient.json", ::importToFhirEngine)
    loadFile(
      "/plan-definition/cql-applicability-condition/plan_definition_with_requests.json",
      ::installToIgManager
    )
    loadFile("/plan-definition/cql-applicability-condition/example-1.0.0.cql", ::installToIgManager)

    val carePlan =
      fhirOperator.generateCarePlan(
        planDefinitionId = "Plan-Definition-Example",
        patientId = "Patient/Female-Patient-Example"
      ) as CarePlan

    val testRequestHandler = TestRequestHandler()
    val requestManager = RequestManager(fhirEngine, fhirContext, testRequestHandler)
    for (request in carePlan.contained) {
      if (request is RequestGroup) {
        requestManager.createRequestFromRequestGroup(request)
      }
    }

    var requests = requestManager.getAllRequestsForPatient("Female-Patient-Example")
    for (request in requests) {
      println(jsonParser.encodeResourceToString(request))
    }
    assert(!requests.isNullOrEmpty() && requests.size == 2)

    requests = requestManager.getAllRequestsForPatient("Female-Patient-Example", status = "accepted")
    for (request in requests) {
      println(jsonParser.encodeResourceToString(request))
    }
    assert(!requests.isNullOrEmpty() && requests.size == 1)

    requests = requestManager.getAllRequestsForPatient("Female-Patient-Example", intent = "proposal")
    for (request in requests) {
      println(jsonParser.encodeResourceToString(request))
    }
    assert(!requests.isNullOrEmpty() && requests.size == 2)

    requests = requestManager.getAllRequestsForPatient("Female-Patient-Example", status = "draft", intent = "proposal")
    for (request in requests) {
      println(jsonParser.encodeResourceToString(request))
    }
    assert(!requests.isNullOrEmpty() && requests.size == 1)


    val tasks = requestManager.getRequestsForPatient("Female-Patient-Example", ResourceType.Task)
    if (tasks != null) {
      for (task in tasks) {
        println(jsonParser.encodeResourceToString(task))
      }
    }
    assert(!tasks.isNullOrEmpty() && tasks.size == 1)

    val serviceRequests = requestManager.getRequestsForPatient("Female-Patient-Example", ResourceType.ServiceRequest)
    if (serviceRequests != null) {
      for (serviceRequest in serviceRequests) {
        println(jsonParser.encodeResourceToString(serviceRequest))
      }
    }
    assert(!serviceRequests.isNullOrEmpty() && serviceRequests.size == 1)
  }


  private suspend fun loadFile(path: String, importFunction: KSuspendFunction1<Resource, Unit>) {
    val resource =
      if (path.endsWith(suffix = ".xml")) {
        xmlParser.parseResource(open(path)) as Resource
      } else if (path.endsWith(".json")) {
        jsonParser.parseResource(open(path)) as Resource
      } else if (path.endsWith(".cql")) {
        toFhirLibrary(open(path))
      } else {
        throw IllegalArgumentException("Only xml and json and cql files are supported")
      }
    loadResource(resource, importFunction)
  }

  private suspend fun loadResource(
    resource: Resource,
    importFunction: KSuspendFunction1<Resource, Unit>
  ) {
    when (resource.resourceType) {
      ResourceType.Bundle -> loadBundle(resource as Bundle, importFunction)
      else -> importFunction(resource)
    }
  }

  private suspend fun loadBundle(
    bundle: Bundle,
    importFunction: KSuspendFunction1<Resource, Unit>
  ) {
    for (entry in bundle.entry) {
      val resource = entry.resource
      loadResource(resource, importFunction)
    }
  }

  private fun writeToFile(resource: Resource): File {
    val fileName =
      if (resource is MetadataResource && resource.name != null) {
        resource.name
      } else {
        resource.idElement.idPart
      }
    return File(context.filesDir, fileName).apply {
      writeText(jsonParser.encodeResourceToString(resource))
    }
  }

  private fun toFhirLibrary(cql: InputStream): Library {
    return CqlBuilder.compileAndBuild(cql)
  }

  private fun open(path: String) = javaClass.getResourceAsStream(path)!!

  private fun readResourceAsString(path: String) = open(path).readBytes().decodeToString()

  private suspend fun importToFhirEngine(resource: Resource) {
    fhirEngine.create(resource)
  }

  private suspend fun installToIgManager(resource: Resource) {
    knowledgeManager.install(writeToFile(resource))
  }
}