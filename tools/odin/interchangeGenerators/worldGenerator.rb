=begin

Copyright 2012 Shared Learning Collaborative, LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=end

# World Generator
# -> intent is to create 'scaffolding' that represents a detailed, time-sensitive view of the world
#
# generation strategies:
# (1) create world using number of students + time information (begin year, number of years)
# (2) create world using number of schools  + time information (begin year, number of years) [not supported]
class WorldGenerator
  def initialize
    @edOrgs = Hash.new
    @edOrgs["sea"]        = []
    @edOrgs["leas"]       = []
    @edOrgs["elementary"] = []
    @edOrgs["middle"]     = []
    @edOrgs["high"]       = []
    
    # Initialize work order writer(s)
#    @education_organization_writer = EducationOrganizationGenerator.new
  end

  def create(rand, yaml)
        
    # Generate the initial world object (seas, leas, schools, session, gradingPeriod, calendarDate, section, couseOffering, course)
    build_infrastructure(rand, yaml)
    
    # Populate the initial state of world (student, studentSchooassoc, studentSectionAssoc)
    populate(rand, yaml)

  end

  def build_infrastructure(rand, yaml)
    if !yaml["studentCount"].nil?
      create_edOrgs_from_students(rand, yaml)
    end
  end  

  def create_edOrgs_from_students(rand, yaml)
    num_students = yaml["studentCount"]

    puts "Initial conditions for creating world:"
    puts "\tnumber of students: #{num_students}"
  
    puts "edOrgs: #{@edOrgs}"
    # { stateOrganizationId:2, parent:1, sessions:[], teachers:[], staff:[] }
  end
  
  def populate(rand, yaml)
    # Allocate staff to the infrastructure entities
    # populate_staff(rand, yaml)
    
    # Initial allocation of students to the infrastructure entities
    populate_students(rand, yaml)
  end
  
  def populate_students(rand, yaml)
    # Allocate students to the infrastructure entities
  end
    
  def simulate(rand, yaml)
    begin_year   = yaml["beginYear"]
    num_years    = yaml["numYears"]
    
    if begin_year.nil?
      puts "beginYear scenario property not set. Using default (2012)"
      begin_year = 2012
    end

    if num_years.nil?
      puts "numYears scenario property not set. Using default (1)"
      num_years = 1
    end

    # loop over years updating infrastructure and population
    for year in begin_year..(begin_year+num_years-1) do
      puts " [info] simulating information for year: #{year}-#{year+1}"

      # Calculate changes to the infrastructure for the year
      step_infrastructure(year, rand, yaml)
      
      # Calculate changes to the population for the year
      step_population(year, rand, yaml)
      
      # Incremental work order write could go here to clear previous year specific state
      # may end up passing an workOrder writer to the "step*" methods
      update_work_order(year)
    end

    # Output any remaining world entities to the work order(s)
    finalize_work_order()
    
  end
  
  def step_infrastructure(year, rand, yaml)
    # Update infrastructure entities for the year
    puts " [info] creating session information for year: #{year}-#{year+1}"
    # create session for leas
    # -> create grading periods for session
    # -> create calendar dates for grading period
    # iterate through schools and determine with some probability if school overrides existing lea session
  end

  def step_population(year, rand, yaml)
    # Update staff related entities for the year
    # step_staff(year, rand, yaml)

    # Update student related entities for the year
    step_student(year, rand, yaml)
  end
  
  def step_student(year, rand, yaml)
    # Update studentSchoolAssociation meta-data based on configuration
    # Update studentSectionAssociation meta-data based on configuration
    # Update any other student specific entity meta-data based on configuration
  end
  
  def update_work_order(year)
    # Write out work order entries which are no longer required after the year has been processed
  end
  
  def finalize_work_order()
    # Write out remaining work order entries
  end
  
  def get_number_of_elementary_schools(students)
  	return 1
  end

  def get_number_of_middle_schools(students)
  	return 1
  end

  def get_number_of_high_schools(students)
  	return 1
  end

  def get_number_of_students(schools)
  	return 500
  end
end
