package org.slc.sli.test.exportTool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slc.sli.test.edfi.entities.Address;
import org.slc.sli.test.edfi.entities.AddressType;
import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.BirthData;
import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Student;

public class ExportAssessment {
	private ObjectFactory factory = new ObjectFactory();
	private InterchangeAssessmentMetadata interchangeAssessmentMetadata = factory
			.createInterchangeAssessmentMetadata();

	private ResultSet assessmentResultSet;
	public String assessmentQuery = new StringBuilder()
			.append("SELECT StudentUSI \n")
			.append("    ,PersonalTitlePrefixTypeId \n")
			.append("    ,FirstName \n").append("    ,MiddleName \n")
			.append("    ,LastSurname \n")
			.append("    ,GenerationCodeSuffixTypeId \n")
			.append("    ,MaidenName \n")
			.append("    ,PersonalInformationVerificationTypeId \n")
			.append("    ,SexType.CodeValue \n").append("    ,BirthDate \n")
			.append("    ,CityOfBirth \n")
			.append("    ,StateOfBirthAbbreviationTypeId \n")
			.append("    ,CountryOfBirthCodeTypeId \n")
			.append("    ,DateEnteredUS \n")
			.append("    ,MultipleBirthStatus \n")
			.append("    ,ProfileThumbnail \n")
			.append("    ,HispanicLatinoEthnicity \n")
			.append("    ,OldEthnicityTypeId \n")
			.append("    ,EconomicDisadvantaged \n")
			.append("    ,SchoolFoodServicesEligibilityTypeId \n")
			.append("    ,LimitedEnglishProficiencyTypeId \n")
			.append("    ,DisplacementStatusType \n")
			.append("    ,LoginId \n")
			.append("FROM Student, SexType \n")
			.append("WHERE Student.SexTypeId = SexType.SexTypeId \n")
			.append("ORDER BY StudentUSI \n").toString();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExportAssessment es = new ExportAssessment();
		System.out.println(es.assessmentQuery);
		es.getDataSet();
		es.getInterchangeAssessmentMetadatas();
		es.generateXML();
	}

	private void getDataSet() {
		Connection conn = DBUtil.getConnection();
		assessmentResultSet = DBUtil.getResultSet(conn, this.assessmentQuery);
	}

	private Assessment getAssessment(ResultSet srs) {
		Assessment assessment = factory.createAssessment();
		try {
			String studentUniqueStateId = srs.getString("StudentUSI");
			assessment.setStudentUniqueStateId(studentUniqueStateId);

			Name studentName = factory.createName();
			studentName.setFirstName(this.assessmentResultSet
					.getString("FirstName"));
			studentName.setLastSurname(this.assessmentResultSet
					.getString("LastSurname"));
			assessment.setName(studentName);

			assessment.setSex(SexType.fromValue(this.assessmentResultSet
					.getString("CodeValue")));

			BirthData birthData = factory.createBirthData();
			birthData.setBirthDate(DBUtil
					.convertStringToCalendar(this.assessmentResultSet
							.getString("BirthDate")));
			assessment.setBirthData(birthData);

			assessment.setProfileThumbnail(this.assessmentResultSet.getString("ProfileThumbnail"));

			assessment.setHispanicLatinoEthnicity(this.assessmentResultSet
					.getBoolean("HispanicLatinoEthnicity"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return assessment;
	}

	private InterchangeAssessmentMetadata getInterchangeAssessmentMetadatas() {
		try {
			while (this.assessmentResultSet.next()) {
				interchangeAssessmentMetadata.getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor().add(
						this.getAssessment(this.assessmentResultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interchangeAssessmentMetadata;
	}

	private void generateXML() {
		QName qName = new QName("InterchangeAssessmentMetadata");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("org.slc.sli.test.edfi.entities");
			JAXBElement<InterchangeAssessmentMetadata> element = new JAXBElement<InterchangeAssessmentMetadata>(
					qName, InterchangeAssessmentMetadata.class, interchangeAssessmentMetadata);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			marshaller.marshal(element, System.out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
