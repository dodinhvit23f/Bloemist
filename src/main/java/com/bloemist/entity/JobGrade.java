package com.bloemist.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the job_grade database table.
 * 
 */
@Data
@Entity
@Table(name="job_grade")
@EqualsAndHashCode(callSuper = false)
public class JobGrade extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String name;

	public JobGrade() {
	}
	
}