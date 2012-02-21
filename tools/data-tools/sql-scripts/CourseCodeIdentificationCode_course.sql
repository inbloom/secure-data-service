SELECT c.EducationOrganizationId
      ,c.IdentityCourseCode
      --,ccic.CourseCodeSystemTypeId
      ,ccst.CodeValue as IdentificationSystem
      ,ccic.AssigningOrganizationCode as AssigningOrganizationCode
      ,ccic.IdentificationCode as CourseCodeID
  FROM EdFi.edfi.CourseCodeIdentificationCode ccic
  RIGHT JOIN EdFi.edfi.Course c 
	ON c.EducationOrganizationId = ccic.EducationOrganizationId
	   AND c.IdentityCourseCode = ccic.IdentityCourseCode
  LEFT JOIN EdFi.edfi.CourseCodeSystemType ccst ON ccic.CourseCodeSystemTypeId = ccst.CourseCodeSystemTypeId
  ORDER BY c.EducationOrganizationId, c.IdentityCourseCode
