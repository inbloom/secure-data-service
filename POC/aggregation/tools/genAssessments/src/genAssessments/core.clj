(ns genAssessments.core
  (:use clojure.data.xml)
  (:use clojure.contrib.math)
  (:use [clojure.string :only (join)])
  (:use [clojure.contrib.string :only (substring?)])
)

(defn gen-edfi
  [interchange output-file contents]
  (prn output-file)
  (with-open [out (java.io.OutputStreamWriter. (java.io.FileOutputStream. output-file) "UTF-8")]
    (emit (element interchange {:xmlns "http://ed-fi.org/0100"} contents) out)
  )
)

(defn gen-student [schoolName studentId]
  (element :Student {:id (format "%s-%s" schoolName (str studentId))}
    (element :StudentUniqueStateId {} (format "%s-%s" schoolName (str studentId)))
    (element :Name {}
      (element :FirstName {} (rand-nth ["Nathan" "Gina" "Alan" "Morena" "Adam" "Jewel" "Sean" "Summer" "Ron"]))
      (element :LastSurname {} (rand-nth ["Fillion" "Torres" "Tudyk" "Baccarin" "Baldwin" "Staite" "Maher" "Glau" "Glass"]))
    )
    (element :Sex {} (rand-nth ["Male" "Female"]))
    (element :BirthData {}
      (element :BirthDate {} (str "2001-" (format "%02d" (inc (rand-int 12))) "-" (format "%02d" (inc (rand-int 28)))))
    )
    (element :HispanicLatinoEthnicity {} (rand-nth ["true" "false"]))
    (element :Race {})
  )
)

(defn gen-students [schools rng output-file]
  (gen-edfi :InterchangeStudentParent output-file
    (into ()
      [
        (for [schoolName schools]
          (for [id rng
            :let [tmp (gen-student schoolName id)]
            ]
            tmp
          )
        )
      ]
    )
  )
)

(defn create-state []
  (let [tmp (element :StateEducationAgency {:id "NY"}
    (element :StateOrganizationId {} "NY")
    (element :NameOfInstitution {} "New York State Board of Education")
    (element :OrganizationCategories {}
      (element :OrganizationCategory {} "State Education Agency")
    )
    (element :Address {}
      (element :StreetNumberName {} "123 Street")
      (element :City {} "Albany")
      (element :StateAbbreviation {} "NY")
      (element :PostalCode {} "11011")
    )
  )]
  tmp
  )
)

(defn create-district [districtName]
  (let [tmp (element :LocalEducationAgency {:id districtName}
    (element :StateOrganizationId {} districtName)
    (element :NameOfInstitution {} districtName)
    (element :OrganizationCategories {}
      (element :OrganizationCategory {} "Local Education Agency")
    )
    (element :Address {}
      (element :StreetNumberName {} "123 Street")
      (element :City {}  districtName)
      (element :StateAbbreviation {} "NY")
      (element :PostalCode {} "11011")
    )
    (element :LEACategory {} "Independent")
    (element :StateEducationAgencyReference {:ref "NY"})
    )]
    tmp
  )
)

(defn create-school [districtName, schoolName]
  (let [tmp (element :School {:id schoolName}
    (element :StateOrganizationId {} schoolName)
    (element :EducationOrgIdentificationCode {:IdentificationSystem "School"}
      (element :ID {} schoolName)
    )
    (element :NameOfInstitution {} schoolName)
    (element :OrganizationCategories {}
      (element :OrganizationCategory {} "School")
    )
    (element :Address {:AddressType "Physical"}
      (element :StreetNumberName {} "123 Street")
      (element :City {} "Albany")
      (element :StateAbbreviation {} "NY")
      (element :PostalCode {} "11011")
      (element :NameOfCounty {} districtName)
    )
    (element :Telephone {:InstitutionTelephoneNumberType "Main"}
      (element :TelephoneNumber {} "(425)-555-1212" )
    )
    (element :GradesOffered {}
      (element :GradeLevel {} "First grade")
      (element :GradeLevel {} "Second grade")
      (element :GradeLevel {} "Third grade")
      (element :GradeLevel {} "Fourth grade")
      (element :GradeLevel {} "Fifth grade")
      (element :GradeLevel {} "Sixth grade")
    )
    (element :SchoolCategories {}
      (element :SchoolCategory {} "Elementary School")
    )
    (element :LocalEducationAgencyReference {:ref districtName})
  )]
  tmp
  )
)

