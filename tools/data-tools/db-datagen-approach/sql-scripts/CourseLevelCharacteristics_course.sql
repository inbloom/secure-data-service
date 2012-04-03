SELECT c.EducationOrganizationId
      ,c.IdentityCourseCode
      --,clc.CourseLevelCharacteristicsTypeId
      ,clct.CodeValue as CourseLevelCharacteristic
  FROM EdFi.edfi.CourseLevelCharacteristics clc
  RIGHT JOIN EdFi.edfi.Course c 
	ON c.EducationOrganizationId = clc.EducationOrganizationId
		AND c.IdentityCourseCode = clc.IdentityCourseCode
  LEFT JOIN EdFi.edfi.CourseLevelCharacteristicsType clct ON clc.CourseLevelCharacteristicsTypeId = clct.CourseLevelCharacteristicsTypeId
  ORDER BY c.EducationOrganizationId, c.IdentityCourseCode


