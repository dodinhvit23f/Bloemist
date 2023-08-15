package com.bloemist.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


/**
 * The persistent class for the staff_description database table.
 * 
 */
@Entity
@Table(name="staff_description")
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class StaffDescription extends BaseEntity {
	private static final long serialVersionUID = 1L;

	@Column(name="department_id")
	private Long departmentId;

	@Column(name="job_grade_id")
	private Long jobGradeId;

	@Column(name="user_id")
	private Long userId;

	public StaffDescription() {
 
	}
}