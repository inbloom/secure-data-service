(ns genAssessments.core
  (:use genAssessments.defines)
  (:use genAssessments.edfi_xml)
  (:use genAssessments.json)
  (:use clojure.data.xml)
  (:use clojure.contrib.math)
)

(defn no-op [v]
  v
)

(defn make-counter [init-val]
  (let [c (atom init-val)]
    {:next #(swap! c inc)
     :reset #(reset! c init-val)
     :curr #(swap! c no-op)}
  )
)

(defn gen-district-schools [districtCount schoolsPerDistrict studentsPerSchool]
  ; should distribute this in a more normal distribution
  (for [i (range districtCount)
    :let [r (assoc (hash-map) (districts i)
      (for [j (range schoolsPerDistrict)]
        (school-id i j)))]]
   r)
)

(defn gen-big-data
  [districtCount schoolCount studentCount dataFormat]
  (def sectionName "Math007-S2")
  (def assessmentName "Grade 7 State Math")
  (def i (make-counter 0))
  (println
    (format "Starting assessment data generation: [%d districts %d schools/district %d students/school - format '%s']"
    districtCount schoolCount studentCount dataFormat)
  )
  (println (format "[0/%d districts]" districtCount))
  
  (def ^:dynamic gen-state)
  (def ^:dynamic gen-assessment)
  (def ^:dynamic gen-students)
  (def ^:dynamic gen-schools)
  (def ^:dynamic gen-sections)
  (def ^:dynamic gen-sessions)
  (def ^:dynamic gen-student-enrollments)
  (def ^:dynamic gen-student-assessment-associations)
  
  (defn create-entities [assessmentName sectionName districtCount schoolCount studentCount]
    (gen-state)
    (gen-assessment assessmentName "assessment")
    
    (def rng (range studentCount))
    (doseq [ [district] (map list (gen-district-schools districtCount schoolCount studentCount))]
      (def startTime (System/currentTimeMillis))
      (doseq [ [districtName schools] district]
        (gen-students schools rng (format "student" districtName))
        (gen-schools districtName schools (format "educationOrganization" districtName))
        (gen-sessions schools "session")
        (gen-sections districtName schools (format "section" districtName))
        (gen-student-enrollments schools rng (format "/tmp/test/E-%s-enrollment.xml" districtName))
        (gen-student-assessment-associations districtName schools rng assessmentName 10 (format "/tmp/test/G-%s-assessment-results.xml" districtName))
      )
      (def endTime (System/currentTimeMillis))
      (def elapsed (/ (-  endTime startTime) 1000.0))
      (def remain (* elapsed (- districtCount ((i :next)))))
      (println (format "[%d/%d districts] (%f seconds : %f remaining)"  ((i :curr)) districtCount elapsed (max 0.0 remain)))
    )
  )  
  
  (cond 
    (= dataFormat "json")
    (binding [gen-state gen-state-json]
      (binding [gen-assessment gen-assessment-json]
        (binding [gen-students gen-students-json]
          (binding [gen-schools gen-schools-json]
            (binding [gen-sessions gen-sessions-json] 
              (binding [gen-sections gen-sections-json] 
                (binding [gen-student-enrollments gen-student-enrollments-json]
                  (binding [gen-student-assessment-associations gen-student-assessment-associations-json]
                    (create-entities assessmentName sectionName districtCount schoolCount studentCount)
                  )
                )
              )
            )
          )
        )
      )
    )
    :else 
    (binding [gen-state gen-state-edfi]
      (binding [gen-assessment gen-assessment-edfi]
        (binding [gen-students gen-students-edfi]
          (binding [gen-schools gen-schools-edfi]
            (binding [gen-sessions gen-sessions-edfi]
              (binding [gen-sections gen-sections-edfi]
                (binding [gen-student-enrollments gen-student-enrollments-edfi]
                  (binding [gen-student-assessment-associations gen-student-assessment-associations-edfi]
                    (create-entities assessmentName sectionName districtCount schoolCount studentCount)
                    (create-control-file-edfi)
                  )
                )
              )
            )
          )
        )
      )
    )
  )
  
  (println "Assessment data generation complete")
)

; 100 students
(defn gen-tiny-set []
  (gen-big-data 1 1 100 "json")
)

; 2500 students
(defn gen-small-set []
  (gen-big-data 1 5 100 "json")
)

; 42000 students
(defn gen-medium-set []
  (gen-big-data 6 7 1000 "json")
)

; 500k students
(defn gen-medium-large-set []
  (gen-big-data 20 10 2500 "json")
)

; 1.5 million students
(defn gen-large-set []
  (gen-big-data 25 25 2500 "json")
)

; 15 million students
(defn gen-extra-large-set []
  (gen-big-data 125 50 2500 "json")
)

; 45 million students
(defn gen-giant-set []
  (gen-big-data 250 75 2400 "json")
)