(defn create-courses [districtName]
  (let [tmp (element :Course {:id (format "Math007-%s" districtName)}
    (element :CourseTitle {} "Math007")
    (element :NumberOfParts {} "1")
    (element :CourseCode {:IdentificationSystem "CSSC course code"}
        (element :ID {} (format "Math007-%s" districtName))
    )
    (element :CourseDescription {} "7th grade math")
    (element :EducationOrganizationReference {}
      (element :EducationalOrgIdentity {}
        (element :StateOrganizationId {} districtName)
      )
    )
    )]
    tmp
  )
)

(defn gen-schools [districtName schools output-file]
  (gen-edfi :InterchangeEducationOrganization output-file
    (reverse
      (into ()
        [
          (create-state)
          (create-district districtName)
          (for [schoolName schools
            :let [tmp (create-school districtName schoolName)]]
            tmp
          )
          (create-courses districtName)
        ]
      )
    )
  )
)

(defn create-assessment [assessmentName]
  (let [tmp (element :Assessment {:id "Grade_7_2011_State_Math"}
      (element :AssessmentTitle {} assessmentName)
      (element :AssessmentIdentificationCode {:IdentificationSystem "Test Contractor"}
        (element :ID {} assessmentName)
      )
      (element :AssessmentCategory {} "State summative assessment 3-8 general")
      (element :AcademicSubject {} "Mathematics")
      (element :GradeLevelAssessed {} "Seventh grade")
      (element :AssessmentPerformanceLevel {}
        (element :PerformanceLevel {}
          (element :CodeValue {} "W")
        )
        (element :AssessmentReportingMethod {} "Scale score")
        (element :MinimumScore {} "6")
        (element :MaximumScore {} "14")
      )
      (element :AssessmentPerformanceLevel {}
        (element :PerformanceLevel {}
          (element :CodeValue {} "B")
        )
        (element :AssessmentReportingMethod {} "Scale score")
        (element :MinimumScore {} "15")
        (element :MaximumScore {} "20")
      )
      (element :AssessmentPerformanceLevel {}
        (element :PerformanceLevel {}
          (element :CodeValue {} "S")
        )
        (element :AssessmentReportingMethod {} "Scale score")
        (element :MinimumScore {} "21")
        (element :MaximumScore {} "27")
      )
      (element :AssessmentPerformanceLevel {}
        (element :PerformanceLevel {}
          (element :CodeValue {} "E")
        )
        (element :AssessmentReportingMethod {} "Scale score")
        (element :MinimumScore {} "28")
        (element :MaximumScore {} "33")
      )
      (element :ContentStandard {} "State Standard")
      (element :Version {} "1")
      (element :RevisionDate {} "2011-03-12")
      (element :MaxRawScore {} "33")
    )]
    tmp
  )
)

(defn gen-assessment [assessmentName output-file]
  (gen-edfi :InterchangeAssessmentMetadata output-file (create-assessment assessmentName))
)

(defn get-perf-level [score]
  (cond
    (> 14 score) "W"
    (> 20 score) "B"
    (> 27 score) "S"
    (> 33 score) "E")
)

(defn student-ref [schoolName studentId]
  (element :StudentReference {}
    (element :StudentIdentity {}
      (element :StudentUniqueStateId {} (format "%s-%s" schoolName (str studentId)))
    )
  )
)

