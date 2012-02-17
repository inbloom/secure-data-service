/****** Script for SelectTopNRows command from SSMS  ******/
SELECT aic.AssessmentTitle
      ,aic.AcademicSubjectTypeId
      ,aic.AssessedGradeLevelTypeId
      ,aic.Version
      ,aist.CodeValue as AssessmentIdentificationSystem
      ,aic.AssigningOrganizationCode
      ,aic.IdentificationCode
  FROM EdFi.edfi.AssessmentIdentificationCode aic
  RIGHT JOIN EdFi.edfi.Assessment a 
	ON a.AssessmentTitle = aic.AssessmentTitle
		AND a.AcademicSubjectTypeId = aic.AcademicSubjectTypeId
		AND a.AssessedGradeLevelTypeId = aic.AssessedGradeLevelTypeId
		AND a.Version = aic.Version
  LEFT JOIN EdFi.edfi.AssessmentIdentificationSystemType aist ON aic.AssessmentIdentificationSystemTypeId = aist.AssessmentIdentificationSystemTypeId
  ORDER BY aic.AssessmentTitle, aic.AcademicSubjectTypeId, aic.AssessedGradeLevelTypeId, aic.Version
