#!/usr/bin/ruby

#
# Generate StudentAssessment XML data from .CSV data file
# 
# Adapted from the original Python version, gen.py, created by
# J. Hinsdale of Wireless Generation 20Feb2013
#

require 'pp'

#
# Main driver
#
def main()

  # This has a mapping of "Last,First" to the StudentUniqueStateId
  name2id = load_map('student_map.rb')

  # This has a mapping of assessment scores to color code
  level2color = load_map('level_color_map.rb')

  # This has a mapping of school year, period, and reporting method to administration date
  schoolyear_period2admin_date_map = load_map('admindate_map.rb')

  # Various input files.  Emit header, then templates populated with data, then footer
  header_fn = "header.xml"
  footer_fn = "footer.xml"
  template_fn = "template.xml"
  data_fn = "input.csv"
    
  print slurp_file(header_fn)
  print "\n"

  # Map sheet column number to corresponding short assessment name
  assessment_short_name = {
    8 => "DIBELS Next Composite",
    10 => "DORF (Fluency)",
    12 => "DORF (Accuracy)",
    14 => "DORF (Retell)",
    16 => "DORF (Retell Quality)",
    18 => "DORF (Daze)",
    20 => "TRC",
  }

  # Get XML template with substitutable variables
  template = slurp_file(template_fn)

  # For each line in .CSV input, 
  count = 0
  File.open(data_fn) do |data_file|
    data_file.each_line do |line|

      # Ignore column header line
      count += 1
      if count == 1
        next
      end

      # Extract relevent spreadsheet columns
      line.strip!
      flds = line.split(",")
      school_year = flds[0]
      lname = flds[3]
      fname = flds[4]
      grade = flds[6]
      period = flds[7]

      # Map school year to a short representation for use in title
      if school_year == "2011-2012"
        short_school_year = "11-12"
      elsif school_year == "2012-2013"
        short_school_year = "12-13"
      else
        raise "Unsupported grade '" + grade + "'"
      end

      # Map grade to grade_level string representation
      if grade == "4"
        grade_level = "Fourth grade"
        title_grade_level = "Fourth Grade"
      elsif grade == "5"
        grade_level = "Fifth grade"
        title_grade_level = "Fifth Grade"
      else
        raise "Unsupported grade '" + grade + "'"
      end
      
      # Map student name to ID
      student_id = name2id[lname + "," + fname]

      # Map benchmark period to administration date
      admin_date_map = schoolyear_period2admin_date_map[school_year][period]
      
      # Generate a <StudentAssessment> block for each of the repeating assessment/score column pairs
      pair_start = [ 8, 10, 12, 14, 16, 18, 20 ]
      for p in pair_start
        short_name = assessment_short_name[p]
        level = flds[p]
        color = level2color[level]
        score = flds[p + 1]
        title = short_name + " " + title_grade_level + " " + short_school_year + " " + period
        comment = fname + " " + lname + " (" + student_id.to_s() + ") / " + short_name + " / " + period

        # Composite assessments should have a later administration date
        if p == 8
          admin_date = admin_date_map["composite"]
        else
          admin_date = admin_date_map["standard"]
        end

        # TRC assessments should use Mastery level, DIBELS Scale score for reporting_method
        if p == 20
          reporting_method = "Mastery level"
        else
          reporting_method = "Number score"
        end

        # __AdministrationDate__     BOY = 2012-10-01   MOY 2013-02-01
        # __AssessmentTitle__      "<assmt short name> <gradelevel> 12-13 <bench period>"
        # __Score__
        # __Color__
        # __Level__
        # __StudentId__            Map from first, last name
            
        # Apply substitutions to XML template and print it
        xml = template
        subst_map = [
          [ "__AdministrationDate__", admin_date ],
          [ "__AssessmentTitle__", title ],
          [ "__AssessmentReportingMethod__", reporting_method ],
          [ "__Score__", score ],
          [ "__Level__", level ],
          [ "__Color__", color ],
          [ "__StudentId__", student_id ],
          [ "__Comment__", comment ],
          [ "__GradeLevel__", grade_level ],
        ]
        for subst_pair in subst_map
            xml = xml.gsub(subst_pair[0], subst_pair[1].to_s())
        end
        
        print xml
        print "\n"

      end
    end
  end

  # Footer
  print slurp_file(footer_fn)
  print "\n"
  
end

##
## Slurp in contents of file to a string
##
def slurp_file(fn)
  result = File.read(fn)
  # Binary version
  # result = File.open("file", "rb") {|io| io.read}
  return result
end

#
# Load map (really, any expression) given as code in file named by fn and return value
#
def load_map(fn)
  return eval(slurp_file(fn))
end

# Call main driver
main()
