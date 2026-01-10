package com.journalSystem.clinical_service.service;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for looking up FHIR resources by identifier (personnummer).
 */
@Service
@RequiredArgsConstructor
public class FhirLookupService {

    private final HapiClientService hapiClient;

    public String findPatientIdByPersonnummer(String personnummer) {
        if (personnummer == null || personnummer.isEmpty()) {
            throw new IllegalArgumentException("Personnummer cannot be null or empty");
        }

        IGenericClient client = hapiClient.getClient();

        try {
            Bundle bundle = client
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.IDENTIFIER.exactly().identifier(personnummer))
                    .returnBundle(Bundle.class)
                    .execute();

            List<Patient> patients = BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Patient) entry.getResource())
                    .toList();

            if (!patients.isEmpty()) {
                String fhirId = patients.get(0).getIdElement().getIdPart();
                System.out.println("✓ Found Patient by identifier: " + personnummer + " -> FHIR ID: " + fhirId);
                return fhirId;
            }

            try {
                Patient patient = client
                        .read()
                        .resource(Patient.class)
                        .withId(personnummer)
                        .execute();

                if (patient != null) {
                    String fhirId = patient.getIdElement().getIdPart();
                    System.out.println("✓ Found Patient by direct ID: " + personnummer);
                    return fhirId;
                }
            } catch (Exception e) {
            }

            throw new RuntimeException("Patient not found with identifier or ID: " + personnummer);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error looking up patient: " + personnummer);
            e.printStackTrace();
            throw new RuntimeException("Error looking up patient: " + personnummer, e);
        }
    }


    public String findPractitionerIdByPersonnummer(String personnummer) {
        if (personnummer == null || personnummer.isEmpty()) {
            throw new IllegalArgumentException("Personnummer cannot be null or empty");
        }

        IGenericClient client = hapiClient.getClient();

        try {
            Bundle bundle = client
                    .search()
                    .forResource(Practitioner.class)
                    .where(Practitioner.IDENTIFIER.exactly().identifier(personnummer))
                    .returnBundle(Bundle.class)
                    .execute();

            List<Practitioner> practitioners = BundleUtil.toListOfEntries(hapiClient.getContext(), bundle)
                    .stream()
                    .map(entry -> (Practitioner) entry.getResource())
                    .toList();

            if (!practitioners.isEmpty()) {
                String fhirId = practitioners.get(0).getIdElement().getIdPart();
                System.out.println("✓ Found Practitioner by identifier: " + personnummer + " -> FHIR ID: " + fhirId);
                return fhirId;
            }

            try {
                Practitioner practitioner = client
                        .read()
                        .resource(Practitioner.class)
                        .withId(personnummer)
                        .execute();

                if (practitioner != null) {
                    String fhirId = practitioner.getIdElement().getIdPart();
                    System.out.println("✓ Found Practitioner by direct ID: " + personnummer);
                    return fhirId;
                }
            } catch (Exception e) {
            }

            throw new RuntimeException("Practitioner not found with identifier or ID: " + personnummer);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error looking up practitioner: " + personnummer);
            e.printStackTrace();
            throw new RuntimeException("Error looking up practitioner: " + personnummer, e);
        }
    }

    public Optional<String> findPatientIdOptional(String personnummer) {
        try {
            return Optional.of(findPatientIdByPersonnummer(personnummer));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> findPractitionerIdOptional(String personnummer) {
        try {
            return Optional.of(findPractitionerIdByPersonnummer(personnummer));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}