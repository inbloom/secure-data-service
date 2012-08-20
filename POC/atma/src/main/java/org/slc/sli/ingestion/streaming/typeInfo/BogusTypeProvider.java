package org.slc.sli.ingestion.streaming.typeInfo;

import java.util.Arrays;
import java.util.List;

import org.slc.sli.ingestion.streaming.TypeProvider;
import org.springframework.stereotype.Component;

@Component
public class BogusTypeProvider implements TypeProvider {

	private static final List<String> COMPLEX = Arrays.asList("assessment", "cohort", "competencyLevelDescriptor", "course", "educationOrganization", "gradingPeriod", "graduationPlan", "learningObjective", "learningStandard", "parent",
			"program", "school", "section", "session", "staff", "student", "studentCompetency", "studentCompetencyObjective", "teacher","assessmentIdentificationCode","availableCredit","gradeBookEntries","studentGradebookEntries","studentAssociations","teacherAssociations");

	public boolean isComplexType(String elementName) {
		return COMPLEX.contains(elementName);
	}

	public Object convertType(String elementName, String value) {
		return value;
	}

}
