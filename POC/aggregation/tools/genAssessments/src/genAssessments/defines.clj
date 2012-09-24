(ns genAssessments.defines)

(def first-names
  ["James","John","Robert","Michael","William","David","Richard","Charles","Joseph","Thomas","Christopher","Daniel","Paul","Mark",
   "Donald","George","Kenneth","Steven","Edward","Brian","Ronald","Anthony","Kevin","Jason","Matthew","Gary","Timothy","Jose","Larry",
   "Jeffrey","Frank","Scott","Eric","Stephen","Andrew","Raymond","Gregory","Joshua","Jerry","Dennis","Walter","Patrick","Peter",
   "Harold","Douglas","Henry","Carl","Arthur","Ryan","Roger","Joe","Juan","Jack","Albert","Jonathan","Justin","Terry","Gerald","Keith",
   "Samuel","Willie","Ralph","Lawrence","Nicholas","Roy","Benjamin","Bruce","Brandon","Adam","Harry","Fred","Wayne","Billy","Steve",
   "Louis","Jeremy","Aaron","Randy","Howard","Eugene","Carlos","Russell","Bobby","Victor","Martin","Ernest","Phillip","Todd","Jesse",
   "Craig","Alan","Shawn","Clarence","Sean","Philip","Chris","Johnny","Earl","Jimmy","Antonio","Danny","Bryan","Tony","Luis","Mike",
   "Stanley","Leonard","Nathan","Dale","Manuel","Rodney","Curtis","Norman","Allen","Marvin","Vincent","Glenn","Jeffery","Travis",
   "Jeff","Chad","Jacob","Lee","Melvin","Alfred","Kyle","Francis","Bradley","Jesus","Herbert","Frederick","Ray","Joel","Edwin","Don",
   "Eddie","Ricky","Troy","Randall","Barry","Alexander","Bernard","Mario","Leroy","Francisco","Marcus","Micheal","Theodore",
   "Clifford","Miguel","Oscar","Jay","Jim","Tom","Calvin","Alex","Jon","Ronnie","Bill","Lloyd","Tommy","Leon","Derek","Warren","Darrell",
   "Jerome","Floyd","Leo","Alvin","Tim","Wesley","Gordon","Dean","Greg","Jorge","Dustin","Pedro","Derrick","Dan","Lewis","Zachary",
   "Corey","Herman","Maurice","Vernon","Roberto","Clyde","Glen","Hector","Shane","Ricardo","Sam","Rick","Lester","Brent","Ramon",
   "Charlie","Tyler","Gilbert","Gene","Marc","Reginald","Ruben","Brett","Angel","Nathaniel","Rafael","Leslie","Edgar","Milton","Raul",
   "Ben","Chester","Cecil","Duane","Franklin","Andre","Elmer","Brad","Gabriel","Ron","Mitchell","Roland","Arnold","Harvey","Jared",
   "Adrian","Karl","Cory","Claude","Erik","Darryl","Jamie","Neil","Jessie","Christian","Javier","Fernando","Clinton","Ted","Mathew",
   "Tyrone","Darren","Lonnie","Lance","Cody","Julio","Kelly","Kurt","Allan","Nelson","Guy","Clayton","Hugh","Max","Dwayne","Dwight",
   "Armando","Felix","Jimmie","Everett","Jordan","Ian","Wallace","Ken","Bob","Jaime","Casey","Alfredo","Alberto","Dave","Ivan",
   "Johnnie","Sidney","Byron","Julian","Isaac","Morris","Clifton","Willard","Daryl","Ross","Virgil","Andy","Marshall","Salvador",
   "Perry","Kirk","Sergio","Marion","Tracy","Seth","Kent","Terrance","Rene","Eduardo","Terrence","Enrique","Freddie","Wade"]
)

(def last-names
  ["Sears","Mayo","Dunlap","Hayden","Wilder","Mckay","Coffey","Mccarty","Ewing","Cooley","Vaughan","Bonner","Cotton","Holder","Stark",
   "Ferrell","Cantrell","Fulton","Lynn","Lott","Calderon","Rosa","Pollard","Hooper","Burch","Mullen","Fry","Riddle","Levy","David",
   "Duke","Odonnell","Guy","Michael","Britt","Frederick","Daugherty","Berger","Dillard","Alston","Jarvis","Frye","Riggs","Chaney",
   "Odom","Duffy","Fitzpatrick","Valenzuela","Merrill","Mayer","Alford","Mcpherson","Acevedo","Donovan","Barrera","Albert","Cote",
   "Reilly","Compton","Raymond","Mooney","Mcgowan","Craft","Cleveland","Clemons","Wynn","Nielsen","Baird","Stanton","Snider",
   "Rosales","Bright","Witt","Stuart","Hays","Holden","Rutledge","Kinney","Clements","Castaneda","Slater","Hahn","Emerson","Conrad",
   "Burks","Delaney","Pate","Lancaster","Sweet","Justice","Tyson","Sharpe","Whitfield","Talley","Macias","Irwin","Burris","Ratliff",
   "Mccray","Madden","Kaufman","Beach","Goff","Cash","Bolton","Mcfadden","Levine","Good","Byers","Kirkland","Kidd","Workman","Carney",
   "Dale","Mcleod","Holcomb","England","Finch","Head","Burt","Hendrix","Sosa","Haney","Franks","Sargent","Nieves","Downs","Rasmussen",
   "Bird","Hewitt","Lindsay","Le","Foreman","Valencia","Oneil","Delacruz","Vinson","Dejesus","Hyde","Forbes","Gilliam","Guthrie",
   "Wooten","Huber","Barlow","Boyle","Mcmahon","Buckner","Rocha"]
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
     "Salem","Salmon","Sandy-Creek","Saranac","Saranac-Lake","Saratoga-Springs","Saugerties","Sauquoit-Valley" ]
 )

(defn school-id [i j]
  (format "PS-%s" (str (+ i j)))
)

(defn student-id [schoolName studentId]
  (format "%s-%s" schoolName (str studentId))
)

(defn section-id [schoolName]
  (format "%s-Math007-S2" schoolName)
)

(defn local-course-id [districtName schoolName]
  (format "Math007-%s-%s" districtName schoolName)
)


(defn course-id [districtName]
  (format "Math007-%s" districtName)
)

(defn assessment-id [assessmentName]
  (str "Grade_7_2011_State_Math")
)

(defn saa-id [schoolName studentId date]
  (format "SAA-%s-%s-%s" schoolName (str studentId) date)
)

(defn calendar-date-id [districtName]
  (format "%s-day" districtName)
)

(defn grading-period-id [districtName]
  (format "%s-GP1" districtName)
)

(defn session-id [districtName]
  (format "%s-Fall-2011" districtName)
)

(defn performance-level [score]
  (cond
    (> 14 score) "W"
    (> 20 score) "B"
    (> 27 score) "S"
    (> 33 score) "E")
)
