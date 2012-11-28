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

require_relative "../lib/Shared/EntityClasses/enum/GradeLevelType.rb"

# specifications for grade level type functions
describe "GradeLevelType" do
  describe "Correctly translates grades into strings" do
  	describe "for symbol that identifies Kindergarten" do
  	  it "will return the string representation for Kindergarten" do
  	  	kindergarten = :KINDERGARTEN
  	  	GradeLevelType.get(kindergarten) == "Kindergarten"
  	  end
    end
  end
  
  describe "Correctly returns sets of grades" do
    describe "--> elementary schools" do
      it "will return the set of grades: K - 5" do
        grades = GradeLevelType.elementary
        grades.include? :KINDERGARTEN == true
        grades.include? :FIRST_GRADE == true
        grades.include? :SECOND_GRADE == true
        grades.include? :THIRD_GRADE == true
        grades.include? :FOURTH_GRADE == true
        grades.include? :FIFTH_GRADE == true
        grades.include? :SIXTH_GRADE == false
        grades.include? :SEVENTH_GRADE == false
        grades.include? :EIGHTH_GRADE == false
        grades.include? :NINTH_GRADE == false
        grades.include? :TENTH_GRADE == false
        grades.include? :ELEVENTH_GRADE == false
        grades.include? :TWELFTH_GRADE == false
      end
    end

    describe "--> middle schools" do
      it "will return the set of grades: 6 - 8" do
        grades = GradeLevelType.middle
        grades.include? :KINDERGARTEN == false
        grades.include? :FIRST_GRADE == false
        grades.include? :SECOND_GRADE == false
        grades.include? :THIRD_GRADE == false
        grades.include? :FOURTH_GRADE == false
        grades.include? :FIFTH_GRADE == false
        grades.include? :SIXTH_GRADE == true
        grades.include? :SEVENTH_GRADE == true
        grades.include? :EIGHTH_GRADE == true
        grades.include? :NINTH_GRADE == false
        grades.include? :TENTH_GRADE == false
        grades.include? :ELEVENTH_GRADE == false
        grades.include? :TWELFTH_GRADE == false
      end
    end

    describe "--> high schools" do
      it "will return the set of grades: 9 - 12" do
        grades = GradeLevelType.middle
        grades.include? :KINDERGARTEN == false
        grades.include? :FIRST_GRADE == false
        grades.include? :SECOND_GRADE == false
        grades.include? :THIRD_GRADE == false
        grades.include? :FOURTH_GRADE == false
        grades.include? :FIFTH_GRADE == false
        grades.include? :SIXTH_GRADE == false
        grades.include? :SEVENTH_GRADE == false
        grades.include? :EIGHTH_GRADE == false
        grades.include? :NINTH_GRADE == true
        grades.include? :TENTH_GRADE == true
        grades.include? :ELEVENTH_GRADE == true
        grades.include? :TWELFTH_GRADE == true
      end
    end
  end

  describe "Correctly orders grades" do
    describe "--> getting set of ordered grades" do
      it "wlll return appropriately ordered grades" do
        grades = GradeLevelType.get_ordered_grades
        grades[0] == :KINDERGARTEN
      end
    end
  end

  describe "Correctly identifies grade levels" do
  	describe "--> elementary school grades" do
  	  it "will return true for each elementary school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade) == true
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade) == false
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade) == false
  	  	end
  	  end
  	end

  	describe "--> middle school grades" do
  	  it "will return true for each middle school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade) == false
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade) == true
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade) == false
  	  	end
  	  end
  	end

  	describe "--> high school grades" do
  	  it "will return true for each high school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade) == false
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade) == false
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade) == true
  	  	end
  	  end
  	end
  end

  describe "Handles edge cases" do
    describe "--> checking if grade is an elementary, middle, or high school grade" do
      it "will handle null input" do
      	GradeLevelType.is_elementary_school_grade(nil) == false
      	GradeLevelType.is_middle_school_grade(nil) == false
        GradeLevelType.is_high_school_grade(nil) == false
      end
    end
  end
end