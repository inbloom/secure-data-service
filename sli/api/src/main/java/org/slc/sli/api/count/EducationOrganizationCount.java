package org.slc.sli.api.count;

public class EducationOrganizationCount {

	private String educationOrganizationId;
	private int totalStaff;
	private int currentStaff;
	private int totalStudent;
	private int currentStudent;
	private int totalTeacher;
	private int currentTeacher;
	private int totalNonTeacher;
	private int currentNonTeacher;

	public String getEducationOrganizationId() {
		return educationOrganizationId;
	}

	public void setEducationOrganizationId(String educationOrganizationId) {
		this.educationOrganizationId = educationOrganizationId;
	}

	public int getTotalStaff() {
		return totalStaff;
	}

	public void setTotalStaff(int totalStaff) {
		this.totalStaff = totalStaff;
	}
	
	public int getCurrentStaff() {
		return currentStaff;
	}
	
	public void setCurrentStaff(int currentStaff) {
		this.currentStaff = currentStaff;
	}
	
	public int getTotalStudent() {
		return totalStudent;
	}
	
	public void setTotalStudent(int totalStudent) {
		this.totalStudent = totalStudent;
	}
	
	public int getCurrentStudent() {
		return currentStudent;
	}
	
	public void setCurrentStudent(int currentStudent) {
		this.currentStudent = currentStudent;
	}
	
	public int getTotalTeacher() {
		return totalTeacher;
	}
	
	public void setTotalTeacher(int totalTeacher) {
		this.totalTeacher = totalTeacher;
	}
	
	public int getCurrentTeacher() {
		return currentTeacher;
	}
	
	public void setCurrentTeacher(int currentTeacher) {
		this.currentTeacher = currentTeacher;
	}
	
	public int getTotalNonTeacher() {
		return totalNonTeacher;
	}
	
	public void setTotalNonTeacher(int totalNonTeacher) {
		this.totalNonTeacher = totalNonTeacher;
	}
	
	public int getCurrentNonTeacher() {
		return currentNonTeacher;
	}
	
	public void setCurrentNonTeacher(int currentNonTeacher) {
		this.currentNonTeacher = currentNonTeacher;
	}

	
}