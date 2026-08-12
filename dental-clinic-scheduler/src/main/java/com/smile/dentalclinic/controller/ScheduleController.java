package com.smile.dentalclinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smile.dentalclinic.dto.OptimizationResponse;
import com.smile.dentalclinic.dto.ScheduleRequest;
import com.smile.dentalclinic.dto.ScheduleResponse;
import com.smile.dentalclinic.service.SchedulingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {
	
	private final SchedulingService schedulingService;

    public ScheduleController(
            SchedulingService schedulingService) {

        this.schedulingService = schedulingService;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> generateSchedule(
            @Valid @RequestBody ScheduleRequest request) {

        return ResponseEntity.ok(
                schedulingService.generateSchedule(request)
        );
    }

    @PostMapping("/optimize")
    public ResponseEntity<OptimizationResponse>
    optimizeOperatories(
            @Valid @RequestBody ScheduleRequest request) {

        return ResponseEntity.ok(
                schedulingService.optimizeOperatories(request)
        );
    }

}