(defn gen-saa [schoolName studentId assessmentName date]
  (let [score (rand-nth (range 6 33))]
    (element :StudentAssessment {:id (format "SAA-%s-%s-%s" schoolName (str studentId) date)}
      (element :AdministrationDate {} date)
      (element :AdministrationEndDate {} date)
      (element :SerialNumber {} "2")
      (element :ScoreResults {:AssessmentReportingMethod "Scale score"}
        (element :Result {} (str score))
      )
      (element :PerformanceLevels {}
        (element :CodeValue {} (get-perf-level score))
      )
      (student-ref schoolName studentId)
      (element :AssessmentReference {}
        (element :AssessmentIdentity {}
          (element :AssessmentIdentificationCode {:IdentificationSystem "Test Contractor"}
            (element :ID {} assessmentName)
          )
        )
      )
    )
  )
)

(defn gen-saas
  [districtName schools students assessment n output-file]
  (gen-edfi :InterchangeStudentAssessment output-file
    (for [schoolName schools]
      (for [studentId students, i (range 1 n)]
        (gen-saa schoolName studentId assessment (str "2011-10-" (format "%02d" i)))
      )
    )
  )
)

(defn gen-enroll
  [schoolName studentId]
  (element :StudentSchoolAssociation {}
    (student-ref schoolName studentId)
    (element :SchoolReference {}
      (element :EducationalOrgIdentity {}
        (element :StateOrganizationId {} schoolName)
      )
    )
    (element :EntryDate {} "2011-09-01")
    (element :EntryGradeLevel {} "Seventh grade")
  )
)

(defn gen-section-assoc
  [schoolName studentId]
  (element :StudentSectionAssociation {}
    (student-ref schoolName studentId)
    (element :SectionReference {}
      (element :SectionIdentity {}
        (element :StateOrganizationId {} schoolName)
        (element :UniqueSectionCode {}  (format "%s-Math-7-2011-Sec2" schoolName))
      )
    )
    (element :BeginDate {} "2011-09-06")
    (element :EndDate {} "2011-12-16")
  )
)

(defn enroll [schoolName studentId]
  (gen-enroll schoolName studentId)
  (gen-section-assoc schoolName studentId)
)

(defn gen-enrollments [schools rng output-file]
  (gen-edfi :InterchangeStudentEnrollment output-file
    (for [schoolName schools]
      (for [id rng]
        (enroll schoolName id)
      )
    )
  )
)

(defn create-session [districtName schoolName]
  (into () [
    (element :CalendarDate {:id (format "%s_day" districtName) }
      (element :Date {} "2011-09-22")
      (element :CalendarEvent {} "Instructional day")
    )

    (element :GradingPeriod {:id (format "%s-GP1" districtName)}
      (element :GradingPeriodIdentity {}
        (element :GradingPeriod {} "End of Year")
        (element :SchoolYear {} "2011-2012")
        (element :StateOrganizationId {} districtName)
      )

      (element :BeginDate {} "2011-09-01")
      (element :EndDate {} "2011-12-01")
      (element :TotalInstructionalDays {} "90")
      (element :CalendarDateReference {:ref (format "%s_day" districtName)})
    )

    (element :Session {}
      (element :SessionName {} (format "%s-Fall-2011" schoolName))
      (element :SchoolYear {} "2011-2012")
      (element :Term {} "Fall Semester")
      (element :BeginDate {} "2011-09-06")
      (element :EndDate {} "2011-12-16")
      (element :TotalInstructionalDays {} "75")
      (element :EducationOrganizationReference {}
        (element :EducationalOrgIdentity {}
          (element :StateOrganizationId {} schoolName)
        )
      )
      (element :GradingPeriodReference {:ref (format "%s-GP1" districtName)})
      (element :CalendarDateReference {:ref (format "%s_day" districtName)})
    ) ]
  )
)

(defn gen-sessions [districtName schools output-file]
  (gen-edfi :InterchangeEducationOrgCalendar output-file
    (for [schoolName schools]
      (reverse (create-session districtName schoolName))
    )
  )
)

