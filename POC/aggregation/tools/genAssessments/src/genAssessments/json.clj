(ns genAssessments.json
  (:use clojure.contrib.math)
  (:use clojure.data.json)
  (:use genAssessments.defines)
  (:require [clojure.java.io :as io])
)

(defn sha1 [id]
  (apply str
    (map (partial format "%02x") (.digest (doto (java.security.MessageDigest/getInstance "SHA-1").reset(.update(.getBytes id)))))
  )
)

(defn gen-json [output-file contents]
  (def entity (merge contents { :metaData { :tenantId "Hyrule" } } ) )    
  (with-open [f (io/writer output-file :append true)]
    (.write f (with-out-str (print-json entity)))
    (.write f "\n")
  )
)

(defn create-name []
  {
    :firstName (rand-nth first-names),
    :lastSurname (rand-nth last-names)
  }
)

(defn create-address []
  {
   :streetNumberName "123 Elm Street",
   :city "NY",
   :stateAbbreviation "NY",
   :postalCode "11011"
  }
)

(defn create-result [result]
  {
    :result result,
    :assessmentReportingMethod "Scale score"
  }
)

(defn create-student [schoolName studentId]
  {
    :type "student",
    :_id (sha1 (student-id schoolName studentId)),
    :body
    {
      :studentUniqueStateId (student-id schoolName studentId),
      :name (create-name),
      :sex (rand-nth ["Male" "Female"])
      :birthData (str "2001-" (format "%02d" (inc (rand-int 12))) "-" (format "%02d" (inc (rand-int 28))))
    }
  }
)

(defn create-state []
  {
    :type "stateEducationAgency",
    :_id (sha1 "NY"),
    :body
    {
      :stateOrganizationId "NY",
      :nameOfInstitution "New York State Board of Education",
      :organizationCategories ["State Education Agency"],
      :address (create-address)
    }
  }
)

(defn create-district [districtName]
  {
    :type "localEducationAgency",
    :_id (sha1 districtName),
    :body
    {
      :stateOrganizationId districtName,
      :nameOfInstitution districtName,
      :organizationCategories ["Local Education Agency"],
      :address (create-address),
      :parentEducationAgencyReference (sha1 "NY")
    }
  }
)

(defn create-school [districtName schoolName]
  {
    :type  "school",
    :_id (sha1 schoolName),
    :body 
    {
      :schoolCategories [ "Elementary School" ],
      :gradesOffered
      [
        "First grade",
        "Second grade",
	      "Third grade",
	      "Fourth grade",
	      "Fifth grade",
	      "Sixth grade"
      ],
      :organizationCategories ["School"],
      :stateOrganizationId schoolName,
      :nameOfInstitution schoolName,
      :address (create-address),
      :parentEducationAgencyReference (sha1 districtName)
    }
  }
)

(defn create-course-code [districtName]
  [
    {
      :ID (sha1 (course-id districtName) ),
      :identificationSystem "CSSC course code"
    }
  ]
)

(defn create-course [districtName]
  {
    :type "course",
    :_id (sha1 (course-id districtName) ),
    :body
    {
      :courseTitle "Math 7",
      :numberOfParts 1,
      :courseCode (create-course-code districtName),
      :schoolId (sha1 districtName)
    }
  }
)

(defn create-student-assessment-association [schoolName studentId assessmentName score date]
  {
    :type "studentAssessmentAssociation",
    :_id (sha1 (saa-id schoolName studentId date) ),
    :body
    {
      :administrationDate date,
      :studentId (sha1 (student-id schoolName studentId) ),
      :assessmentId (sha1 assessmentName),
      :scoreResults [ { :result (str score), :assessmentReportingMethod "Scale score" } ],
      :performanceLevelDescriptors [ { :codeValue (performance-level score) } ]
    }
  }
)

(defn create-student-school-association [schoolName studentId]
  {
    :type "studentSchoolAssociation",
    :_id (sha1 (format "%s-%s-%s" schoolName studentId "2011-09-01") ),
    :body
    {
      :studentId (sha1 (student-id schoolName studentId) ),
      :schoolId (sha1 schoolName),
      :entryDate "2011-09-01",
      :entryGradeLevel "Seventh grade"
    }
  }
)

(defn create-student-section-association [schoolName studentId]
  {
    :type "studentSectionAssociation",
    :_id (sha1 (student-id schoolName studentId) )
    :body
    {
      :studentId (sha1 (student-id schoolName studentId) ),
      :sectionId (sha1 (section-id schoolName) )
    }
  }
)

