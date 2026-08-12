package com.smile.dentalclinic.dto;

import java.time.LocalDateTime;

public class ScheduledOperationResponse {
	
	private Long operationId;
    private Long patientId;
    private Long dentistId;
    private int operatoryId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScheduledOperationResponse() {
    }

    public ScheduledOperationResponse(
            Long operationId,
            Long patientId,
            Long dentistId,
            int operatoryId,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        this.operationId = operationId;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.operatoryId = operatoryId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getOperationId() {
        return operationId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public int getOperatoryId() {
        return operatoryId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

}
