Data generate script to create assessment data suitable for aggregation testing.

To run you will need:
- Clojure 1.4.0
- leiningen 1.7.1
- /tmp/test directory must exist.

This supports the following data generation commands:
(gen-tiny-set)         ;   1 district,   1 school/district,   100 students/school => 100 students
(gen-small-set)        ;   1 district,   5 schools/district,  500 students/school => 2500 students
(gen-medium-set)       ;   6 districts,  7 schools/district, 1000 students/school => 42000 students
(gen-medium-large-set) ;  20 districts, 10 schools/district, 2500 students/school => 500k students
(gen-large-set)        ;  25 districts, 25 schools/district, 2500 students/school => ~1.5 million students
(gen-extra-large-set   ; 125 districts, 50 schools/district, 2500 students/school  => ~15 million students

You can generate a custom data set by using the gen-big-data function. Example:
(gen-big-data 1 5 750)  ; 1 district,  5 schools/district,  750 students/school

Usage:
1. From the command prompt, run 'lein repl'.  You will see the repl prompt "user =>"
2. Load the library by entering to following at the repl prompt:
     (use 'genAssessments.core :reload-all)
3. Run one of the above commands to generate the dataset you want.

Data is generated in the /tmp/test directory.
