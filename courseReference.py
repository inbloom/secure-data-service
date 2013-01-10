from xml.dom import minidom
import re
import sys



sliRoot = "/Users/srichards/code/sli/sli/"
dataSetBaseDir = sliRoot + "acceptance-tests/test/features/ingestion/test_data/"
#dataSetDir = dataSetBaseDir + "StoriedDataSet_IL_Daybreak/"
#interchangeMasterSchedule_ = dataSetDir + "InterchangeMasterSchedule.xml"
#interchangeEducationOrganization_ = dataSetDir + "InterchangeEducationOrganization.xml"


 

def process():
  "Main function"
  
  dataSets = [
    #Old
    #("PartialIgestionDataSet_Tier6/","PartialIgestionDataSet_Tier5/"),
    #("SSDS_No_StudentAssessment/","SSDS_No_StudentAssessment/")

    #Done
    #("DashboardSadPath_IL_Daybreak/","DashboardSadPath_IL_Daybreak/")
    #("ingestion_IDReferences/","ingestion_IDReferences/")
    #("MediumSampleDataSet/","MediumSampleDataSet/")
    #("SmallSampleDataSet/","SmallSampleDataSet/")
    #("SSDS_No_StudentAssessment/","SSDS_No_StudentAssessment/")
    #("StoriedDataSet_NY/","StoriedDataSet_NY/")
    #("StudentGradeXsdValidation/","StudentGradeXsdValidation/")
    #("DemoData/","DemoData/")
    #("DemoDataLindaNBMTest/","DemoDataLindaNBMTest/")
    #("DirPathnameInCtlFile/","DirPathnameInCtlFile/")
    #("Parallel_IL_Daybreak/","Parallel_IL_Daybreak/")
    #("Reingest-StoriedDataSet_IL_Daybreak/","Reingest-StoriedDataSet_IL_Daybreak/")
    #("StoriedDataSet_IL_Daybreak/","StoriedDataSet_IL_Daybreak/")
    #("StoriedDataSet_IL_Sunset/","StoriedDataSet_IL_Sunset/")
    #("StudentTranscriptAssociation/","StudentTranscriptAssociation/")
    #("ZipContainsSubfolder/DataFiles/","ZipContainsSubfolder/DataFiles/")
    #("StoriedDataSet_IL_Daybreak_Append/","StoriedDataSet_IL_Daybreak/")
    ("SubDocOutOfOrder_section/","SubDocOutOfOrder_section/")

    #Unnecessary
    #("complexObjectArrayIdResolutionTest/","complexObjectArrayIdResolutionTest/"),
    #("DailyAttendance/","DailyAttendance/")

    #Problematic

    #Weird
    #("PartialIgestionDataSet_Tier3/","PartialIgestionDataSet_Tier2/")
    #("PartialIgestionDataSet_Tier7/","PartialIgestionDataSet_Tier2/")
    
    
  ]
  
  for ds in dataSets:
    interchangeEducationOrganization = dataSetBaseDir + ds[1] + "InterchangeEducationOrganization.xml"

    interchangeMasterSchedule = dataSetBaseDir + ds[0] + "InterchangeMasterSchedule.xml"
    #processDataSet(interchangeMasterSchedule,interchangeEducationOrganization) 
    #interchangeMasterSchedule = dataSetBaseDir + ds[0] + "InterchangeStudentGrade.xml"
    #processDataSet(interchangeMasterSchedule,interchangeEducationOrganization) 
    #interchangeMasterSchedule = dataSetBaseDir + ds[0] + "StudentTranscriptAssociation.xml"
    #processDataSet(interchangeMasterSchedule,interchangeEducationOrganization) 
    #interchangeMasterSchedule = dataSetBaseDir + ds[0] + "StudentTranscriptAssociation2.xml"
    #processDataSet(interchangeMasterSchedule,interchangeEducationOrganization) 
    #interchangeMasterSchedule = dataSetBaseDir + ds[0] + "CourseOffering.xml"
    processDataSet(interchangeMasterSchedule,interchangeEducationOrganization) 
  
  
  
