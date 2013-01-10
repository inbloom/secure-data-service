from xml.dom import minidom
import re
import sys

#script to pull documentation from SLI-Ed-Fi-Core.xsd

if len(sys.argv) < 2:
	print "usage: python genSchemaDoc.py <srcFile> > <targetFile>"
	sys.exit("Exiting")	

fileName = sys.argv[1]

def getDom(filename):
  "Read an xml file into a dom"
  file = open(filename)
  #convert to string:
  data = file.read()
  #close file because we dont need it anymore:
  file.close()
  dom = minidom.parseString(data) 
  return dom
  
def getText( node ):
  "Get the text element from the given node"
  nodes = node.childNodes
  for node in nodes:
    if node.nodeType == node.TEXT_NODE:
      return node.data
  
dom = getDom(fileName)
childNodes = dom.documentElement.childNodes

for node in childNodes:
	if node.localName == "annotation":
		element = node.getElementsByTagName("xs:documentation")[0]
		
		if element != None:
			text = getText(element)
			print "\n" + text + "\n"

	if node.localName == "complexType" or node.localName == "simpleType": 
		
		nameAtr = node.getAttributeNode('name')
		if nameAtr != None:
			entityName = str(nameAtr.nodeValue)
		
			annotation = node.getElementsByTagName("xs:annotation")[0]
		
			if annotation != None:	
				doc = node.getElementsByTagName("xs:documentation")[0]
				if doc != None:
					docText = getText(doc)
					if docText != None:
						print entityName
						print "\t" + docText
	


