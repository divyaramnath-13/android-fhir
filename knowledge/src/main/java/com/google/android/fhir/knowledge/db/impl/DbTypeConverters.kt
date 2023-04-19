/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

<<<<<<<< HEAD:knowledge/src/main/java/com/google/android/fhir/knowledge/db/impl/DbTypeConverters.kt
package com.google.android.fhir.knowledge.db.impl

import androidx.room.TypeConverter
import java.io.File

internal object DbTypeConverters {

  @JvmStatic @TypeConverter fun filePathToFile(filePath: String) = File(filePath)

  @JvmStatic @TypeConverter fun fileToFilePath(file: File): String = file.path
========
package com.google.android.fhir.workflow

import org.hl7.fhir.r4.model.CarePlan

interface RequestResourceManager<T> {

  suspend fun createRequestResource(resource: T): T

  suspend fun updateRequestResourceStatus(resource: T, status: String)

  fun mapRequestResourceStatusToCarePlanStatus(resource: T): CarePlan.CarePlanActivityStatus

  suspend fun linkCarePlanToRequestResource(resource: T, carePlan: CarePlan)

  suspend fun assignOwner(resource: T, ownerId: String)
>>>>>>>> 999dcfaf (Configurable care - workflow APIs (#1962)):workflow/src/main/java/com/google/android/fhir/workflow/RequestResourceManager.kt
}