(defn create-assessment [assessmentName]
  {
    :type "assessment",
    :_id (sha1 assessmentName),
    :body
    {
      :assessmentTitle assessmentName,
      :assessmentIdentificationCode
      [
        { :identificationSystem "Test Contractor" ,
          :ID (sha1 assessmentName)
        }
      ],
  		:assessmentPerformanceLevel
  		[
  		  {
  		    :performanceLevelDescriptor [ { :codeValue "W" } ],
  			  :assessmentReportingMethod "Scale score",
       
  			  :minimumScore 6,
  			  :maximumScore 14
  		  },
  		  {
  		    :performanceLevelDescriptor [ { :codeValue "B" } ],
  			  :assessmentReportingMethod "Scale score",
  			  :minimumScore 15,
  			  :maximumScore 20
  		  },
  		  {
  		    :performanceLevelDescriptor [ { :codeValue "S" } ],
  			  :assessmentReportingMethod "Scale score",
  			  :minimumScore 21,
  			  :maximumScore 27
  		  },
  		  {
  		    :performanceLevelDescriptor [ { :codeValue "E" } ],
  			  :assessmentReportingMethod "Scale score",
  			  :minimumScore 28,
  			  :maximumScore 33
  		  }
  		]
    }
  }
)

(defn create-session [schoolName]
  {
    :type "session",
    :_id (sha1 (session-id schoolName) ),
    :body
    {
      :sessionName (session-id schoolName),
      :schoolYear "2011-2012",
      :term "Fall Semester",
      :beginDate "2011-09-06",
      :endDate "2011-12-16",
      :totalInstructionalDays 75
    }
  }
)

(defn create-calendar-date [date calendarEvent]
  {
    :type "calendarDate",
    :_id (sha1 (format "%s-%s" date calendarEvent) ),
    :body
    {
      :date date,
      :calendarEvent calendarEvent
    }
  }
)

(defn create-course-offering [districtName schoolName]
  {
    :type "courseOffering",
    :_id (sha1 (local-course-id districtName schoolName) ),
    :body
    {
      :localCourseCode (local-course-id districtName schoolName),
      :schoolId (sha1 schoolName),
      :courseId (sha1 (course-id districtName) ),
      :sessionId (sha1 (session-id schoolName) )
    }
  }
)

(defn create-section [districtName schoolName]
  {
    :_id (sha1 (section-id schoolName) ),
    :type "section",
    :body
    {
      :uniqueSectionCode (section-id schoolName),
      :sequenceOfCourse 1,
      :schoolId schoolName,
      :sessionId (sha1 (session-id districtName) ),
      :courseOfferingId (sha1 (local-course-id districtName schoolName) )
    }
  }
)

(defn gen-sessions [schools output-file]
  (doseq [schoolName schools]
    (gen-json "/tmp/test/session.json" (create-session schoolName))
  )
)
  
(defn gen-students [schools rng output-file]
  (doseq [schoolName schools]
    (doseq [id rng]
      (gen-json "/tmp/test/student.json" (create-student schoolName id))
    )
  )
)

(defn gen-state []
  (gen-json "/tmp/test/educationOrganization.json" (create-state))
)

(defn gen-district [districtName]
  (gen-json (format "/tmp/test/educationOrganization.json") (create-district districtName))
)

(defn gen-course [districtName]
  (gen-json "/tmp/test/course.json" (create-course districtName))
)

(defn gen-schools [districtName schools output-file]
  (gen-state)
  (gen-district districtName)
  (gen-course districtName)
  
  (doseq [schoolName schools]
    (gen-json "/tmp/test/educationOrganization.json" (create-school districtName schoolName))
  )
)

(defn gen-assessment [assessmentName output-file]
  (gen-json "/tmp/test/assessment.json" (create-assessment assessmentName))
)

(defn gen-student-assessment-associations [districtName schools students assessment n output-file]
  (doseq [schoolName schools]
    (doseq [studentId students]
      (doseq [i (range 1 n)]
        (gen-json "/tmp/test/studentAssessmentAssociation.json" 
          (create-student-assessment-association schoolName studentId assessment (rand-nth (range 6 33)) (str "2011-10-" (format "%02d" i)))
        )
      )
    )
  )
)
      
(defn gen-student-enrollments [schools rng output-file]
  (doseq [schoolName schools]
    (doseq [id rng]
      (gen-json "/tmp/test/studentSchoolAssociation.json" (create-student-school-association schoolName id))
      (gen-json "/tmp/test/studentSectionAssociation.json" (create-student-section-association schoolName id))
    )
  )
)  

(defn gen-sections [districtName schools output-file]
  (doseq [schoolName schools]
    (gen-json "/tmp/test/courseOffering.json" (create-course-offering districtName schoolName))
    (gen-json "/tmp/test/section.json" (create-section districtName schoolName))
  )
)

(defn create-control-file [] )
