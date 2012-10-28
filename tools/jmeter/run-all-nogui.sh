#!/bin/sh
# Run all the tests in this directory once locally without starting the jmeter gui.
jmeter -n -q local-1.properties -t list-attendance.jmx
jmeter -n -q local-1.properties -t list-grades.jmx
jmeter -n -q local-1.properties -t list-sections.jmx
jmeter -n -q local-1.properties -t list-students.jmx
jmeter -n -q local-1.properties -t single-student.jmx
jmeter -n -q local-1.properties -t update-attendance.jmx
jmeter -n -q local-1.properties -t update-gradebooks.jmx