(defn create-section [districtName schoolName]
  (into () [
    (element :CourseOffering {}
      (element :LocalCourseCode {} (format "%s-Math-7" schoolName))
      (element :LocalCourseTitle {} "7th Grade Math")
      (element :SchoolReference {}
        (element :EducationalOrgIdentity {}
          (element :StateOrganizationId {} schoolName)
        )
      )
      (element :SessionReference {}
        (element :SessionIdentity {}
          (element :SessionName {} (format "%s-Fall-2011" schoolName))
        )
      )
      (element :CourseReference {}
        (element :CourseIdentity {}
          (element :CourseCode {:IdentificationSystem "CSSC course code"}
            (element :ID {} (format "Math007-%s" districtName))
          )
        )
      )
    )

    (element :Section {}
      (element :UniqueSectionCode {} (format "%s-Math-7-2011-Sec2" schoolName))
      (element :SequenceOfCourse {} "1")
      (element :CourseOfferingReference {}
        (element :CourseOfferingIdentity {}
          (element :LocalCourseCode {} (format "%s-Math-7" schoolName))
          (element :CourseCode {:IdentificationSystem "CSSC course code"}
            (element :ID {} (format "Math007-%s" districtName))
          )
          (element :Term {} "Fall Semester")
          (element :SchoolYear {} "2011-2012")
          (element :StateOrganizationId {} schoolName)
        )
      )
      (element :SchoolReference {}
        (element :EducationalOrgIdentity {}
          (element :StateOrganizationId {} schoolName)
        )
      )
      (element :SessionReference {}
        (element :SessionIdentity {}
          (element :SessionName {} (format "%s-Fall-2011" schoolName))
        )
      )
    ) ]
  )
)

(defn gen-sections [districtName schools output-file]
  (gen-edfi :InterchangeMasterSchedule output-file
    (for [schoolName schools]
      (reverse (create-section districtName schoolName))
    )
  )
)


