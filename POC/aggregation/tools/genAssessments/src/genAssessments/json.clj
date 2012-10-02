(ns genAssessments.json
  (:use clojure.contrib.math)
  (:use clojure.data.json)
  (:use genAssessments.defines)
  (:require [clojure.java.io :as io])
  (:use [monger.core :only [connect! connect set-db! get-db]]
        [monger.collection :only [insert insert-batch]])
  (:use [monger.result :only [ok? has-error?]])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(connect!)
(set-db! (get-db "sli"))

(defn sha1 [id]
  (apply str
    (map (partial format "%02x") (.digest (doto (java.security.MessageDigest/getInstance "SHA1").reset(.update(.getBytes id)))))
  )
)

(defn gen-json [output-file contents]
  (def entity (merge contents { :metaData { :tenantId "Hyrule" } } ) )
  (try (insert output-file entity)
    (catch Exception e
      (println (format "%s\n\n%s" entity e)),
      (.System.exit 0)
    )
  )
)

(defn create-name-json []
  {
    :firstName (rand-nth first-names),
    :lastSurname (rand-nth last-names)
  }
)

(defn create-address-json []
  {
   :streetNumberName "123 Elm Street",
   :city "NY",
   :stateAbbreviation "NY",
   :postalCode "11011"
  }
)

(defn create-result-json [result]
  {
    :result result,
    :assessmentReportingMethod "Scale score"
  }
)

(defn create-student-json [schoolName studentId]
  {
    :type "student",
    :_id (sha1 (student-id schoolName studentId)),
    :body
    {
      :studentUniqueStateId (student-id schoolName studentId),
      :name (create-name-json),
      :sex (rand-nth ["Male" "Female"])
      :birthData (str "2001-" (format "%02d" (inc (rand-int 12))) "-" (format "%02d" (inc (rand-int 28))))
    }
  }
)

(defn create-state-json []
  {
    :type "stateEducationAgency",
    :_id (sha1 "NY"),
    :body
    {
      :stateOrganizationId "NY",
      :nameOfInstitution "New York State Board of Education",
      :organizationCategories ["State Education Agency"],
      :address (create-address-json)
    }
  }
)

(defn create-district-json [districtName]
  {
    :type "localEducationAgency",
    :_id (sha1 districtName),
    :body
    {
      :stateOrganizationId districtName,
      :nameOfInstitution districtName,
      :organizationCategories ["Local Education Agency"],
      :address (create-address-json),
      :parentEducationAgencyReference (sha1 "NY")
    }
  }
)

(defn create-school-json [districtName schoolName]
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
      :address (create-address-json),
      :parentEducationAgencyReference (sha1 districtName)
    }
  }
)

(defn create-course-code-json [districtName]
  [
    {
      :ID (sha1 (course-id districtName) ),
      :identificationSystem "CSSC course code"
    }
  ]
)

(defn create-course-json [districtName]
  {
    :type "course",
    :_id (sha1 (course-id districtName) ),
    :body
    {
      :courseTitle "Math 7",
      :numberOfParts 1,
      :courseCode (create-course-code-json districtName),
      :schoolId (sha1 districtName)
    }
  }
)

(defn create-student-assessment-association-json [schoolName studentId assessmentName score date]
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

(defn create-student-school-association-json [schoolName studentId]
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

(defn create-student-section-association-json [schoolName studentId]
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

(defn create-assessment-json [assessmentName]
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

(defn create-session-json [schoolName]
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

(defn create-calendar-date-json [date calendarEvent]
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

(defn create-course-offering-json [districtName schoolName]
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

(defn create-section-json [districtName schoolName]
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

(defn gen-sessions-json [districtName schools]
  (doseq [schoolName schools]
    (gen-json "session" (create-session-json schoolName))
  )
)

(defn gen-students-json [districtName schools rng]
  (doseq [schoolName schools]
    (doseq [id rng]
      (gen-json "student" (create-student-json schoolName id))
    )
  )
)

(defn gen-state-json []
  (gen-json "educationOrganization" (create-state-json))
)

(defn gen-district-json [districtName]
  (gen-json (format "educationOrganization") (create-district-json districtName))
)

(defn gen-course-json [districtName]
  (gen-json "course" (create-course-json districtName))
)

(defn gen-schools-json [districtName schools]
  (gen-district-json districtName)
  (gen-course-json districtName)

  (doseq [schoolName schools]
    (gen-json "educationOrganization" (create-school-json districtName schoolName))
  )
)

(defn gen-assessment-json [assessmentName]
  (gen-json "assessment" (create-assessment-json assessmentName))
)

(defn gen-student-assessment-associations-json [districtName schools students assessment n]
  (doseq [schoolName schools]
    (doseq [studentId students]
      (doseq [i (range n)]
        (gen-json "studentAssessmentAssociation"
          (create-student-assessment-association-json schoolName studentId assessment (rand-nth (range 6 33)) (str "2011-10-" (format "%02d" i)))
        )
      )
    )
  )
)

(defn gen-student-enrollments-json [districtName schools rng]
  (doseq [schoolName schools]
    (doseq [id rng]
      (gen-json "studentSchoolAssociation" (create-student-school-association-json schoolName id))
      (gen-json "studentSectionAssociation" (create-student-section-association-json schoolName id))
    )
  )
)

(defn gen-sections-json [districtName schools]
  (doseq [schoolName schools]
    (gen-json "courseOffering" (create-course-offering-json districtName schoolName))
    (gen-json "section" (create-section-json districtName schoolName))
  )
)

(defn create-control-file-json [] )
