import os
import re
import csv
from optparse import OptionParser

def getFeatureFiles(path):
	files = []
	for dirname, dirnames, filenames in os.walk(path):
	    for filename in filenames:
			if filename[-7:]=='feature':
				files.append(os.path.join(dirname, filename))
	return files

def makeTagMap(feature_files, path):
	result = {}
	for feature in feature_files:
		for line in open(feature):
			tagMatch = re.findall('@\w+', line)
			if len(tagMatch) > 0:
				for x in tagMatch:
					key = x
					if key[:7] == '@RALLY_': key = key[7:]
					if key in result and feature not in result[key]:
						result[key].append(feature[len(path):])
					else:
						result[key] = [feature[len(path):]]
	return result

def makeFeatureMap(feature_files, path):
	refeature = re.compile(r'(Feature:.+)')
	result = {}
	for feature in feature_files:
		foundFeatureLine = False
		for line in open(feature):
			tagMatch = refeature.search(line)
			if tagMatch:
				result[feature[len(path):]] = [tagMatch.group(1)]
				foundFeatureLine = True
				break
	return result
	
def topHtmlTable(out, hdr):
	out.write( '<html><head><link href="coffee-with-milk.css" rel="stylesheet" type="text/css"></head><table>' )
	out.write( '<thead><tr>' )
	for col in hdr:
		out.write( '<th>'+col+'</th>' )
	out.write( '<th> Tests </th>' )
	out.write( '</tr></thead>' )
	out.write( '<tbody>' )

def rowHtmlTable(out, row):
	out.write( '<tr>' )
	for col in row:
		out.write( '<td>'+col+'</td>' )
	
def rowEndHtmlTable(out):
	out.write( '</tr>' )
	
def bottomHtmlTable(out):
	out.write( '</tbody></table></html>' )
		
if __name__=='__main__':
	# Usage and options handling
	usage = "Usage: %prog [options] csvfile"
	parser = OptionParser(usage=usage)
	parser.add_option("-p", "--path", dest="path",
	                  help="Specify path to feature files", default=".")
	(options, args) = parser.parse_args()

	# Get all the feature files in path
	files = getFeatureFiles(options.path)
	# Create a map of RALLY tags in feature files to the files
	tags = makeTagMap(files, options.path)
	# Create a map of feature files to feature descriptions
	features = makeFeatureMap(files, options.path)

	# Open the two output files
	tracematrix = open('trace_matrix.html', 'w')
	problems = open('no_tests_matrix.html', 'w')
	
	# Open the input CSV file
	csvfile = open(args[0], "rU")
	table_rows = csv.reader(csvfile)
	header = table_rows.next()
	
	# Create top part of the two output files
	topHtmlTable(tracematrix, header)
	topHtmlTable(problems, header)

	for row in table_rows:
		# Determine set of features that apply for all the stories in this row
		canonical_feature_set = []
		stories = row[3].split(' ')
		for s in stories:
			if s in tags:
				for f in tags[s]:
					if f not in canonical_feature_set: canonical_feature_set.append(f)
		rowHtmlTable(tracematrix, row)
		if len(canonical_feature_set) == 0:
			# What? No tests! That's a problem
			rowHtmlTable(problems, row)
			rowEndHtmlTable(problems)
		else:
			tracematrix.write( '<td>' )
			for f in canonical_feature_set:
				feature_desc = ''
				if f in features: feature_desc = features[f][0]
				tracematrix.write( f+'<br/>'+feature_desc+'<br/><br/>' )
			tracematrix.write( '</td>' )
			rowEndHtmlTable(tracematrix)
		
	bottomHtmlTable(tracematrix)
	bottomHtmlTable(problems)
	
	tracematrix.close()
	problems.close()
	