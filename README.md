# Dental Clinic Scheduler

A simple web application for scheduling dental operations across dentists, patients and operatories.

The application generates a schedule while making sure that an operatory, dentist or patient is not assigned to overlapping operations.

## Tech Stack

* Java 17
* Spring Boot
* Spring Web
* Spring Validation
* Thymeleaf
* Maven
* JUnit 5

The application currently uses in-memory data. No database is required to run it.

## Features

* Add dentists
* Add patients
* Add operations
* Configure the number of operatories
* Generate a schedule
* Prevent operatory double booking
* Prevent dentist conflicts
* Prevent patient conflicts
* Validate operation duration
* Compare completion time with 1 to 10 operatories
* Recommend the minimum number of operatories needed for the best completion time

## How to Run

Clone the repository and open the project.

Run:

```bash
mvn spring-boot:run
```

The application will start on:

```text
http://localhost:8080
```

Open the above URL in a browser.

To run the tests:

```bash
mvn test
```

## How Scheduling Works

The scheduler uses a greedy approach.

Operations are sorted using:

1. Priority
2. Duration (longer operations first)
3. Operation ID

For every operation, the scheduler checks when the following resources are available:

* Assigned dentist
* Patient
* Each operatory

The operation is started at the earliest time when all required resources are available.

For example:

```text
Dentist available   : 11:00
Patient available   : 09:00
Operatory available : 10:00
```

The operation can start at:

```text
11:00
```

After scheduling the operation, the availability time of the dentist, patient and selected operatory is updated.

## Double Booking

The scheduler prevents overlapping operations for:

* The same operatory
* The same dentist
* The same patient

This is important because even if multiple operatories are available, one dentist should not be scheduled for two patients at the same time.

## Operatory Analysis

The application also checks how the schedule changes when the clinic has different numbers of operatories.

It evaluates:

```text
1 operator
2 operatories
3 operatories
...
10 operatories
```

For each case, it calculates the time required to finish all operations.

The application recommends the smallest number of operatories that achieves the minimum completion time.

For example:

```text
Operatories    Completion Time

1              12 hours
2               8 hours
3               6 hours
4               6 hours
5               6 hours
```

In this case, 3 operatories would be recommended because adding more operatories does not improve the completion time.

## API

### Generate Schedule

```http
POST /api/schedules
```

Example request:

```json
{
  "numberOfOperatories": 2,
  "dentists": [
    {
      "id": 1,
      "name": "Dr. Smith"
    },
    {
      "id": 2,
      "name": "Dr. Johnson"
    }
  ],
  "patients": [
    {
      "id": 101,
      "name": "Alice"
    },
    {
      "id": 102,
      "name": "Bob"
    }
  ],
  "operations": [
    {
      "id": 1,
      "patientId": 101,
      "dentistId": 1,
      "durationHours": 4,
      "priority": 1
    },
    {
      "id": 2,
      "patientId": 102,
      "dentistId": 2,
      "durationHours": 3,
      "priority": 1
    }
  ]
}
```

### Analyze Operator Efficiency

```http
POST /api/schedules/optimize
```

This evaluates the same set of operations using 1 to 10 operatories and returns the completion time for each configuration along with the recommended number of operatories.

## Validation

The application validates the main constraints from the assignment:

* Number of operatories must be between 1 and 10.
* Number of dentists must be between 1 and 10.
* Operation duration must be between 1 and 8 hours.
* Each operation must reference an existing dentist.
* Each operation must reference an existing patient.

Invalid requests return a `400 Bad Request` response.

## Assumptions

The assignment leaves some details open, so I made the following assumptions:

* Each operation has one assigned dentist.
* Each operation has one assigned patient.
* Each operation requires one operatory.
* A dentist cannot work on two operations at the same time.
* A patient cannot have two operations at the same time.
* The schedule starts at 09:00.
* Operation durations are whole hours.
* There are no breaks or dentist working-hour restrictions in the current version.
* The scheduler uses a greedy approach and does not try to find a mathematically optimal schedule.

## Project Structure

```text
src/main/java/com/certes/dentalclinic

├── controller
│   ├── GlobalExceptionHandler.java
│   ├── HomeController.java
│   └── ScheduleController.java
│
├── dto
│   ├── OptimizationResponse.java
│   ├── ScheduleRequest.java
│   ├── ScheduleResponse.java
│   └── ScheduledOperationResponse.java
│
├── model
│   ├── Dentist.java
│   ├── Operation.java
│   ├── Operatory.java
│   └── Patient.java
│
└── service
    └── SchedulingService.java
```

## Tests

The project contains tests for the main scheduling rules, including:

* Operatory double booking
* Dentist overlap
* Patient overlap
* Invalid operation duration
* Invalid number of operatories
* Invalid dentist
* Parallel scheduling with multiple operatories
* Operatory optimization

Run them with:

```bash
mvn test
```

## Possible Improvements

If this were developed further, some possible additions would be:

* Database support
* Login and user roles
* Dentist working hours
* Clinic holidays and breaks
* Appointment rescheduling
* Appointment cancellation
* Calendar-style schedule view
* More advanced scheduling/optimization algorithms
