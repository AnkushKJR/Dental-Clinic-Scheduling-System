# Dental Clinic Scheduling System

A small web application built for the Certes Networks take-home assignment.

The application schedules dental operations while considering the availability of dentists, patients and operatories. It also compares different numbers of operatories to see whether adding more rooms actually improves the overall completion time.

## Live Demo

**[Open the application](https://dental-clinic-scheduling-system.onrender.com/)**

The application is deployed on Render, so the first request may take a little longer if the service has been inactive.

## Tech Used

* Java 17
* Spring Boot
* Spring Web
* Spring Validation
* Thymeleaf
* Maven
* JUnit 5
* Docker

## What the application does

* Add dentists
* Add patients
* Add dental operations
* Select the number of operatories
* Generate a schedule
* Prevent an operatory from being double booked
* Prevent a dentist from being scheduled for two operations at the same time
* Prevent a patient from having overlapping operations
* Validate operation duration from 1 to 8 hours
* Compare schedules with 1 to 10 operatories
* Show the recommended number of operatories

## Scheduling Approach

I used a greedy scheduling approach.

Operations are sorted by priority and then by duration, with longer operations being scheduled first when priorities are the same.

For each operation, the scheduler checks:

* When the assigned dentist is free
* When the patient is free
* When each operatory is free

The operation is then placed at the earliest time when all three are available.

For example, if:

```text
Dentist   → 11:00
Patient   → 09:00
Operatory → 10:00
```

the operation can start at 11:00.

After scheduling it, the availability time of the dentist, patient and selected operatory is updated.

## Operatory Comparison

The application can run the same workload with different numbers of operatories.

It checks:

```text
1 operatory
2 operatories
3 operatories
...
10 operatories
```

and compares the time required to finish all operations.

The recommendation is the smallest number of operatories that gives the minimum completion time.

For example, if 3 operatories finish the workload in 6 hours and adding a 4th operatory still takes 6 hours, the application recommends 3.

## Assumptions

Some details were not specified in the assignment, so I made the following assumptions:

* Each operation has one assigned dentist.
* Each operation has one patient.
* Each operation requires one operatory.
* A dentist cannot perform two operations at the same time.
* A patient cannot have two operations at the same time.
* The clinic schedule starts at 09:00.
* Operation durations are whole hours between 1 and 8.
* Dentist working hours, breaks and holidays are not considered.
* The scheduler is a heuristic and does not guarantee a mathematically optimal schedule.
* Data is currently kept in memory and is not persisted in a database.

## Running Locally

Clone the repository:

```bash
git clone https://github.com/AnkushKJR/Dental-Clinic-Scheduling-System.git
```

Go into the application directory:

```bash
cd Dental-Clinic-Scheduling-System/dental-clinic-scheduler
```

Run the application:

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Running Tests

Run:

```bash
mvn test
```

The tests cover the main scheduling rules, including operatory, dentist and patient conflicts, validation and operatory comparison.

## API

### Generate Schedule

```http
POST /api/schedules
```

### Compare Operator Efficiency

```http
POST /api/schedules/optimize
```

Both endpoints accept the clinic data as JSON and return the generated scheduling information.

## Project Structure

```text
dental-clinic-scheduler
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com/certes/dentalclinic
│   │   │       ├── controller
│   │   │       ├── dto
│   │   │       ├── model
│   │   │       └── service
│   │   │
│   │   └── resources
│   │       ├── templates
│   │       └── application.properties
│   │
│   └── test
│
├── Dockerfile
├── pom.xml
└── README.md
```

## Possible Next Steps

If this were extended beyond the assignment, I would consider adding database persistence, dentist working hours, appointment rescheduling/cancellation and a calendar-style view for the generated schedule.
