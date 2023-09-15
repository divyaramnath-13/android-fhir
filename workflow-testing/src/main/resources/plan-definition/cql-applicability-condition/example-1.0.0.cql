library example version '1.0.0'
using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

context Patient

define "Patient is Female": Patient.gender = 'female'

define "Patient is above 10 years of age": Patient.birthDate <= Today() - 10 years

define "Get Patient Reference": Reference {
  reference: string { value: 'Patient/' + Patient.id}
}

define "Get Task Description": 'Example Task'

define "Get ServiceRequest Description": 'Example ServiceRequest'
