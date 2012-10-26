from xml.dom import minidom
import re
import sys

#get parameters for splitter
#if len(sys.argv) < 5:
#	print "usage: python splitter.py <srcFile> <targetFile> <entityName> <splitNodeName>"
#	print "e.g. python splitter.py InterchangeStudentCohort.xml InterchangeStudentCohort_split.xml StaffCohortAssociation StaffReference"
#	sys.exit("Exiting")

fileName = sys.argv[1]
##newFileName = sys.argv[2]
#entityName = sys.argv[3]
#elementToSplitOn = sys.argv[4]


def getDom( filename ):
  file = open(filename)
  #convert to string:
  data = file.read()
  #close file because we dont need it anymore:
  file.close()
  dom = minidom.parseString(data) 
  return dom

dom = getDom( fileName )


uglyXml = dom.toprettyxml(indent='    ')


text_re = re.compile('>\n\s+([^<>\s].*?)\n\s+</', re.DOTALL)    
prettyXml = text_re.sub('>\g<1></', uglyXml)


newLines = []
lines = prettyXml.split("\n")
for line in lines:
  if line.strip() != "":
    newLines.append(line)

prettyXml = "\n".join(newLines)



#print fixed_output

outFile = open(fileName, "w")
outFile.write(prettyXml)
outFile.close()
  
sys.exit("Exiting")	





gradeRefs = studentGradeDom.getElementsByTagName("GradeReference")

recordsAdded = 0

for entity in gradeRefs:
	#get the elements that we will split on
	
	atr = entity.getAttributeNode('ref')
	if atr != None:
	  print atr
	  print atr.nodeValue
	continue
	





gradeRefs = studentGradeDom.getElementsByTagName("GradeReference")

recordsAdded = 0

for entity in gradeRefs:
	#get the elements that we will split on
	
	atr = entity.getAttributeNode('ref')
	if atr != None:
	  print atr
	  print atr.nodeValue
	continue
	
	splitElements = entity.getElementsByTagName(elementToSplitOn)
	
	#copy the entity dom and remove the split element
	newEntitySkeleton = entity.cloneNode(True)
	for splitElement in newEntitySkeleton.getElementsByTagName(elementToSplitOn):
		parentNode = splitElement.parentNode
		parentNode.removeChild(splitElement)
	
	recordsAdded = recordsAdded + len(splitElements) - 1
	
	#print "\n\n" + entityName + " num staffReferences = " + str(len(references)) + ", chort refs = " + str(len(cohortReferences)) + " :"
	for splitElement in splitElements:
		#create the new entity
		newSplitElement = splitElement.cloneNode(True)
		newEntity = newEntitySkeleton.cloneNode(True)
		newEntity.appendChild(splitElement)
		
		#add newEntity to entity.parentNode
		entity.parentNode.insertBefore(newEntity, entity)
		
	#remove entity from entity.parentNode
	entity.parentNode.removeChild(entity)
	
	

	
uglyXml = dom.toprettyxml(indent='  ')
text_re = re.compile('>\n\s+([^<>\s].*?)\n\s+</', re.DOTALL)    
prettyXml = text_re.sub('>\g<1></', uglyXml)

outFile = open(newFileName, "w")
outFile.write(prettyXml)
outFile.close()

#get rid of empty lines that are left by formatter
fd = open(newFileName)
contents = fd.readlines()
fd.close()

new_contents = []

# Strip empty lines
for line in contents:
    if not line.strip():
        continue
    else:
        new_contents.append(line)

# Print file sans empty lines
output = "".join(new_contents)
outFile = open(newFileName, "w")
outFile.write(output)
outFile.close()

print "Added " + str(recordsAdded) + " " + entityName + " records"