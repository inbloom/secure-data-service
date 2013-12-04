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

whitespaceToStrip = ' \t\n'
tag_re = re.compile('\{.*\}')

def initData(data):
	data["count"] = 0
	data["naturalKeyDup"] = 0
	data["attendanceEventDup"] = 0
	data["naturalKeyHash"] = {}
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

def compareAndStoreHash(data, naturalKeyString, entityString, naturalKeyStringStrip, entityStringStrip, xmlnode, compareEntity=False):
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
		print sys.exc_info()
		#print et.tostring(xmlnode)

def createAttendanceHashes(source, data, strip=True, compareEntity=False):
	tree = et.parse(source)	
	root = tree.getroot()
	entityStringStrip = ""
	entityString = ""
	print root
	for attendance in root:
		data["count"] += 1
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
		for child in attendance:
			if child.tag.lower().endswith('schoolyear'):
					schoolYearStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
					schoolYear = child.text.encode("UTF-8")
			elif child.tag.lower().endswith('schoolreference'):
					schoolIdStrip = getStateEdOrgId(child).strip(whitespaceToStrip).encode("UTF-8")
					schoolId = getStateEdOrgId(child).encode("UTF-8")
			elif child.tag.lower().endswith('studentreference'):
					studentIdStrip = getStudentId(child).strip(whitespaceToStrip).encode("UTF-8")
					studentId = getStudentId(child).encode("UTF-8")
			elif child.tag.lower().endswith('AttendanceEventCategory'.lower()):
					categoryStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
					category = child.text.encode("UTF-8")
			elif child.tag.lower().endswith('EventDate'.lower()):
					dateStrip = child.text.strip(whitespaceToStrip).encode("UTF-8")
					date = child.text.encode("UTF-8")

		if len(schoolId) < 1 | len(schoolIdStrip) <1:
			print "Empty School Id"
		if len(studentId) < 1 | len(studentIdStrip)<1:
			print "Empty Student Id"
		if len(schoolYear) < 1 | len(schoolYearStrip)<1:
			print "Empty School Year"
		naturalKeyString = schoolId + studentId + schoolYear
		naturalKeyStringStrip = schoolIdStrip + studentIdStrip + schoolYearStrip
		if compareEntity:
			entityStringStrip = schoolIdStrip + studentIdStrip + schoolYearStrip + dateStrip + categoryStrip
			entityString = schoolId + studentId + schoolYear + date + category
			
		compareAndStoreHash(data, naturalKeyString, entityString, naturalKeyStringStrip, entityStringStrip, attendance, compareEntity)

def iterateThroughFiles(fileList, strip=True, checkEntities=False):	  
	data = {}
	initData(data)
	for i in range(0,len(fileList)):
		print "Processing File: " + fileList[i] 
		f = open(fileList[i])
		createAttendanceHashes(f, data, strip, checkEntities)
		print 'Cumulative Entites:' + str(data["count"])+ ', Cumulative unique natural key combos:' + str(data["count"] - data["naturalKeyDupStrip"])
		f.close()
	print str(data["count"]) + " attendance " + " checked."
	print str(data["naturalKeyDupStrip"]) + " Natural Key duplicates found"
	print str(data["count"] - data["naturalKeyDupStrip"]) + " unique attendance documents"
	if not checkEntities:
		print str(data["attendanceEventDupStrip"]) + " duplicate attendance events found"

	print "\nWithout Whitespace Stripped:"
	print str(data["naturalKeyDup"]) + " Natural Key duplicates found"
	print str(data["count"] - data["naturalKeyDup"]) + " unique attendance documents"
	print str(data["attendanceEventDup"]) + " duplicate attendance events found"
	if data["naturalKeyDupStrip"] != data["naturalKeyDup"]:
		print "Without Stripping Whitespace, different counts for natural keys of Attendance docs.  Check for leading or trailing whitespace in source system"
	if not checkEntities:
		if data["attendanceEventDupStrip"] != data["attendanceEventDup"]:
			print "Without Stripping Whitespace, different counts AttendanceEvent (subdocs).  Check for leading or trailing whitespace in source system"
	
	return (data["count"] - data["naturalKeyDup"])

	
if __name__ == '__main__':
	if len(sys.argv) <= 1: 
		print 'usage: ' + sys.argv[0] + ' datafile.xml ...' 
	else:
		unique = iterateThroughFiles(sys.argv[1:])
		