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

#    @education_organization_writer = EducationOrganizationGenerator.new
  end

  def create(rand, yaml)
  	if !yaml["studentCount"].nil?
  		create_edOrgs_from_students(rand, yaml)
  		update_edOrgs_for_time(rand, yaml)
  	end
  end

  def create_edOrgs_from_students(rand, yaml)
  	num_students = yaml["studentCount"]
  	begin_year   = yaml["beginYear"]
  	num_years    = yaml["numYears"]
  	
  	puts "Initial conditions for creating world:"
  	puts "\tnumber of students: #{num_students}"
  	puts "\tbegin year:         #{begin_year}"
  	puts "\tnumber of years:    #{num_years}"
	
  	puts "edOrgs: #{@edOrgs}"
  	# { stateOrganizationId:2, parent:1, sessions:[], teachers:[], staff:[] }
  end

  def create_from_schools(rand, yaml)
  	puts "Initial conditions for creating world:"
  	puts "\tnumber of schools: #{num_schools}"
  	puts "\tbegin year:        #{begin_year}"
  	puts "\tnumber of years:   #{num_years}"
  end

  def update_edOrgs_for_time(rand, yaml)
  	begin_year   = yaml["beginYear"]
  	num_years    = yaml["numYears"]
  	
  	# todo: need to create leas before this encapsulates year iteration loop
  	#for lea in @edOrgs["leas"] do
  	#end
  	for year in begin_year..(begin_year+num_years-1) do
  		puts " [info] creating session information for year: #{year}-#{year+1}"
  		# create session for leas
  		# -> create grading periods for session
  		# -> create calendar dates for grading period
  		# iterate through schools and determine with some probability if school overrides existing lea session
  	end
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
