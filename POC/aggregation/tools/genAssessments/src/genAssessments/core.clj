(ns genAssessments.core
  (:use genAssessments.defines)
  (:use genAssessments.edfi_xml)
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
  (for [i (range 1 (+ districtCount 1))
    :let [r (assoc (hash-map) (districts i)
      (for [j (range 1 (+ 1 schoolsPerDistrict))]
        (school-id i j)))]]
   r)
)


(defn gen-big-data
  [districtCount schoolCount studentCount]
  (def sectionName "Math007-S2")
  (def assessmentName "Grade 7 State Math")
  (def i (make-counter 0))
  (println
    (format "Starting assessment data generation: [%d districts %d schools %d students/school]"
    districtCount schoolCount studentCount)
  )
  (println (format "[0/%d districts]" districtCount))
  (gen-assessment assessmentName "/tmp/test/F-assessment.xml")
  (def rng (range 1 (+ 1 studentCount)))
  (doseq [ [district] (map list (gen-district-schools districtCount schoolCount studentCount))]
    (def startTime (System/currentTimeMillis))
    (doseq [ [districtName schools] district]
      (gen-students schools rng (format "/tmp/test/A-%s-student.xml" districtName))
      (gen-schools districtName schools (format "/tmp/test/B-%s-schools.xml" districtName))
      (gen-sessions schools (format "/tmp/test/C-%s-calendar.xml" districtName))
      (gen-sections districtName schools (format "/tmp/test/D-%s-master-schedule.xml" districtName))
      ; (gen-student-enrollments schools rng (format "/tmp/test/E-%s-enrollment.xml" districtName))
      ; (gen-student-assessment-associations districtName schools rng assessmentName 4 (format "/tmp/test/G-%s-assessment-results.xml" districtName))
    )
    (def endTime (System/currentTimeMillis))
    (def elapsed (/ (-  endTime startTime) 1000.0))
    (def remain (* elapsed (- districtCount ((i :next)))))
    (println (format "[%d/%d districts] (%f seconds : %f remaining)"  ((i :curr)) districtCount elapsed (max 0.0 remain)))
  )
  (create-control-file)
  (println "Assessment data generation complete")
)

; 100 students
(defn gen-tiny-set []
  (gen-big-data 1 1 100)
)

; 2500 students
(defn gen-small-set []
  (gen-big-data 1 5 100)
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
