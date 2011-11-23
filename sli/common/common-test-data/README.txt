To generate xml interchange files from the source csv files, and validate them against Ed-Fi xsds, do the following:

mvn exec:exec xml:validate

The xml files will be output to target/xml/Interchange*.xml

Python2.5+ is required.