package com.smile.dentalclinic.model;

public class Operation {
	
	private Long id;
    private Long patientId;
    private Long dentistId;
    private int durationHours;
    private int priority;

    public Operation() {
    }

    public Operation(
            Long id,
            Long patientId,
            Long dentistId,
            int durationHours,
            int priority) {

        this.id = id;
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.durationHours = durationHours;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
