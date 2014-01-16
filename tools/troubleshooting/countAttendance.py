#!/usr/bin/python

# Copyright 2012-2013 inBloom, Inc. and its affiliates.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#This only works for Attendance
#This script is meant for debugging purposes.  It is not produciton quality
#This script counts the number of unique cominations of SchoolYear, StudentID, and SchoolId.  These are the components of 
# the key fields for the document that will be created in the mongo db.
#The script checks for uniquenss both with strings stripped of whitespace and whithout (the inbloom system will strip 
# leading and trailing whitespace when processing XML).  The there is a difference in counts when whitespace is not stripped
# this may be an indication that what the source system considers a unique record will be considered a dupilcate by the
# inbloom system

import hashlib
import sys
import xml.etree.cElementTree as et
import re
from glob import glob
import time
from os import listdir
from os.path import isfile, join

whitespaceToStrip = ' \t\n'
tag_re = re.compile('\{.*\}')

def initData(data):
	data["count"] = 0
	data["naturalKeyDup"] = 0
	data["deletes"] = 0
	data["deletesDup"] = 0
	data["deletesDupStrip"] = 0
	data["attendanceEventDup"] = 0
	data["naturalKeyHash"] = {}
	data["deleteNaturalKeyHash"] = {}
	data["deleteNaturalKeyHashStrip"] = {}
	data["entityEventHash"] = {}
	data["naturalKeyDupStrip"] = 0
	data["attendanceEventDupStrip"] = 0
	data["naturalKeyHashStrip"] = {}
	data["entityEventHashStrip"] = {}
	data["dupXML"] = {}
	data["dupXMLStrip"] = {}

def get_namespace(element):
  m = tag_re.match(element.tag)
  return m.group(0) if m else ''

def getStateEdOrgId(EdOrgReferenceNode):
	ns =  get_namespace(EdOrgReferenceNode)
	EducationalOrgIdentity = EdOrgReferenceNode.find("{0}EducationalOrgIdentity".format(ns))
	StateOrganizationId = EducationalOrgIdentity.find("{0}StateOrganizationId".format(ns))
	return StateOrganizationId.text
	
def getStudentId(StudentReferenceNode):
	ns =  get_namespace(StudentReferenceNode)
	StudentIdentity = StudentReferenceNode.find("{0}StudentIdentity".format(ns))
	StudentUniqueStateId = StudentIdentity.find("{0}StudentUniqueStateId".format(ns))
	return StudentUniqueStateId.text

def compareAndStoreHash(data, naturalKeyString, entityString, naturalKeyStringStrip, entityStringStrip, xmlnode, isDelete, compareEntity=False):
	try:
			hash = hashlib.sha256(naturalKeyString).hexdigest()
			hashStrip = hashlib.sha256(naturalKeyStringStrip).hexdigest()
		
			if hash in data["naturalKeyHash"]:
				data["naturalKeyDup"] += 1
				data["naturalKeyHash"][hash] += 1
			else:
				data["naturalKeyHash"][hash] = 1

			if hashStrip in data["naturalKeyHashStrip"]:
				data["naturalKeyDupStrip"] += 1
				data["naturalKeyHashStrip"][hashStrip] += 1
			else:
				data["naturalKeyHashStrip"][hashStrip] = 1

			if compareEntity:
				entityHashStrip = hashlib.sha256(entityStringStrip).hexdigest()
				entityHash = hashlib.sha256(entityString).hexdigest()
				if entityHashStrip in data["entityEventHashStrip"]:
					data["attendanceEventDupStrip"] += 1
					data["entityEventHashStrip"][entityHashStrip] +=1
					if (entityHash not in data["dupXMLStrip"]):
						data["dupXMLStrip"][entityHashStrip] = []
				else:
					data["entityEventHashStrip"][entityHashStrip] = 1

				if entityHash in data["entityEventHash"]:
					data["attendanceEventDup"] += 1
					data["entityEventHash"][entityHash] +=1
					if (entityHash not in data["dupXML"]):
						data["dupXML"][entityHash] = []
				else:
					data["entityEventHash"][entityHash] = 1		

	except UnicodeEncodeError:
		print(sys.aexc_info())
		#print(et.tostring(xmlnode))


