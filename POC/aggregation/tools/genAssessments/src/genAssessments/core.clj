(ns genAssessments.core
  (:use clojure.data.xml))

(defn gen-student
  [id]
  (element :Student {}
           (element :StudentUniqueStateId {} (str id))
           (element :Name {}
                    (element :FirstName {} (rand-nth ["Nathan" "Gina" "Alan" "Morena" "Adam" "Jewel" "Sean" "Summer" "Ron"]))
                    (element :LastSurname {} (rand-nth ["Fillion" "Torres" "Tudyk" "Baccarin" "Baldwin" "Staite" "Maher" "Glau" "Glass"])))
           (element :Sex {} (rand-nth ["Male" "Female"]))
           (element :BirthData {}
                    (element :BirthDate {} (str "2001-" (format "%02d" (inc (rand-int 12))) "-" (format "%02d" (inc (rand-int 28))))))
           (element :HispanicLatinoEthnicity {} (rand-nth ["true" "false"]))
           (element :Race {})))

(defn get-perf-level
  [score]
  (cond
    (> 14 score) "W"
    (> 20 score) "B"
    (> 27 score) "S"
    (> 33 score) "E"))

(defn student-ref [student]
  (element :StudentReference {}
           (element :StudentIdentity {}
                    (element :StudentUniqueStateId {} (str student)))))


(defn gen-saa
  [student assessment date]
  (let [score (rand-nth (range 6 33))]
    (element :StudentAssessment {}
             (element :AdministrationDate {} date)
             (element :ScoreResults {:AssessmentReportingMethod "Scale score"}
                      (element :Result {} (str score)))
             (element :PerformanceLevels {}
                      (element :CodeValue {} (get-perf-level score)))
             (student-ref student)
             (element :AssessmentReference {}
                      (element :AssessmentIdentity {}
                               (element :AssessmentIdentificationCode {:IdentificationSystem "Test Contractor"}
                                        (element :ID {} assessment)))))))

(defn gen-enroll
  [student edorg]
  (element :StudentSchoolAssociation {}
           (student-ref student)
           (element :SchoolReference {}
                    (element :EducationalOrgIdentity {}
                             (element :StateOrganizationId {} (str edorg))))
           (element :EntryDate {} "2011-09-01")
           (element :EntryGradeLevel {} "Seventh grade")))

(defn gen-section-assoc
  [student section edorg]
  (element :StudentSectionAssociation {}
           (student-ref student)
           (element :SectionReference {}
                    (element :SectionIdentity {}
                             (element :StateOrganizationId {} edorg)
                             (element :UniqueSectionCode {} section)))))

(defn gen-edfi
  [interchange output-file contents]
  (with-open [out (java.io.OutputStreamWriter. (java.io.FileOutputStream. output-file) "UTF-8")]
    (emit
      (element interchange {:xmlns "http://ed-fi.org/0100"}
               contents)
      out)))

(defn gen-students
  [ids output-file]
  (gen-edfi :InterchangeStudentParent output-file (map gen-student ids)))

(defn gen-saas
  [students assessment n output-file]
  (gen-edfi :InterchangeStudentAssessment output-file
            (for
              [s students, i (range 1 n)]
              (gen-saa s assessment (str "2011-10-" (format "%02d" i))))))

