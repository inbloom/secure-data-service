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

(defn gen-saa
  [student assessment date]
  (let [score (rand-nth (range 6 33))]
    (element :StudentAssessment {}
             (element :AdministrationDate {} date)
             (element :ScoreResults {:AssessmentReportingMethod "Scale score"} 
                      (element :Result {} (str score)))
             (element :PerformanceLevels {}
                      (element :CodeValue {} (get-perf-level score)))
             (element :StudentReference {}
                      (element :StudentIdentity {}
                               (element :StudentUniqueStateId {} (str student))))
             (element :AssessmentReference {}
                      (element :AssessmentIdentity {}
                               (element :AssessmentIdentificationCode {:IdentificationSystem "Test Contractor"}
                                        (element :ID {} assessment)))))))

(defn gen-students
  [ids output-file]
  (with-open [out (java.io.FileWriter. output-file)]
    (emit
      (element :InterchangeStudentParent {:xmlns "http://ed-fi.org/0100"}
               (map gen-student ids))
      out)))

(defn gen-saas
  [students assessment n output-file]
  (with-open [out (java.io.FileWriter. output-file)]
    (emit
      (element :InterchangeStudentAssessment {:xmlns "http://ed-fi.org/0100"}
               (for
                 [s students
                  i (range n)]
                 (gen-saa s assessment (str "2011-10-1" i))))
      out)))