(def districts
    ["Abbott","Addison","Adirondack","Afton","Akron","Albany-City","Albion","Alden","Alexander","Alexandria","Alfred",
     "Allegany","Altmar-Parish","Amagansett","Amherst","Amityville","Amsterdam","Andes","Andover",
     "Ardsley","Argyle","Arkport","Arlington","Attica","Auburn-City","Ausable-Valley","Averill-Park","Avoca","Avon","Babylon",
     "Bainbridge","Baldwin","Baldwinsville","Ballston","Barker","Batavia","Bath","Bay-Shore","Bayport-Blue",
     "Beacon-City","Beaver-River","Bedford","Beekmantown","Belfast","Belleville","Bellmore","Bellmore-Merrick",
     "Bemus","Berkshire","Berlin","Berne-Knox","Bethlehem","Bethpage","Binghamton","Brook-Rye",
     "Bolivar","Bolton","Bradford","Brasher","Brentwood","Brewster","Briarcliff","Bridgehampton","Brighton",
     "Broadalbin","Brockport","Brocton","Bronxville","Brookfield","Brookhaven","Broome",
     "Brunswic","Brushton","Buffalo","Burnt-Hills","Byram Hills","Byron-Bergen","Cairo-Durham",
     "Caledonia","Cambridge","Camden","Campbell","Canajoharie","Canandaigua","Canaseraga","Canastota",
     "Candor","Canisteo","Canton","Capital","Carle-Place","Carmel","Carthage","Cassadaga",
     "Cato-Meridian","Catskill","Cattar-Allegany","Cattaraugus","Cayuga",
     "Cazenovia","Center","Central-Islip","Central-Square","Chappaqua","Charlotte","Chateaugay","Chatham",
     "Chautauqua","Chazy","Cheektowaga","Maryvale","Sloan","Chenango-Forks","Chenango",
     "Cherry-Valley","Chester","Chittenango","Churchville","Cincinnatus","Clarence","Clarkstown","Cleveland",
     "Clifton-Fin","Clinton","Clinton-Essex","Clyde","Clymer","Cobleskill","Cohoes-City",
     "Cold-Spring","Colton","Commack","Connetquot","Cooperstown","Copenhagen","Copiague","Corinth","Corning-City",
     "Cornwall","Cortland-City","Coxsackie","Croton-Harmon","Crown-Point","Cuba-Rushford","Dalton-Nunda",
     "Dansville","Deer-Park","Delaware","Delaw-","Depew","Deposit","Deruyter",
     "Dobbs-Ferry","Dolgeville","Dover","Downsville","Dryden","Duanesburg","Dundee","Dunkirk-City","Dutchess","East-Aurora",
     "East-Bloomfield","East-Greenbush","East-Hampton","East-Irondequoit","East-Islip","East-Meadow","East-Moriches",
     "East-Quogue","East-Ramapo","East-Rochester","East-Rockaway","East-Syracuse","East-Williston",
     "Eastchester","Eastern-Suffolk","Eastport-South","Eden","Edgemont","Edinburg-Common","Edmeston","Edwards-Knox",
     "Elba","Eldred","Elizabethtown","Ellenville","Ellicottville","Elmira-City","Elmira-Heights","Elmont","Elmsford",
     "Elwood","Erie","Erie-2","Evans-Brant","Fabius-Pompey","Fairport",
     "Falconer","Fallsburg","Farmingdale","Fayetteville","Fillmore","Fire-Island","Fishers-Island","Floral-Park",
     "Florida","Fonda","Forestville","Fort-Ann","Fort-Edward","Fort-Plain","Frankfort","Franklin",
     "Garden-City","Garrison","Gates-Chili","General-Brown","Genesee-Valley","Geneseo","Geneva-City",
     "Hadley-Luzerne","Haldane","Half-Hollow","Hamburg","Hamilton","Hamilton","Hammond",
     "Homer","Honeoye","Honeoye","Hoosic-Valley","Hoosick-Falls","Hopevale","Hornell-City",
     "Inlet-Comn","Iroquois","Irvington","Island-Park","Island-Trees","Islip","Ithaca-City","Jamestown-City","Jamesville",
     "Keene","Kendall","Kenmore","Kinderhook","Kings-Park","Kingston-City","Kiryas","La-Fargeville",
     "Livonia","Lockport-City","Locust-Valley","Long-Beach","Long-Lake","Longwood","Lowville-Academy","Lyme",
     "Middletown","1 - Manhattan","2- Manhattan","3 - Manhattan",
     "4 - Manhattan","5 - Manhattan","6 - Manhattan","7 - Bronx",
     "8 - Bronx","9 - Bronx","10 - Bronx","11 - Bronx",
     "12 - Bronx","13 - Brooklyn","14 - Brooklyn","15 - Brooklyn",
     "16 - Brooklyn","17 - Brooklyn","18 - Brooklyn","19 - Brooklyn",
     "20 - Brooklyn","21 - Brooklyn","22 - Brooklyn","23 - Brooklyn",
     "24 - Queens","25 - Queens","26 - Queens","27 - Queens",
     "31 - Staten Island","32 - Brooklyn","NYC Spec Schools","Oakfield",
     "Ossining","Oswego","Oswego-City","Otego-Unadilla","Otsego-Delaw","Owego-Apalachin",
     "Panama","Parishville","Patchogue","Pavilion","Pawling","Pearl-River","Peekskill","Poland",
     "Randolph","Raquette","Ravena","Red-Creek","Red-Hook","Remsen","Remsenburg",
     "Salem","Salmon","Sandy-Creek","Saranac","Saranac-Lake","Saratoga-Springs","Saugerties","Sauquoit-Valley" ] )

