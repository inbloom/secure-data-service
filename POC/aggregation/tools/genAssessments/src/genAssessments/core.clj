(ns genAssessments.core
  (:use clojure.data.xml)
  (:use clojure.contrib.math))

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


(defn create-school [districtName, school]
  (let [tmp (element :School {}
    (element :StateOrganizationId {} school)
    (element :EducationOrgIdentificationCode {:IdentificationSystem "School"}
      (element :ID {} school)
    )
    (element :NameOfInstitution {} school)
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
      (element :schoolCategory {} "Elementary School")
    )
    (element :LocalEducationAgencyReference {}
      (element :EducationalOrgIdentity {}
        (element :StateOrganizationId {} districtName)
      )
      (element :EducationOrgIdentificationCode {:IdentificationSystem "SEA"}
        (element :ID {} districtName)
      )
    )
  )]
  tmp
  )
)

(defn create-state []
  (let [tmp (element :StateEducationAgency {}
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
  (let [tmp (element :LocalEducationAgency {}
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
    (element :StateEducationAgencyReference {}
      (element :EducationalOrgIdentity {}
        (element :StateOrganizationId {} "NY")
      )
      (element :EducationOrgIdentificationCode {:IdentificationSystem "SEA"}
        (element :ID {} "IL")
      )
    )
    )]
    tmp
  )
)

(defn gen-schools
  [district schools output-file]
  (gen-edfi :InterchangeEducationOrganization output-file
    (reverse
      (into ()
        [
          (create-state)
          (create-district district)
          (for [school schools
            :let [tmp (create-school district school)]]
            tmp
          )
        ]
      )
    )
  )
)

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

(def districts
    ["Abbott","Addison","Adirondack","Afton","Akron","Albany City","Albion","Alden","Alexander","Alexandria","Alfred-Almond",
     "Allegany-Limestone","Altmar Parish-Williamstown","Amagansett","Amherst","Amityville","Amsterdam City","Andes","Andover",
     "Ardsley","Argyle","Arkport","Arlington","Attica","Auburn City","Ausable Valley","Averill Park","Avoca","Avon","Babylon",
     "Bainbridge-Guilford","Baldwin","Baldwinsville","Ballston Spa","Barker","Batavia City","Bath","Bay Shore","Bayport-Blue Point",
     "Beacon City","Beaver River","Bedford","Beekmantown","Belfast","Belleville Henderson","Bellmore","Bellmore-Merrick Central",
     "Bemus Point","Berkshire","Berlin","Berne-Knox-Westerlo","Bethlehem","Bethpage","Binghamton City","Blind Brook-Rye",
     "Bolivar-Richburg","Bolton","Bradford","Brasher Falls","Brentwood","Brewster","Briarcliff Manor","Bridgehampton","Brighton",
     "Broadalbin-Perth","Brockport","Brocton","Bronxville","Brookfield","Brookhaven-Comsewogue","Broome-Delaware-Tioga Boces",
     "Brunswic","Brushton-Moira","Buffalo City","Burnt Hills-Ballston Lake","Byram Hills","Byron-Bergen","Cairo-Durham",
     "Caledonia-Mumford","Cambridge","Camden","Campbell-Savona","Canajoharie","Canandaigua City","Canaseraga","Canastota",
     "Candor","Canisteo-Greenwood","Canton","Capital Region Boces","Carle Place","Carmel","Carthage","Cassadaga Valley",
     "Cato-Meridian","Catskill","Cattar-Allegany-Erie-Wyoming Boces","Cattaraugus-Little Valley","Cayuga-Onondaga Boces",
     "Cazenovia","Center Moriches","Central Islip","Central Square","Chappaqua","Charlotte Valley","Chateaugay","Chatham",
     "Chautauqua Lake","Chazy","Cheektowaga","Cheektowaga-Maryvale","Cheektowaga-Sloan","Chenango Forks","Chenango Valley",
     "Cherry Valley-Springfield","Chester","Chittenango","Churchville-Chili","Cincinnatus","Clarence","Clarkstown","Cleveland Hill",
     "Clifton-Fin","Clinton","Clinton-Essex-Warren-Washing Boces","Clyde-Savannah","Clymer","Cobleskill-Richmondville","Cohoes City",
     "Cold Spring Harbor","Colton-Pierrepont","Commack","Connetquot","Cooperstown","Copenhagen","Copiague","Corinth","Corning City",
     "Cornwall","Cortland City","Coxsackie-Athens","Croton-Harmon","Crown Point","Cuba-Rushford","Dalton-Nunda (Keshequa)",
     "Dansville","Deer Park","Delaware Academy At Delhi","Delaw-Chenango-Madison-Otsego Boces","Depew","Deposit","Deruyter",
     "Dobbs Ferry","Dolgeville","Dover","Downsville","Dryden","Duanesburg","Dundee","Dunkirk City","Dutchess Boces","East Aurora",
     "East Bloomfield","East Greenbush","East Hampton","East Irondequoit","East Islip","East Meadow","East Moriches",
     "East Quogue","East Ramapo  (Spring Valley)","East Rochester","East Rockaway","East Syracuse-Minoa","East Williston",
     "Eastchester","Eastern Suffolk Boces","Eastport-South Manor","Eden","Edgemont","Edinburg Common","Edmeston","Edwards-Knox",
     "Elba","Eldred","Elizabethtown-Lewis","Ellenville","Ellicottville","Elmira City","Elmira Heights","Elmont","Elmsford",
     "Elwood","Erie 1 Boces","Erie 2-Chautauqua-Cattaraugus Boces","Evans-Brant  (Lake Shore)","Fabius-Pompey","Fairport",
     "Falconer","Fallsburg","Farmingdale","Fayetteville-Manlius","Fillmore","Fire Island","Fishers Island","Floral Park-Bellerose",
     "Florida","Fonda-Fultonville","Forestville","Fort Ann","Fort Edward","Fort Plain","Frankfort-Schuyler","Franklin",
     "Garden City","Garrison","Gates-Chili","General Brown","Genesee Valley Boces","Genesee Valley","Geneseo","Geneva City",
     "Hadley-Luzerne","Haldane","Half Hollow Hills","Hamburg","Hamilton","Hamilton-Fulton-Montgomery Boces","Hammond",
     "Homer","Honeoye","Honeoye Falls-Lima","Hoosic Valley","Hoosick Falls","Hopevale  At Hamburg","Hornell City",
     "Inlet Comn","Iroquois","Irvington","Island Park","Island Trees","Islip","Ithaca City","Jamestown City","Jamesville-Dewitt",
     "Keene","Kendall","Kenmore-Tonawanda","Kinderhook","Kings Park","Kingston City","Kiryas Joel Village","La Fargeville",
     "Livonia","Lockport City","Locust Valley","Long Beach City","Long Lake","Longwood","Lowville Academy","Lyme",
     "Middletown City","NYC Geog Dist # 1 - Manhattan","NYC Geog Dist # 2 - Manhattan","NYC Geog Dist # 3 - Manhattan",
     "NYC Geog Dist # 4 - Manhattan","NYC Geog Dist # 5 - Manhattan","NYC Geog Dist # 6 - Manhattan","NYC Geog Dist # 7 - Bronx",
     "NYC Geog Dist # 8 - Bronx","NYC Geog Dist # 9 - Bronx","NYC Geog Dist #10 - Bronx","NYC Geog Dist #11 - Bronx",
     "NYC Geog Dist #12 - Bronx","NYC Geog Dist #13 - Brooklyn","NYC Geog Dist #14 - Brooklyn","NYC Geog Dist #15 - Brooklyn",
     "NYC Geog Dist #16 - Brooklyn","NYC Geog Dist #17 - Brooklyn","NYC Geog Dist #18 - Brooklyn","NYC Geog Dist #19 - Brooklyn",
     "NYC Geog Dist #20 - Brooklyn","NYC Geog Dist #21 - Brooklyn","NYC Geog Dist #22 - Brooklyn","NYC Geog Dist #23 - Brooklyn",
     "NYC Geog Dist #24 - Queens","NYC Geog Dist #25 - Queens","NYC Geog Dist #26 - Queens","NYC Geog Dist #27 - Queens",
     "NYC Geog Dist #31 - Staten Island","NYC Geog Dist #32 - Brooklyn","NYC Spec Schools - Dist 75","Oakfield-Alabama",
     "Ossining","Oswego Boces","Oswego City","Otego-Unadilla","Otsego-Delaw-Schoharie-Greene Boces","Owego-Apalachin",
     "Panama","Parishville-Hopkinton","Patchogue-Medford","Pavilion","Pawling","Pearl River","Peekskill City","Poland",
     "Randolph","Raquette Lake","Ravena-Coeymans-Selkirk","Red Creek","Red Hook","Remsen","Remsenburg-Speonk",
     "Salem","Salmon River","Sandy Creek","Saranac","Saranac Lake","Saratoga Springs City","Saugerties","Sauquoit Valley" ] )

(defn gen-district-schools
  [districtCount schoolsPerDistrict studentsPerSchool]
  ; should distribute this in a more normal distribution
  (for [i (range 1 (+ districtCount 1)) :let [r (assoc (hash-map) (districts i) (for [j (range 1 (+ 1 schoolsPerDistrict))] (format "PS-%s-%s" (str i) (str j))))]]
   r)
)


(defn gen-big-data
    []
    (doseq [ [district] (map list (gen-district-schools 1 1 100))]
      (doseq [ [districtName schools] district]
        (gen-schools districtName schools (format "/tmp/test/%s-schools.xml" districtName))
      )
    )
)

;        (doseq [[school rng] schools]
;            (gen-students rng (format "/tmp/test/%s-%s-student.xml" (name district) (name school) ) )
;            (gen-saas rng "Grade 7 State Math" 6 (format "/tmp/test/%s-%s-assessment-results.xml" (name district) (name school)))
;            (gen-enrollments rng (name school) (format "/tmp/test/%s-%s-enrollment.xml" (name district) (name school)))
;            (gen-section-enrollments rng (name school) "7th Grade Math - Sec 2" (format "/tmp/test/%s-%s-sections.xml" (name district) (name school)))
;        )
;    )
;)
