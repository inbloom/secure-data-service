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
  (with-open [out (java.io.FileWriter. output-file)]
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
              [s students, i (range n)]
              (gen-saa s assessment (str "2011-10-1" i)))))

(defn gen-enrollments
  [students edorg output-file]
  (gen-edfi :InterchangeStudentEnrollment output-file 
            (map #(gen-enroll % edorg) students)))

(defn gen-section-enrollments
  [students edorg section output-file]
  (gen-edfi :InterchangeStudentEnrollment output-file 
            (map #(gen-section-assoc % section edorg) students)))
