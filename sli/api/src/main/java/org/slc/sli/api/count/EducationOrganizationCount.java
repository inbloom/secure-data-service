/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.slc.sli.api.count;

import java.util.HashSet;
import java.util.Set;


/**
 * This is a model class that models the EducationOrganization counts that are
 * being used in the Data Browser currently
 * 
 * @author mbrush
 *
 */

public class EducationOrganizationCount {

	private Set<String> educationOrganizationId;
	private int totalStaff;
	private int currentStaff;
	private int totalStudent;
	private int currentStudent;
	private int totalTeacher;
	private int currentTeacher;
	private int totalNonTeacher;
	private int currentNonTeacher;

	public Set<String> getEducationOrganizationId() {
		if (null == this.educationOrganizationId) {
			this.educationOrganizationId = new HashSet<String>();
		}
		return educationOrganizationId;
	}

	public void addEducationOrganizationId(String educationOrganizationId) {
		if (null == this.educationOrganizationId) {
			this.educationOrganizationId = new HashSet<String>();
		}
		this.educationOrganizationId.add(educationOrganizationId);
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