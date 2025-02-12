/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.db

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.impl.dao.ForwardIncludeSearchResult
import com.google.android.fhir.db.impl.dao.ReverseIncludeSearchResult
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.search.SearchQuery
import java.time.Instant
import java.util.UUID
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** The interface for the FHIR resource database. */
internal interface Database {
  /**
   * Inserts a list of local `resources` into the FHIR resource database. If any of the resources
   * already exists, it will be overwritten.
   *
   * @param <R> The resource type
   * @return the logical IDs of the newly created resources.
   */
  suspend fun <R : Resource> insert(vararg resource: R): List<String>

  /**
   * Inserts a list of remote `resources` into the FHIR resource database. If any of the resources
   * already exists, it will be overwritten.
   *
   * @param <R> The resource type
   */
  suspend fun <R : Resource> insertRemote(vararg resource: R)

  /**
   * Updates the `resource` in the FHIR resource database. If the resource does not already exist,
   * then it will not be created.
   *
   * @param <R> The resource type
   */
  suspend fun update(vararg resources: Resource)

  /** Updates the `resource` meta in the FHIR resource database. */
  suspend fun updateVersionIdAndLastUpdated(
    resourceId: String,
    resourceType: ResourceType,
    versionId: String,
    lastUpdated: Instant,
  )

  /**
   * Selects the FHIR resource of type `clazz` with `id`.
   *
   * @param <R> The resource type
   * @throws ResourceNotFoundException if the resource is not found in the database
   */
  @Throws(ResourceNotFoundException::class)
  suspend fun select(type: ResourceType, id: String): Resource

  /**
   * Selects the saved `ResourceEntity` of type `clazz` with `id`.
   *
   * @param <R> The resource type
   * @throws ResourceNotFoundException if the resource is not found in the database
   */
  @Throws(ResourceNotFoundException::class)
  suspend fun selectEntity(type: ResourceType, id: String): ResourceEntity

  /**
   * Insert resources that were synchronised.
   *
   * @param syncedResources The synced resource
   */
  suspend fun insertSyncedResources(resources: List<Resource>)

  /**
   * Deletes the FHIR resource of type `clazz` with `id`.
   *
   * @param <R> The resource type
   */
  suspend fun delete(type: ResourceType, id: String)

  suspend fun <R : Resource> search(query: SearchQuery): List<ResourceWithUUID<R>>

  suspend fun searchForwardReferencedResources(query: SearchQuery): List<ForwardIncludeSearchResult>

  suspend fun searchReverseReferencedResources(query: SearchQuery): List<ReverseIncludeSearchResult>

  suspend fun count(query: SearchQuery): Long

  /**
   * Retrieves all [LocalChange]s for all [Resource]s, which can be used to update the remote FHIR
   * server.
   */
  suspend fun getAllLocalChanges(): List<LocalChange>

  /**
   * Retrieves all [LocalChange]s for the [Resource] which has the [LocalChange] with the oldest
   * [LocalChange.timestamp]
   */
  suspend fun getAllChangesForEarliestChangedResource(): List<LocalChange>

  /** Retrieves the count of [LocalChange]s stored in the database. */
  suspend fun getLocalChangesCount(): Int

  /** Remove the [LocalChangeEntity] s with given ids. Call this after a successful sync. */
  suspend fun deleteUpdates(token: LocalChangeToken)

  /** Remove the [LocalChangeEntity] s with matching resource ids. */
  suspend fun deleteUpdates(resources: List<Resource>)

  /**
   * Updates the [ResourceEntity.serializedResource] and [ResourceEntity.resourceId] corresponding
   * to the updatedResource. Updates all the [LocalChangeEntity] for this updated resource as well
   * as all the [LocalChangeEntity] referring to this resource in their [LocalChangeEntity.payload]
   * Updates the [ResourceEntity.serializedResource] for all the resources which refer to this
   * updated resource.
   */
  suspend fun updateResourceAndReferences(
    currentResourceId: String,
    updatedResource: Resource,
  )

  /** Runs the block as a database transaction. */
  suspend fun withTransaction(block: suspend () -> Unit)

  /** Closes the database connection. */
  fun close()

  /**
   * Clears all database tables without resetting the auto-increment value generated by
   * PrimaryKey.autoGenerate. WARNING: This will clear the database and it's not recoverable.
   */
  suspend fun clearDatabase()

  /**
   * Retrieve a list of [LocalChange] for [Resource] with given type and id, which can be used to
   * purge resource from database. If there is no local change for given [resourceType] and
   * [Resource.id], return an empty list.
   *
   * @param type The [ResourceType]
   * @param id The resource id [Resource.id]
   * @return [List]<[LocalChange]> A list of local changes for given [resourceType] and
   *   [Resource.id] . If there is no local change for given [resourceType] and [Resource.id],
   *   return empty list.
   */
  suspend fun getLocalChanges(type: ResourceType, id: String): List<LocalChange>

  /**
   * Retrieve a list of [LocalChange] for [ResourceEntity] with given UUID, which can be used to
   * purge resource from database. If there is no local change for [ResourceEntity.resourceUuid],
   * return an empty list.
   *
   * @param resourceUuid The resource UUID [ResourceEntity.resourceUuid]
   * @return [List]<[LocalChange]> A list of local changes for given [resourceType] and
   *   [Resource.id] . If there is no local change for given [resourceType] and
   *   [ResourceEntity.resourceUuid], return empty list.
   */
  suspend fun getLocalChanges(resourceUuid: UUID): List<LocalChange>

  /**
   * Purge resource from database based on resource type and id without any deletion of data from
   * the server.
   *
   * @param type The [ResourceType]
   * @param id The resource id [Resource.id]
   * @param isLocalPurge default value is false here resource will not be deleted from
   *   LocalChangeEntity table but it will throw IllegalStateException("Resource has local changes
   *   either sync with server or FORCE_PURGE required") if local change exists. If true this API
   *   will delete resource entry from LocalChangeEntity table.
   */
  suspend fun purge(type: ResourceType, id: String, forcePurge: Boolean = false)
}

data class ResourceWithUUID<R>(
  val uuid: UUID,
  val resource: R,
)
