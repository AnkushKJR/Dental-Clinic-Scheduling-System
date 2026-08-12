package com.smile.dentalclinic.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smile.dentalclinic.dto.OptimizationResponse;
import com.smile.dentalclinic.dto.ScheduleRequest;
import com.smile.dentalclinic.dto.ScheduleResponse;
import com.smile.dentalclinic.dto.ScheduledOperationResponse;
import com.smile.dentalclinic.model.Dentist;
import com.smile.dentalclinic.model.Operation;
import com.smile.dentalclinic.model.Patient;

@Service
public class SchedulingService {
	
	private static final LocalDateTime CLINIC_START_TIME =
            LocalDateTime.of(2026, 8, 12, 9, 0);

    public ScheduleResponse generateSchedule(ScheduleRequest request) {

        validateRequest(request);

        List<Operation> operations =
                new ArrayList<>(request.getOperations());

        /*
         * Scheduling priority:
         * 1. Higher priority first
         * 2. Longer operations first
         * 3. Operation ID
         */
        operations.sort(
                Comparator.comparingInt(Operation::getPriority)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        Operation::getDurationHours
                                ).reversed()
                        )
                        .thenComparing(Operation::getId)
        );

        Map<Long, LocalDateTime> dentistAvailableAt =
                new HashMap<>();

        Map<Long, LocalDateTime> patientAvailableAt =
                new HashMap<>();

        Map<Integer, LocalDateTime> operatoryAvailableAt =
                new HashMap<>();

        for (Dentist dentist : request.getDentists()) {
            dentistAvailableAt.put(
                    dentist.getId(),
                    CLINIC_START_TIME
            );
        }

        for (Patient patient : request.getPatients()) {
            patientAvailableAt.put(
                    patient.getId(),
                    CLINIC_START_TIME
            );
        }

        for (int i = 1;
             i <= request.getNumberOfOperatories();
             i++) {

            operatoryAvailableAt.put(
                    i,
                    CLINIC_START_TIME
            );
        }

        List<ScheduledOperationResponse> scheduledOperations =
                new ArrayList<>();

        for (Operation operation : operations) {

            LocalDateTime dentistAvailable =
                    dentistAvailableAt.get(
                            operation.getDentistId()
                    );

            LocalDateTime patientAvailable =
                    patientAvailableAt.get(
                            operation.getPatientId()
                    );

            int selectedOperatory = -1;
            LocalDateTime selectedStart = null;

            for (int operatoryId = 1;
                 operatoryId <= request.getNumberOfOperatories();
                 operatoryId++) {

                LocalDateTime operatoryAvailable =
                        operatoryAvailableAt.get(operatoryId);

                LocalDateTime possibleStart =
                        max(
                                dentistAvailable,
                                patientAvailable,
                                operatoryAvailable
                        );

                if (selectedStart == null
                        || possibleStart.isBefore(selectedStart)) {

                    selectedStart = possibleStart;
                    selectedOperatory = operatoryId;
                }
            }

            LocalDateTime endTime =
                    selectedStart.plusHours(
                            operation.getDurationHours()
                    );

            scheduledOperations.add(
                    new ScheduledOperationResponse(
                            operation.getId(),
                            operation.getPatientId(),
                            operation.getDentistId(),
                            selectedOperatory,
                            selectedStart,
                            endTime
                    )
            );

            dentistAvailableAt.put(
                    operation.getDentistId(),
                    endTime
            );

            patientAvailableAt.put(
                    operation.getPatientId(),
                    endTime
            );

            operatoryAvailableAt.put(
                    selectedOperatory,
                    endTime
            );
        }

        long completionTimeHours =
                calculateCompletionHours(
                        scheduledOperations
                );

        scheduledOperations.sort(
                Comparator.comparing(
                        ScheduledOperationResponse::getStartTime
                )
        );

        return new ScheduleResponse(
                scheduledOperations,
                completionTimeHours,
                request.getNumberOfOperatories()
        );
    }

    public OptimizationResponse optimizeOperatories(
            ScheduleRequest request) {

        List<OptimizationResponse.OperatoryComparison>
                comparisons = new ArrayList<>();

        long previousCompletion = -1;

        int recommendedOperatories = 1;
        long minimumCompletion = Long.MAX_VALUE;

        for (int numberOfOperatories = 1;
             numberOfOperatories <= 10;
             numberOfOperatories++) {

            ScheduleRequest testRequest =
                    copyRequestWithOperatories(
                            request,
                            numberOfOperatories
                    );

            ScheduleResponse response =
                    generateSchedule(testRequest);

            long completion =
                    response.getCompletionTimeHours();

            double improvement = 0;

            if (previousCompletion > 0) {
                improvement =
                        ((double) (previousCompletion - completion)
                                / previousCompletion)
                                * 100;
            }

            comparisons.add(
                    new OptimizationResponse.OperatoryComparison(
                            numberOfOperatories,
                            completion,
                            improvement
                    )
            );

            /*
             * We select the smallest number of operatories
             * that achieves the minimum completion time.
             */
            if (completion < minimumCompletion) {
                minimumCompletion = completion;
                recommendedOperatories =
                        numberOfOperatories;
            }

            previousCompletion = completion;
        }

        return new OptimizationResponse(
                recommendedOperatories,
                minimumCompletion,
                comparisons
        );
    }

    private ScheduleRequest copyRequestWithOperatories(
            ScheduleRequest original,
            int numberOfOperatories) {

        ScheduleRequest request = new ScheduleRequest();

        request.setNumberOfOperatories(
                numberOfOperatories
        );

        request.setDentists(
                original.getDentists()
        );

        request.setPatients(
                original.getPatients()
        );

        request.setOperations(
                original.getOperations()
        );

        return request;
    }

    private long calculateCompletionHours(
            List<ScheduledOperationResponse> schedule) {

        if (schedule.isEmpty()) {
            return 0;
        }

        LocalDateTime earliest =
                CLINIC_START_TIME;

        LocalDateTime latest =
                schedule.stream()
                        .map(
                                ScheduledOperationResponse
                                        ::getEndTime
                        )
                        .max(LocalDateTime::compareTo)
                        .orElse(CLINIC_START_TIME);

        return java.time.Duration.between(
                earliest,
                latest
        ).toHours();
    }

    private LocalDateTime max(
            LocalDateTime first,
            LocalDateTime second,
            LocalDateTime third) {

        LocalDateTime result =
                first.isAfter(second)
                        ? first
                        : second;

        return result.isAfter(third)
                ? result
                : third;
    }

    private void validateRequest(
            ScheduleRequest request) {

        if (request.getNumberOfOperatories() < 1
                || request.getNumberOfOperatories() > 10) {

            throw new IllegalArgumentException(
                    "Number of operatories must be between 1 and 10"
            );
        }

        if (request.getDentists() == null
                || request.getDentists().isEmpty()
                || request.getDentists().size() > 10) {

            throw new IllegalArgumentException(
                    "Number of dentists must be between 1 and 10"
            );
        }

        if (request.getPatients() == null
                || request.getPatients().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one patient is required"
            );
        }

        if (request.getOperations() == null
                || request.getOperations().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one operation is required"
            );
        }

        for (Operation operation :
                request.getOperations()) {

            if (operation.getDurationHours() < 1
                    || operation.getDurationHours() > 8) {

                throw new IllegalArgumentException(
                        "Operation duration must be between 1 and 8 hours"
                );
            }

            boolean dentistExists =
                    request.getDentists()
                            .stream()
                            .anyMatch(
                                    dentist ->
                                            dentist.getId()
                                                    .equals(
                                                            operation
                                                                    .getDentistId()
                                                    )
                            );

            if (!dentistExists) {
                throw new IllegalArgumentException(
                        "Dentist "
                                + operation.getDentistId()
                                + " does not exist"
                );
            }

            boolean patientExists =
                    request.getPatients()
                            .stream()
                            .anyMatch(
                                    patient ->
                                            patient.getId()
                                                    .equals(
                                                            operation
                                                                    .getPatientId()
                                                    )
                            );

            if (!patientExists) {
                throw new IllegalArgumentException(
                        "Patient "
                                + operation.getPatientId()
                                + " does not exist"
                );
            }
        }
    }

}
