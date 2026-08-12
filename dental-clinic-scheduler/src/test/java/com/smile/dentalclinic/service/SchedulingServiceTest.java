package com.smile.dentalclinic.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.smile.dentalclinic.dto.ScheduleRequest;
import com.smile.dentalclinic.dto.ScheduleResponse;
import com.smile.dentalclinic.dto.ScheduledOperationResponse;
import com.smile.dentalclinic.model.Dentist;
import com.smile.dentalclinic.model.Operation;
import com.smile.dentalclinic.model.Patient;

public class SchedulingServiceTest {
	
	private SchedulingService schedulingService;

    @BeforeEach
    void setUp() {
        schedulingService = new SchedulingService();
    }

    @Test
    void shouldScheduleOperationsWithoutOperatoryConflict() {

        ScheduleRequest request = createRequest(
                2,
                List.of(
                        new Operation(1L, 101L, 1L, 4, 1),
                        new Operation(2L, 102L, 2L, 3, 1),
                        new Operation(3L, 103L, 1L, 2, 1)
                )
        );

        ScheduleResponse response =
                schedulingService.generateSchedule(request);

        assertEquals(3, response.getSchedule().size());

        List<ScheduledOperationResponse> schedule =
                response.getSchedule();

        for (int i = 0; i < schedule.size(); i++) {

            for (int j = i + 1; j < schedule.size(); j++) {

                ScheduledOperationResponse first =
                        schedule.get(i);

                ScheduledOperationResponse second =
                        schedule.get(j);

                if (first.getOperatoryId()
                        == second.getOperatoryId()) {

                    boolean overlaps =
                            first.getStartTime()
                                    .isBefore(second.getEndTime())
                                    &&
                            second.getStartTime()
                                    .isBefore(first.getEndTime());

                    assertFalse(
                            overlaps,
                            "Operatory cannot be double booked"
                    );
                }
            }
        }
    }

    @Test
    void shouldNotScheduleSameDentistAtSameTime() {

        ScheduleRequest request = createRequest(
                2,
                List.of(
                        new Operation(1L, 101L, 1L, 4, 1),
                        new Operation(2L, 102L, 1L, 3, 1),
                        new Operation(3L, 103L, 1L, 2, 1)
                )
        );

        ScheduleResponse response =
                schedulingService.generateSchedule(request);

        List<ScheduledOperationResponse> schedule =
                response.getSchedule();

        for (int i = 0; i < schedule.size(); i++) {

            for (int j = i + 1; j < schedule.size(); j++) {

                ScheduledOperationResponse first =
                        schedule.get(i);

                ScheduledOperationResponse second =
                        schedule.get(j);

                if (first.getDentistId()
                        .equals(second.getDentistId())) {

                    boolean overlaps =
                            first.getStartTime()
                                    .isBefore(second.getEndTime())
                                    &&
                            second.getStartTime()
                                    .isBefore(first.getEndTime());

                    assertFalse(
                            overlaps,
                            "Dentist cannot perform overlapping operations"
                    );
                }
            }
        }
    }

    @Test
    void shouldNotScheduleSamePatientAtSameTime() {

        ScheduleRequest request = createRequest(
                2,
                List.of(
                        new Operation(1L, 101L, 1L, 4, 1),
                        new Operation(2L, 101L, 2L, 3, 1)
                )
        );

        ScheduleResponse response =
                schedulingService.generateSchedule(request);

        List<ScheduledOperationResponse> schedule =
                response.getSchedule();

        for (int i = 0; i < schedule.size(); i++) {

            for (int j = i + 1; j < schedule.size(); j++) {

                ScheduledOperationResponse first =
                        schedule.get(i);

                ScheduledOperationResponse second =
                        schedule.get(j);

                if (first.getPatientId()
                        .equals(second.getPatientId())) {

                    boolean overlaps =
                            first.getStartTime()
                                    .isBefore(second.getEndTime())
                                    &&
                            second.getStartTime()
                                    .isBefore(first.getEndTime());

                    assertFalse(
                            overlaps,
                            "Patient cannot have overlapping operations"
                    );
                }
            }
        }
    }

    @Test
    void shouldRejectInvalidOperationDuration() {

        ScheduleRequest request = createRequest(
                2,
                List.of(
                        new Operation(1L, 101L, 1L, 9, 1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.generateSchedule(request)
        );
    }

    @Test
    void shouldRejectInvalidNumberOfOperatories() {

        ScheduleRequest request = createRequest(
                11,
                List.of(
                        new Operation(1L, 101L, 1L, 4, 1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.generateSchedule(request)
        );
    }

    @Test
    void shouldRejectInvalidNumberOfDentists() {

        ScheduleRequest request = createRequest(
                2,
                List.of(
                        new Operation(1L, 101L, 99L, 4, 1)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> schedulingService.generateSchedule(request)
        );
    }

    @Test
    void shouldUseAdditionalOperatoriesForParallelScheduling() {

        ScheduleRequest request =
                createRequest(
                        2,
                        List.of(
                                new Operation(
                                        1L,
                                        101L,
                                        1L,
                                        4,
                                        1
                                ),
                                new Operation(
                                        2L,
                                        102L,
                                        2L,
                                        4,
                                        1
                                )
                        )
                );

        ScheduleResponse response =
                schedulingService.generateSchedule(request);

        assertEquals(
                4,
                response.getCompletionTimeHours()
        );
    }

    @Test
    void shouldOptimizeNumberOfOperatories() {

        ScheduleRequest request =
                createRequest(
                        2,
                        List.of(
                                new Operation(
                                        1L,
                                        101L,
                                        1L,
                                        4,
                                        1
                                ),
                                new Operation(
                                        2L,
                                        102L,
                                        2L,
                                        4,
                                        1
                                )
                        )
                );

        var response =
                schedulingService.optimizeOperatories(request);

        assertNotNull(response);

        assertEquals(
                2,
                response.getRecommendedOperatories()
        );

        assertEquals(
                4,
                response.getMinimumCompletionHours()
        );
    }

    private ScheduleRequest createRequest(
            int numberOfOperatories,
            List<Operation> operations) {

        ScheduleRequest request =
                new ScheduleRequest();

        request.setNumberOfOperatories(
                numberOfOperatories
        );

        request.setDentists(
                List.of(
                        new Dentist(
                                1L,
                                "Dr. Smith"
                        ),
                        new Dentist(
                                2L,
                                "Dr. Johnson"
                        )
                )
        );

        request.setPatients(
                List.of(
                        new Patient(
                                101L,
                                "Alice"
                        ),
                        new Patient(
                                102L,
                                "Bob"
                        ),
                        new Patient(
                                103L,
                                "Charlie"
                        )
                )
        );

        request.setOperations(operations);

        return request;
    }

}
