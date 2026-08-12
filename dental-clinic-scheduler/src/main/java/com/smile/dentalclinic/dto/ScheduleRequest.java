package com.smile.dentalclinic.dto;

import java.util.List;

import com.smile.dentalclinic.model.Dentist;
import com.smile.dentalclinic.model.Operation;
import com.smile.dentalclinic.model.Patient;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class ScheduleRequest {

	@Min(value = 1, message = "Number of operatories must be at least 1")
	@Max(value = 10, message = "Number of operatories cannot exceed 10")
	private int numberOfOperatories;

	@NotEmpty(message = "At least one dentist is required")
	@Valid
	private List<Dentist> dentists;

	@NotEmpty(message = "At least one patient is required")
	@Valid
	private List<Patient> patients;

	@NotEmpty(message = "At least one operation is required")
	@Valid
	private List<Operation> operations;

	public int getNumberOfOperatories() {
		return numberOfOperatories;
	}

	public void setNumberOfOperatories(int numberOfOperatories) {
		this.numberOfOperatories = numberOfOperatories;
	}

	public List<Dentist> getDentists() {
		return dentists;
	}

	public void setDentists(List<Dentist> dentists) {
		this.dentists = dentists;
	}

	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

}
