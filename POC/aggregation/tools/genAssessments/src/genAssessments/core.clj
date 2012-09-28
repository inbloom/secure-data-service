(ns genAssessments.core
  (:use genAssessments.defines)
  (:use genAssessments.edfi_xml)
  (:use genAssessments.json)
  (:use clojure.data.xml)
  (:use clojure.contrib.math)
  (:gen-class)
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

  (def json { :gen-state gen-state-json,
              :gen-assessment gen-assessment-json,
              :gen-students gen-students-json,
              :gen-schools gen-schools-json,
              :gen-sessions gen-sessions-json,
              :gen-sections gen-sections-json,
              :gen-student-enrollments gen-student-enrollments-json,
              :gen-student-assessment-associations gen-student-assessment-associations-json 
              :create-control-file create-control-file-json } )

  (def edfi {  :gen-state gen-state-edfi,
               :gen-assessment gen-assessment-edfi,
               :gen-students gen-students-edfi,
               :gen-schools gen-schools-edfi,
               :gen-sessions gen-sessions-edfi,
               :gen-sections gen-sections-edfi,
               :gen-student-enrollments gen-student-enrollments-edfi,
               :gen-student-assessment-associations gen-student-assessment-associations-edfi
               :create-control-file create-control-file-edfi } )

  (def ^:dynamic fmt)

  (cond
    (= dataFormat "json") (def fmt json)
    :else (def fmt edfi)
  )

  ((:gen-state fmt))
  ((:gen-assessment fmt) assessmentName)

  (def rng (range studentCount))
  (doseq [ [district] (map list (gen-district-schools districtCount schoolCount studentCount))]
    (def startTime (System/currentTimeMillis))
    (doseq [ [districtName schools] district]
      ((:gen-students fmt) districtName schools rng)
      ((:gen-schools fmt) districtName schools)
      ((:gen-sessions fmt) districtName schools)
      ((:gen-sections fmt) districtName schools)
      ((:gen-student-enrollments fmt) districtName schools rng)
      ((:gen-student-assessment-associations fmt) districtName schools rng assessmentName 10)
    )
    (def endTime (System/currentTimeMillis))
    (def elapsed (/ (-  endTime startTime) 1000.0))
    (def remain (* elapsed (- districtCount ((i :next)))))
    (println (format "[%d/%d districts] (%f seconds : %f remaining)"  ((i :curr)) districtCount elapsed (max 0.0 remain)))
  )
  ((:create-control-file fmt))

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

(defn -main [& [args]]
  (gen-extra-large-set)
)