def processDataSet(interchangeMasterSchedule,interchangeEducationOrganization):

  print "Processing: %s" % interchangeMasterSchedule
  
  # parse xml
  masterScheduleDom = getDom( interchangeMasterSchedule )
  edOrgDom = getDom( interchangeEducationOrganization )
  
  # load elements
  #coList = getTopLevelElementsByTagName(edOrgDom,"CourseOffering")
  #ctList = getTopLevelElementsByTagName(edOrgDom,"CourseTranscript")
  courseRefList = masterScheduleDom.getElementsByTagName("CourseReference")
  courseList = getTopLevelElementsByTagName(edOrgDom,"Course")

  # get student section assoc beginDates
  courseCodeMap = {}
  for course in courseList: 
    id = genCourseId(course)
    courseCode = getTextByName(course,"ID")
    courseCodeMap[courseCode] = id
    print courseCode
    print id
  print "Finished generating courseCodeMap"
  # Iterate through CourseRefs. Create new ref and replace old one
  for oldCourseRef in courseRefList:
    courseId = getTextByName(oldCourseRef, "ID")
    if courseId == None:
      print "NON-Reference found"
      continue
    
    # look up course
    course = courseCodeMap[ courseId ]
       
    #print courseId in courseCodeMap
     
    if (courseId in courseCodeMap) == False:
      print "Removing item"
      print prettyString(course)
      parent = oldCourseRef.parentNode
      parent.removeChild( oldCourseRef )
      continue

    split = course.find("#")
    uniqueCourseCode = course[:split]
    stateOrganizationId = course[split+1:]

    newCourseRef = masterScheduleDom.createElement("CourseReference")
    courseIdent = masterScheduleDom.createElement("CourseIdentity")
    newCourseRef.appendChild(courseIdent)
    
    newEdOrgRef = masterScheduleDom.createElement("EducationalOrgReference")
    courseIdent.appendChild(newEdOrgRef)
    
    newEdOrgIdent = masterScheduleDom.createElement("EducationalOrgIdentity")
    newEdOrgRef.appendChild(newEdOrgIdent)
    
    soidNode = masterScheduleDom.createElement("StateOrganizationId")    
    soidNode.appendChild( masterScheduleDom.createTextNode(stateOrganizationId) )
    newEdOrgIdent.appendChild(soidNode)

    uniqueCourseCodeNode = masterScheduleDom.createElement("UniqueCourseId")    
    uniqueCourseCodeNode.appendChild( masterScheduleDom.createTextNode(uniqueCourseCode) )
    courseIdent.appendChild(uniqueCourseCodeNode)

    parent = oldCourseRef.parentNode
    parent.replaceChild( newCourseRef, oldCourseRef )
    
    #studentCompetencyObjectiveReferenceClone = studentCompetencyObjectiveReference.cloneNode(True)
    #print len(studentCompetencyObjectiveReference)
    #print prettyString(newSCRef)
    #newSCRef = course.cloneNode(True)
    
    
    
    
  
  
  
  prettyXml = prettyString(masterScheduleDom)
  
  #print prettyXml
  print "Transformed: %s" % interchangeMasterSchedule
  #return
  outFile = open(interchangeMasterSchedule, "w")
  outFile.write(prettyXml)
  outFile.close()
    
    


def prettyString(dom):
  txt = dom.toprettyxml(indent='    ')
  uglyXml = minidom.parseString(txt).toprettyxml(indent='    ')
  text_re = re.compile('>\n\s+([^<>\s].*?)\n\s+</', re.DOTALL)    
  prettyXml = text_re.sub('>\g<1></', uglyXml)
 
  ns_re = re.compile('ns0:', re.DOTALL)
  #prettyXml = ns_re.sub('', prettyXml)
  
  newLines = []
  lines = prettyXml.split("\n")
  for line in lines:
    if line.strip() != "":
      newLines.append(line)
  
  prettyXml = "\n".join(newLines)
  
  return prettyXml



def getDom( filename ):
  "Read an xml file into a dom"
  file = open(filename)
  #convert to string:
  data = file.read()
  #close file because we dont need it anymore:
  file.close()
  dom = minidom.parseString(data) 
  return dom




def getTopLevelElementsByTagName( dom, tagName ):
  "Since getElementsByTagName looks at descendents, this just looks at top level"
  childNodes = dom.documentElement.childNodes
  list = []
  for node in childNodes:
    if node.localName == tagName:
      list.append(node)
  return list  

def getTextByName( node, tagName ):
  "Get text node of the first element with a given element name"
  element = node.getElementsByTagName(tagName)[0]
  text = getText(element)
  return text
  

def buildIdMap( entityList ):
  idMap = {}
  for entity in entityList:
    idAtr = entity.getAttributeNode('id')    
    if idAtr != None:
      idMap[ idAtr.nodeValue ] = entity
  return idMap
  

def getText( node ):
  "Get the text element from the given node"
  nodes = node.childNodes
  for node in nodes:
    if node.nodeType == node.TEXT_NODE:
      return node.data
  
  
def genCourseId( node ):
  "Generate a 'key' for a Course"
  edOrgId = getTextByName(node,"StateOrganizationId")
  courseCode = getTextByName(node,"UniqueCourseId")
  return "%s#%s" % (courseCode, edOrgId)



if __name__ == "__main__":
  process()


