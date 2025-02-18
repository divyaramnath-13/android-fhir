library PatientDataCollection version '1.0.1'
using FHIR version '4.0.1'
include FHIRHelpers version '4.0.1'

// Function argument/parameter
parameter "prefix" String

define "Get PractitionerRole": [PractitionerRole] P return P
define "Get PractitionerRole Reference": Reference {
  reference: string { value: 'PractitionerRole/' + "Get PractitionerRole"[0].id },
  display: string { value: "Get PractitionerRole"[0].specialty[0].coding[0].display },
  type: uri { value: 'PractitionerRole' }
}

define "True": true

// Example of how a function can be created
define "Get Specific Patient": [Patient] P
                                    where P.id.contains(prefix)
                                        return P

// If there is a Patient whose id contains prefix, return the family name
define "Patient id containing prefix": "Get Specific Patient".name[0].family

define "Get Value": Reference {
  reference: string { value: 'Questionnaire/' + prefix}
}


context Patient

define "Get Patient":
    singleton from ([Patient])

define "Patient is Female": Patient.gender = 'female'
define "Get Patient Reference": Reference {
  reference: string { value: 'Patient/' + Patient.id}
}