(defn gen-district-schools [districtCount schoolsPerDistrict studentsPerSchool]
  ; should distribute this in a more normal distribution
  (for [i (range 1 (+ districtCount 1))
    :let [r (assoc (hash-map) (districts i)
      (for [j (range 1 (+ 1 schoolsPerDistrict))]
        (format "PS-%s" (str (+ i j)))))]]
   r)
)

(defn md5 [file]
  (let [input (java.io.FileInputStream. file)
    digest (java.security.MessageDigest/getInstance "MD5")
    stream (java.security.DigestInputStream. input digest) bufsize (* 1024 1024) buf (byte-array bufsize)]
    (while (not= -1 (.read stream buf 0 bufsize)))
    (apply str (map (partial format "%02x") (.digest digest)))
  )
)

(defn get-md5-for-file [file]
  (def md5string (md5 file))
  (def filename (.getName file))
  (def formatString (str "edfi-xml,%s,%s,%s"))
  (let [rval (str
      (if (substring? "-assessment.xml" filename)
        (format formatString "AssessmentMetadata" filename md5string)
      )
      (if (substring? "-schools.xml" filename)
        (format formatString "EducationOrganization" filename md5string)
      )
      (if (substring? "-calendar.xml" filename)
        (format formatString "EducationOrgCalendar" filename md5string)
      )
      (if (substring? "-master-schedule.xml" filename)
        (format formatString "MasterSchedule" filename md5string)
      )
      (if (substring? "student.xml" filename)
        (format formatString "StudentParent" filename, md5string)
      )
      (if (substring? "-enrollment.xml" filename)
        (format formatString "StudentEnrollment" filename md5string)
      )
      (if (substring? "-assessment-results.xml" filename)
        (format formatString "StudentAssessment" filename md5string)
      )
      ; need
      ; edfi-xml,StaffAssociation,InterchangeStaffAssociation.xml,c5efbe159ac926629a3b494460243aba
    )]
    (str rval)
  )
)

(defn create-control-file []
  (def directory (clojure.java.io/file "/tmp/test"))
  (def files (rest (file-seq directory)))
  (with-open [out (java.io.PrintWriter. (java.io.FileOutputStream. "/tmp/test/MainControlFile.ctl"))]
    (doseq [file files]
      (def tmp (get-md5-for-file file))
      (.println out tmp)
    )
  )
)

(defn gen-big-data
  [districtCount schoolCount studentCount]
  (def sectionName "7th Grade Math - Sec 2")
  (def assessmentName "Grade 7 State Math")

  (gen-assessment assessmentName "/tmp/test/F-assessment.xml")
  (def rng (range 1 (+ 1 studentCount)))
  (doseq [ [district] (map list (gen-district-schools districtCount schoolCount studentCount))]
    (doseq [ [districtName schools] district]
      (gen-schools districtName schools (format "/tmp/test/A-%s-schools.xml" districtName))
      (gen-sessions districtName schools (format "/tmp/test/B-%s-calendar.xml" districtName))
      (gen-sections districtName schools (format "/tmp/test/C-%s-master-schedule.xml" districtName))
      (gen-students schools rng (format "/tmp/test/D-%s-student.xml" districtName))
      (gen-enrollments schools rng (format "/tmp/test/E-%s-enrollment.xml" districtName))
      (gen-saas districtName schools rng assessmentName 10 (format "/tmp/test/G-%s-assessment-results.xml" districtName))
    )
  )
  (create-control-file)
)

; 100 students
(defn gen-tiny-set []
  (gen-big-data 1 1 100)
)

; 2500 students
(defn gen-small-set []
  (gen-big-data 1 5 500)
)

; 42000 students
(defn gen-medium-set []
  (gen-big-data 6 7 1000)
)

; 500k students
(defn gen-medium-large-set []
  (gen-big-data 20 10 2500)
)

; 1.5 million students
(defn gen-large-set []
  (gen-big-data 25 25 2500)
)

; 15 million students
(defn gen-extra-large-set []
  (gen-big-data 125 50 2500)
)
