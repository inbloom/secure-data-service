(ns genAssessments.core
  (:use clojure.data.xml))

(defn gen-student
  [id]
  (element :Student {}
           (element :StudentUniqueStateId {} (str id))
           (element :Name {}
                    (element :FirstName {} (str "Kid" id))
                    (element :LastSurname {} (str "Smith" id)))
           (element :Sex {} "Male")
           (element :BirthData {}
                    (element :BirthDate {} "2001-06-18"))
           (element :HispanicLatinoEthnicity {} "false")
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