(defn gen-enrollments
  [students edorg output-file]
  (gen-edfi :InterchangeStudentEnrollment output-file
            (map #(gen-enroll % edorg) students)))

(defn gen-section-enrollments
  [students edorg section output-file]
  (gen-edfi :InterchangeStudentEnrollment output-file
            (map #(gen-section-assoc % section edorg) students)))

(def district-schools {
    :Adams
      {:PS101 (range 1 2500)
       :PS102 (range 2501 5000)
       :PS103 (range 5001 7500)
       :PS104 (range 7501 10000)
       :PS105 (range 10001 12500)
       :PS106 (range 12501 15000)
       :PS107 (range 15001 17500)
       :PS108 (range 17501 20000)
       :PS109 (range 20001 22500)
       :PS110 (range 22501 25000)}
    :Clay
      {:PS201 (range 25001 27500)
       :PS202 (range 27501 30000)
       :PS203 (range 30001 32500)
       :PS204 (range 32501 35000)
       :PS205 (range 35001 37500)
       :PS206 (range 37501 40000)
       :PS207 (range 40001 42500)
       :PS208 (range 42501 45000)
       :PS209 (range 45001 47500)
       :PS210 (range 47501 50000)}
    :Fayette
      {:PS301 (range 50001 52500)
       :PS302 (range 52501 55000)
       :PS303 (range 55001 57500)
       :PS304 (range 57501 60000)
       :PS305 (range 60001 62500)
       :PS306 (range 62501 65000)
       :PS307 (range 65001 67500)
       :PS308 (range 67501 70000)
       :PS309 (range 70001 72500)
       :PS310 (range 72501 75000) }
    :Greene
      {:PS401 (range 75001 77500)
       :PS402 (range 77501 80000)
       :PS403 (range 80001 82500)
       :PS404 (range 82501 85000)
       :PS405 (range 85001 87500)
       :PS406 (range 87501 90000)
       :PS407 (range 90001 92500)
       :PS408 (range 92501 95000)
       :PS409 (range 95001 97500)
       :PS410 (range 97501 100000) }
    :Lake
      {:PS501 (range 100001 102500)
       :PS502 (range 102501 105000)
       :PS503 (range 105001 107500)
       :PS504 (range 107501 110000)
       :PS505 (range 110001 112500)
       :PS506 (range 112501 115000)
       :PS507 (range 115001 117500)
       :PS508 (range 117501 120000)
       :PS509 (range 120001 122500)
       :PS510 (range 122501 125000) }
    :Marion
      {:PS601 (range 125001 127500)
       :PS602 (range 127501 130000)
       :PS603 (range 130001 132500)
       :PS604 (range 132501 135000)
       :PS605 (range 135001 137500)
       :PS606 (range 137501 140000)
       :PS607 (range 140001 142500)
       :PS608 (range 142501 145000)
       :PS609 (range 145001 147500)
       :PS610 (range 147501 150000) }
    :Putnam
      {:PS701 (range 150001 152500)
       :PS702 (range 152501 155000)
       :PS703 (range 155001 157500)
       :PS704 (range 157501 160000)
       :PS705 (range 160001 162500)
       :PS706 (range 162501 165000)
       :PS707 (range 165001 167500)
       :PS708 (range 167501 170000)
       :PS709 (range 170001 172500)
       :PS710 (range 172501 175000) }
    :Rock_Island
      {:PS801 (range 175001 177500)
       :PS802 (range 177501 180000)
       :PS803 (range 180001 182500)
       :PS804 (range 182501 185000)
       :PS805 (range 185001 187500)
       :PS806 (range 187501 190000)
       :PS807 (range 190001 192500)
       :PS808 (range 192501 195000)
       :PS809 (range 195001 197500)
       :PS810 (range 197501 200000) }
    :Wabash
      {:PS901 (range 200001 202500)
       :PS902 (range 202501 205000)
       :PS903 (range 205001 207500)
       :PS904 (range 207501 210000)
       :PS905 (range 210001 212500)
       :PS906 (range 212501 215000)
       :PS907 (range 215001 217500)
       :PS908 (range 217500 220000)
       :PS909 (range 220001 222500)
       :PS910 (range 222501 225000) }
    :Winnebago
      {:PS1001 (range 225001 227500)
       :PS1002 (range 227501 230000)
       :PS1003 (range 230001 232500)
       :PS1004 (range 232501 235000)
       :PS1005 (range 235001 237500)
       :PS1006 (range 237501 240000)
       :PS1007 (range 240001 242500)
       :PS1008 (range 242501 245000)
       :PS1009 (range 245001 247500)
       :PS1010 (range 247501 250000) }
    }
)

(defn gen-big-data
  []
  (doseq [[district schools] district-schools]
    (doseq [[school rng] schools]
      (gen-students rng (format "/tmp/test/student-%s.xml" (name school) ) )
      (gen-saas rng "Grade 7 State Math" 6 (format "/tmp/test/assessment-results-%s.xml" (name school)))
      (gen-enrollments rng (name school) (format "/tmp/test/enrollment-%s.xml" (name school)))
      (gen-section-enrollments rng (name school) "7th Grade Math - Sec 2" (format "/tmp/test/sections-%s.xml" (name school)))
    )
  )
)
