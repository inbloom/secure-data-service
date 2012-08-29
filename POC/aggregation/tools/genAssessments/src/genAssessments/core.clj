(ns genAssessments.core
  (:use clojure.data.xml))

(defn gen-edfi
  [interchange output-file contents]
  (with-open [out (java.io.OutputStreamWriter. (java.io.FileOutputStream. output-file) "UTF-8")]
    (emit
      (element interchange {:xmlns "http://ed-fi.org/0100"}
               contents)
      out)))

(defn gen-student [id]
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


(defn gen-saa [student assessment date]
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

(defn create-school [school]
      (def schoolName (name (key school)))
      (element :School {}
           (element :StateOrganizationId {} schoolName)
           (element :EducationOrgIdentificationCode {}
               (element :IdentificationSystem {} "School")
               (element :ID {} schoolName))
           (element :NameOfInstitution {} schoolName)
           (element :OrganizationCategories {}
               (element :OrganizationCategory {} "School"))
           (element :SchoolCategories {}
               (element :schoolCategory {} "Elementary School"))
           (element :LocalEducationAgencyReference {}
               (element :EducationalOrgIdentity {}
                   (element :StateOrganizationId {} "Test District")))))

(defn gen-schools
  [district schools output-file]
  (gen-edfi :InterchangeEducationOrganization output-file (map create-school schools)))

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
      {:PS101 (range 1 250000)
       :PS102 (range 250001 500000)
       :PS103 (range 500001 750000)
       :PS104 (range 750001 1000000)
       :PS105 (range 1000001 1205000)
       :PS106 (range 1250001 1500000)
       :PS107 (range 1500001 1750000)
       :PS108 (range 1750001 2000000)
       :PS109 (range 2000001 2250000)
       :PS110 (range 2250001 2500000)}
    :Clay
      {:PS201 (range 2500001 2750000)
       :PS202 (range 2750001 3000000)
       :PS203 (range 3000001 3250000)
       :PS204 (range 3250001 3500000)
       :PS205 (range 3500001 3750000)
       :PS206 (range 3750001 4000000)
       :PS207 (range 4000001 4250000)
       :PS208 (range 4250001 4500000)
       :PS209 (range 4500001 4750000)
       :PS210 (range 4750001 5000000)}
    :Fayette
      {:PS301 (range 5000001 5250000)
       :PS302 (range 5250001 5500000)
       :PS303 (range 5500001 5750000)
       :PS304 (range 5750001 6000000)
       :PS305 (range 6000001 6250000)
       :PS306 (range 6250001 6500000)
       :PS307 (range 6500001 6750000)
       :PS308 (range 6750001 7000000)
       :PS309 (range 7000001 7250000)
       :PS310 (range 7250001 7500000) }
    :Greene
      {:PS401 (range 7500001 7750000)
       :PS402 (range 7750001 8000000)
       :PS403 (range 8000001 8250000)
       :PS404 (range 8250001 8500000)
       :PS405 (range 8500001 8750000)
       :PS406 (range 8750001 9000000)
       :PS407 (range 9000001 9250000)
       :PS408 (range 9250001 9500000)
       :PS409 (range 9500001 9750000)
       :PS410 (range 9750001 10000000) }
    :Lake
      {:PS501 (range 10000001 10250000)
       :PS502 (range 10250001 10500000)
       :PS503 (range 10500001 10750000)
       :PS504 (range 10750001 11000000)
       :PS505 (range 11000001 11250000)
       :PS506 (range 11250001 11500000)
       :PS507 (range 11500001 11750000)
       :PS508 (range 11750001 12000000)
       :PS509 (range 12000001 12250000)
       :PS510 (range 12250001 12500000) }
    :Marion
      {:PS601 (range 12500001 12750000)
       :PS602 (range 12750001 13000000)
       :PS603 (range 13000001 13250000)
       :PS604 (range 13250001 13500000)
       :PS605 (range 13500001 13750000)
       :PS606 (range 13750001 14000000)
       :PS607 (range 14000001 14250000)
       :PS608 (range 14250001 14500000)
       :PS609 (range 14500001 14750000)
       :PS610 (range 14750001 15000000) }
    :Putnam
      {:PS701 (range 15000001 15250000)
       :PS702 (range 15250001 15500000)
       :PS703 (range 15500001 15750000)
       :PS704 (range 15750001 16000000)
       :PS705 (range 16000001 16250000)
       :PS706 (range 16250001 16500000)
       :PS707 (range 16500001 16750000)
       :PS708 (range 16750001 17000000)
       :PS709 (range 17000001 17250000)
       :PS710 (range 17250001 17500000) }
    :Rock_Island
      {:PS801 (range 17500001 17750000)
       :PS802 (range 17750001 18000000)
       :PS803 (range 18000001 18250000)
       :PS804 (range 18250001 18500000)
       :PS805 (range 18500001 18750000)
       :PS806 (range 18750001 19000000)
       :PS807 (range 19000001 19250000)
       :PS808 (range 19250001 19500000)
       :PS809 (range 19500001 19750000)
       :PS810 (range 19750001 20000000) }
    :Wabash
      {:PS901 (range 20000001 20250000)
       :PS902 (range 20250001 20500000)
       :PS903 (range 20500001 20750000)
       :PS904 (range 20750001 21000000)
       :PS905 (range 21000001 21250000)
       :PS906 (range 21250001 21500000)
       :PS907 (range 21500001 21750000)
       :PS908 (range 21750000 22000000)
       :PS909 (range 22000001 22250000)
       :PS910 (range 22250001 22500000) }
    :Winnebago
      {:PS1001 (range 22500001 22750000)
       :PS1002 (range 22750001 23000000)
       :PS1003 (range 23000001 23250000)
       :PS1004 (range 23250001 23500000)
       :PS1005 (range 23500001 23750000)
       :PS1006 (range 23750001 24000000)
       :PS1007 (range 24000001 24250000)
       :PS1008 (range 24250001 24500000)
       :PS1009 (range 24500001 24750000)
       :PS1010 (range 24750001 25000000) }
    }
)

(defn gen-big-data
    []
    (doseq [[district schools] district-schools]
        (gen-schools district schools (format "/tmp/test/%s-schools.xml" (name district)))
        (doseq [[school rng] schools]
            (gen-students rng (format "/tmp/test/%s-%s-student.xml" (name district) (name school) ) )
;          (gen-saas rng "Grade 7 State Math" 6 (format "/tmp/test/%s-%s-assessment-results.xml" (name district) (name school)))
            (gen-enrollments rng (name school) (format "/tmp/test/%s-%s-enrollment.xml" (name district) (name school)))
            (gen-section-enrollments rng (name school) "7th Grade Math - Sec 2" (format "/tmp/test/%s-%s-sections.xml" (name district) (name school)))
        )
    )
)
