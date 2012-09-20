(ns genAssessments.core
  (:use clojure.contrib.math)
  (:use clojure.data.json)
  (:use genAssessments.defines)
)

(defn create-address []
  {
    "address" :
    {
       "streetNumberName" : "123 Elm Street",
       "city" : "NY",
       "stateAbbreviation" : "NY",
       "postalCode" : "11011"
    }
  }
)
(defn create-assessment [assessmentName]
  (json_str
    {
      "type" : "assessment",
      "_id" : assessmentName,
      "body" :
      {
        "assessmentTitle" : assessmentName,
        "assessmentIdentificationCode" :
        [
          { "identificationSystem" : "Test Contractor" ,
            "ID": assessmentName
          }
        ],
		"assessmentPerformanceLevel" :
		[
		  {
		    "performanceLevelDescriptor" : [ { "codeValue" : "W" } ],
			"assessmentReportingMethod" : "Scale score",
			"minimumScore" : 6,
			"maximumScore" : 14
		  },
		  {
		    "performanceLevelDescriptor" : [ { "codeValue" : "B" } ],
			"assessmentReportingMethod" : "Scale score",
			"minimumScore" : 15,
			"maximumScore" : 20
		  },
		  {
		    "performanceLevelDescriptor" : [ { "codeValue" : "S" } ],
			"assessmentReportingMethod" : "Scale score",
			"minimumScore" : 21,
			"maximumScore" : 27
		  },
		  {
		    "performanceLevelDescriptor" : [ { "codeValue" : "E" } ],
			"assessmentReportingMethod" : "Scale score",
			"minimumScore" : 28,
			"maximumScore" : 33
		  }
		]
      }
    }
  )
)
(defn create-calendar-date [date calendarEvent]
  (json_str
    {
      "type" : "calendarDate",
      "_id" : (format "%s-%s" date calendarEvent),
      "body" :
      {
        "date" : date,
        "calendarEvent" : calendarEvent
      }
    }
  )
)
(defn create-course [courseName schoolName]
  (json_str
    {
      "type" : "course",
      "_id" : (format "%s-%s" courseName schoolName),
      "body" :
      {
        "courseTitle" : courseName,
        "numberOfParts" : 1,
        (create-course-code courseName),
        "schoolId" : schoolName
      }
    }
  )
)
(defn create-course-code [courseName]
  { "courseCode" :
    [
      {
        "ID" : courseName,
        "identificationSystem" : "CSSC course code"
      }
    ]
  }
)
(defn create-course-offering [sessionName courseName schoolName]
  (json_str
    {
      "type" : "courseOffering",
      "_id" : (format "%s-%s-%s" sessionName courseName schoolName),
      "body" :
      {
        "localCourseCode" : (format "%s-%s-%s" sessionName courseName schoolName),
        "schoolId" : schoolName,
        "courseId" : courseName,
        "sessionId" : sessionName
      }
    }
  )
}
(defn create-district [stateName districtName]
  (json_str
    {
      "type": "localEducationAgency",
      "_id" : districtName,
      "body":
      {
        "stateOrganizationId":stateName,
        "nameOfInstitution":stateName,
        "organizationCategories" :["Local Education Agency"],
        (create-address),
        "parentEducationAgencyReference" : stateName
      }
    }
  )
}
(defn create-name [prefix firstName lastName]
  {
    "firstName" : (rand-nth first-names),
    "lastSurname" : (rand-nth last-names)
  }
)
(defn create-school [districtName schoolName]
  (json_str
    {
      "type": "school",
      "_id" : schoolName,
      "body":
      {
        "schoolCategories" : [ "Elementary School" ],
        "gradesOffered" :
        [
          "First grade",
	      "Second grade",
		  "Third grade",
		  "Fourth grade",
		  "Fifth grade",
		  "Sixth grade"
        ],
        "organizationCategories" : ["School"],
        "stateOrganizationId" : schoolName,
        "nameOfInstitution" : schoolName,
        (create-address),
        "parentEducationAgencyReference" : districtName
      }
    }
  )
}
(defn create-result [result]
  {
    "result":"result",
    "assessmentReportingMethod":"Scale score"
  }
)
(defn create-section [districtName sessionName courseName schoolName sectionName]
  (json_str
    {
      "_id" : (format "%s-%s-%s-%s" schoolName sessionName courseName sectionName),
      "type" : "section",
      "body" :
      {
        "uniqueSectionCode" : sectionName,
        "sequenceOfCourse" : 1,
        "schoolId" : schoolName,
        "sessionId" : sessionName,
        "courseOfferingId" : courseName
      }
    }
  )
}
(defn create-session [sessionName]
  (json_str
    {
      "type" : "session",
      "_id" : (format "%s-Fall-2011" sessionName),
      "body":
      {
        "sessionName" : sessionName,
        "schoolYear" : "2011-2012",
        "term" : "Fall Semester",
        "beginDate" : "2011-09-06",
        "endDate" : "2011-12-16",
        "totalInstructionalDays": 75
      }
    }
  )
}
(defn create-state [stateName]
  (json_str
    {
      "type": "stateEducationAgency",
      "_id" : stateName,
      "body":
      {
        "stateOrganizationId":stateName,
        "nameOfInstitution":stateName,
        "organizationCategories" :["State Education Agency"],
        (create-address)
      }
    }
  )
}
(defn create-student [schoolName studentId]
  (json_str
    {
      "type":"student",
      "_id" : (format "%s-%s" schoolName studentId),
      "body":
      {
        "studentUniqueStateId" : (format "%s-%s" schoolName studentId),
        (create-name),
        "sex" : (rand-nth ["Male" "Female"])
        "birthData": (str "2001-" (format "%02d" (inc (rand-int 12))) "-" (format "%02d" (inc (rand-int 28))))
      }
    }
  )
)
(defn create-student-assessment [adminDate studentName assessmentName]
  (json_str
    {
      "type" : "studentAssessmentAssociation",
      "_id" : (format "%s-%s" assessmentName studentName adminDate),
      "body" :
      {
        "administrationDate" : adminDate,
        "studentId" : studentName,
        "assessmentId" : assessmentName
      }
    }
  )
)
(defn create-student-school-association [schoolName studentName entryDate]
  (json_str
    {
      "type" : "studentSchoolAssociation",
      "_id" : (format "%s-%s-%s" schoolName studentName entryDate),
      "body" :
      {
        "studentId" : studentName,
        "schoolId" : schoolName,
        "entryDate" : entryDate,
        "entryGradeLevel" : "Seventh grade"
      }
    }
  )
)
(defn create-student [studentName sectionName]
  (json_str
    {
      "type" : "studentSectionAssociation",
      "_id" : (format "%s-%s" studentName sectionName),
      "body" :
      {
        "studentId" : studentName,
        "sectionId" : sectionName
      }
    }
  )
)
