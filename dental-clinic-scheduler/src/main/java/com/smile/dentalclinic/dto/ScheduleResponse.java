package com.smile.dentalclinic.dto;

import java.util.List;

public class ScheduleResponse {
	
	private List<ScheduledOperationResponse> schedule;
    private long completionTimeHours;
    private int numberOfOperatories;

    public ScheduleResponse() {
    }

    public ScheduleResponse(
            List<ScheduledOperationResponse> schedule,
            long completionTimeHours,
            int numberOfOperatories) {

        this.schedule = schedule;
        this.completionTimeHours = completionTimeHours;
        this.numberOfOperatories = numberOfOperatories;
    }

    public List<ScheduledOperationResponse> getSchedule() {
        return schedule;
    }

    public long getCompletionTimeHours() {
        return completionTimeHours;
    }

    public int getNumberOfOperatories() {
        return numberOfOperatories;
    }

}