def createAttendanceHashes(filename, data, strip=True, compareEntity=False):
	isDelete = False
	f = open(filename)
	it = et.iterparse(f, events=('start','end'))
	elem = ()
	
	#look in the first 10 tags for an <Action> tag
	for i in range(0,10):
		if sys.version_info[0] > 2:
			elem = it.__next__()
		else:
			elem = it.next()
		ns = get_namespace(elem[1])
		actt = "{0}Action".format(ns)
		#print(elem[1].tag)
		if elem[1].tag == actt:
			isDelete = True

	#ns was acuqired while looking for Action tag
	syt = "{0}SchoolYear".format(ns)
	schrt = "{0}SchoolReference".format(ns)
	sturt = "{0}StudentReference".format(ns)
	aect = "{0}AttendanceEventCategory".format(ns)
	edt = "{0}EventDate".format(ns)
	aet = "{0}AttendanceEvent".format(ns)
	f.close()
	#recreate the iterator so we start at the beginning
	f = open(filename)
	it = et.iterparse(f)
	
	schoolId = u""
	schoolYear = u""
	studentId = u""
	category = u""
	date = u""
	schoolIdStrip = u""
	schoolYearStrip = u""
	studentIdStrip = u""
	categoryStrip = u""
	dateStrip = u""
	for elem in it:
		child = elem[1]
		if elem[1].tag == syt:
			schoolYearStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
			schoolYear = child.text.encode("UTF-8")
		elif elem[1].tag == schrt:
			schoolIdStrip = getStateEdOrgId(child).strip(whitespaceToStrip).encode("UTF-8")
			schoolId = getStateEdOrgId(child).encode("UTF-8")
		elif elem[1].tag == sturt:
			studentIdStrip = getStudentId(child).strip(whitespaceToStrip).encode("UTF-8")
			studentId = getStudentId(child).encode("UTF-8")
		elif elem[1].tag == aect:
			categoryStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
			category = child.text.encode("UTF-8")
		elif elem[1].tag == edt:
			dateStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
			date = child.text.encode("UTF-8")
		elif elem[1].tag == aet:
			data["count"] += 1

			if len(schoolId) < 1:
				print("Empty School Id")
			if len(studentId) < 1:
				print("Empty Student Id")
			if len(schoolYear) < 1:
				print("Empty School Year")
			naturalKeyString = schoolId + studentId + schoolYear
			naturalKeyStringStrip = schoolIdStrip + studentIdStrip + schoolYearStrip
			if compareEntity:
				entityStringStrip = schoolIdStrip + studentIdStrip + schoolYearStrip + dateStrip + categoryStrip
				entityString = schoolId + studentId + schoolYear + date + category
				categoryStrip = u""
				dateStrip = u""
				category = u""
				date = u""
				compareAndStoreHash(data, naturalKeyString, entityString, naturalKeyStringStrip, entityStringStrip, child, isDelete, compareEntity)
			else:
				compareAndStoreHash(data, naturalKeyString, "", naturalKeyStringStrip, "", child, isDelete, compareEntity)
			schoolId = u""
			schoolYear = u""
			studentId = u""
			schoolIdStrip = u""
			studentIdStrip = u""
			schoolYearStrip = u""
			#this signifcantly reduces memory usage
			child.clear()
	f.close()
	
def iterateThroughFiles(fileList, strip=True, checkEntities=False):	  
	data = {}
	initData(data)
	for i in range(0,len(fileList)):
		print("Processing File: " + fileList[i])
		createAttendanceHashes(fileList[i], data, strip, checkEntities)
		print('Cumulative Entites:' + str(data["count"])+ ', Cumulative unique natural key combos:' + str(data["count"] - data["naturalKeyDupStrip"]))

	outputData(data, checkEntities)
	
	return (data["count"] - data["naturalKeyDup"])

def outputData(data, checkEntities=False):
	print(str(data["count"]) + " attendance " + " checked.")
	print(str(data["naturalKeyDupStrip"]) + " Natural Key duplicates found")
	print(str(data["count"] - data["naturalKeyDupStrip"]) + " unique attendance documents (" + str(len(data["naturalKeyHashStrip"])) + ")")
	if checkEntities:
		print(str(data["attendanceEventDupStrip"]) + " duplicate attendance events found")

	print ("\nWithout Whitespace Stripped:")
	print (str(data["naturalKeyDup"]) + " Natural Key duplicates found")
	print (str(data["count"] - data["naturalKeyDup"]) + " unique attendance documents (" + str(len(data["naturalKeyHash"])) + ")")
	if data["naturalKeyDupStrip"] != data["naturalKeyDup"]:
		print("Without Stripping Whitespace, different counts for natural keys of Attendance docs.  Check for leading or trailing whitespace in source system")
	if checkEntities:
		print (str(data["attendanceEventDup"]) + " duplicate attendance events found")
		if data["attendanceEventDupStrip"] == data["attendanceEventDup"]:
			print("Whitespace does not effect the number of unique attendance natural keys")
		else:
			print("Without Stripping Whitespace, different counts AttendanceEvent (subdocs).  Check for leading or trailing whitespace in source system")

	
if __name__ == '__main__':
	fileList = []
	start = time.clock()
	if len(sys.argv) <= 1: 
		print("No files specified.  Using all xml files in current directory")
		fileList = [ f for f in listdir('.') if isfile(join('.',f)) & f.endswith(".xml") ]
	else:
		fileList = sys.argv[1:]
		# windows does not expand wildcards from the command line.  Check for this case.
		# NOTE: on windows, the code will only check for wildcards in the first command line argument.
		# ToDo: update this code to check for all wildcard characters on all arguments
		if ('*' in sys.argv[1]) | ('?' in sys.argv[1]):
			fileList = glob(sys.argv[1])
			#print("Expanding wildcard in first file argument to " + str(fileList))
	unique = iterateThroughFiles(fileList, checkEntities=True)
	print("Processed in " + str(time.clock() - start) + " seconds")
		
