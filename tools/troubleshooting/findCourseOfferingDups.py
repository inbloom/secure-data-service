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

#This only works for CourseOffering
#This script is meant for debugging purposes.  It is not produciton quality

import hashlib
import sys
import xml.etree.cElementTree as et
import re

whitespaceToStrip = ' \t\n'

def initData(data):
	data["count"] = 0
	data["dup"] = 0
	data["hashes"] = {}
	data["dupXML"] = {}

def get_namespace(element):
  m = re.match('\{.*\}', element.tag)
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

def compareAndStoreHash(data, hstring, xmlnode):
	try:
		hash = hashlib.sha256(hstring).hexdigest()
		if hash in data["hashes"]:
			data["dup"] += 1
			data["hashes"][hash] += 1
			if (hash not in data["dupXML"]):
				data["dupXML"][hash] = []
		else:
			data["hashes"][hash] = 1
	except UnicodeEncodeError:
		print(sys.exc_info())
		#print et.tostring(xmlnode)

def collectDuplicates(data, hstring, xmlnode, filename):
	hash = hashlib.sha256(hstring).hexdigest()
	if hash in data["dupXML"]:
		data["dupXML"][hash].append(et.tostring(xmlnode) + "The record above is from " + filename)



def createCourseOfferingHashes(source, data, strip=True, report=False):
	tree = et.parse(source)	
	root = tree.getroot()
	print(root)
	for courseOffering in root:
		data["count"] += 1
		localCourseCode = u""
		schoolId = u""
		sessionName = u""
		sessionSchoolId = u""
		for child in courseOffering:
			#print et.tostring(child)
			if child.tag.lower().endswith('localcoursecode'):
				localCourseCode = child.text
				if strip:
					localCourseCode = localCourseCode.strip(whitespaceToStrip).encode("UTF-8")
				else:
					localCourseCode = localCourseCode.encode("UTF-8")
			elif child.tag.lower().endswith('schoolreference'):
				if strip:
					schoolId = getStateEdOrgId(child).strip(whitespaceToStrip).encode("UTF-8")
				else:
					schoolId = getStateEdOrgId(child).encode("UTF-8")
			elif child.tag.lower().endswith('sessionreference'):
				ns =  get_namespace(child)
				SessionIdentity = child.find("{0}SessionIdentity".format(ns))
				SessionName = SessionIdentity.find("{0}SessionName".format(ns))
				if strip:
					sessionName = SessionName.text.strip(whitespaceToStrip).encode("UTF-8")
				else:
					sessionName = SessionName.text.encode("UTF-8")
				sessionSchoolId = getStateEdOrgId(SessionIdentity.find("{0}EducationalOrgReference".format(ns))).encode("UTF-8")
		if len(localCourseCode) < 1:
			print("Empty CourseCode")
		if len(schoolId) < 1:
			print("Empty SchoolId")
		if len(sessionName) < 1:
			print("Empty sessionName")
		hstring = localCourseCode + schoolId + sessionName
		
		#print hstring
		if report:
			collectDuplicates(data, hstring, courseOffering, source.name)
		else:
			compareAndStoreHash(data, hstring, courseOffering)

def iterateThroughFiles(entityType, fileList):	  
	data = {}
	initData(data)
	for i in range(0,len(fileList)):
		print("Processing File: " + fileList[i])
		f = open(fileList[i])
		createCourseOfferingHashes(f, data, strip=True)
		f.close()
	print(str(data["count"]) + " " + entityType + " checked.")
	print(str(data["dup"]) + " duplicates found")
	print(str(data["count"] - data["dup"]) + " unique entities")
	if data["dup"] > 0:
		data["count"] = 0
		print("Printing Duplicates")
		for i in range(0,len(fileList)):
			print("Processing File: " + fileList[i])
			f = open(fileList[i])
			createCourseOfferingHashes(f, data, strip=True, report=True)
			f.close()
		for hash in data["dupXML"]:
			print("The following " + str(data["hashes"][hash]) + " records are duplicates")
			for xml in data["dupXML"][hash]:
				print (xml)
		print(str(data["count"]) + " " + entityType + " checked.")
		print(str(data["dup"]) + " duplicates found")
		print(str(data["count"] - data["dup"]) + " unique entities")

	
if __name__ == '__main__':
	validEntityTypes = ["courseoffering"]
	if len(sys.argv) <= 1: 
		print('usage: ' + sys.argv[0] + ' datafile.xml ...')
	else:
		iterateThroughFiles("CourseOffering", sys.argv[1:])