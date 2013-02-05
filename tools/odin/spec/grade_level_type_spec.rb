=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
  	  	GradeLevelType.to_string(:KINDERGARTEN).should match("Kindergarten")
  	  end
    end
  end
  
  describe "Correctly returns sets of grades" do
    describe "--> elementary schools" do
      it "will return the set of grades: K - 5" do
        grades = GradeLevelType.elementary
        grades.include?(:KINDERGARTEN).should be true
        grades.include?(:FIRST_GRADE).should be true
        grades.include?(:SECOND_GRADE).should be true
        grades.include?(:THIRD_GRADE).should be true
        grades.include?(:FOURTH_GRADE).should be true
        grades.include?(:FIFTH_GRADE).should be true
        grades.include?(:SIXTH_GRADE).should be false
        grades.include?(:SEVENTH_GRADE).should be false
        grades.include?(:EIGHTH_GRADE).should be false
        grades.include?(:NINTH_GRADE).should be false
        grades.include?(:TENTH_GRADE).should be false
        grades.include?(:ELEVENTH_GRADE).should be false
        grades.include?(:TWELFTH_GRADE).should be false
      end
    end

    describe "--> middle schools" do
      it "will return the set of grades: 6 - 8" do
        grades = GradeLevelType.middle
        grades.include?(:KINDERGARTEN).should be false
        grades.include?(:FIRST_GRADE).should be false
        grades.include?(:SECOND_GRADE).should be false
        grades.include?(:THIRD_GRADE).should be false
        grades.include?(:FOURTH_GRADE).should be false
        grades.include?(:FIFTH_GRADE).should be false
        grades.include?(:SIXTH_GRADE).should be true
        grades.include?(:SEVENTH_GRADE).should be true
        grades.include?(:EIGHTH_GRADE).should be true
        grades.include?(:NINTH_GRADE).should be false
        grades.include?(:TENTH_GRADE).should be false
        grades.include?(:ELEVENTH_GRADE).should be false
        grades.include?(:TWELFTH_GRADE).should be false
      end
    end

    describe "--> high schools" do
      it "will return the set of grades: 9 - 12" do
        grades = GradeLevelType.high
        grades.include?(:KINDERGARTEN).should be false
        grades.include?(:FIRST_GRADE).should be false
        grades.include?(:SECOND_GRADE).should be false
        grades.include?(:THIRD_GRADE).should be false
        grades.include?(:FOURTH_GRADE).should be false
        grades.include?(:FIFTH_GRADE).should be false
        grades.include?(:SIXTH_GRADE).should be false
        grades.include?(:SEVENTH_GRADE).should be false
        grades.include?(:EIGHTH_GRADE).should be false
        grades.include?(:NINTH_GRADE).should be true
        grades.include?(:TENTH_GRADE).should be true
        grades.include?(:ELEVENTH_GRADE).should be true
        grades.include?(:TWELFTH_GRADE).should be true
      end
    end
  end

  describe "Correctly orders grades" do
    describe "--> getting set of ordered grades" do
      it "wlll return appropriately ordered grades" do
        grades = GradeLevelType.get_ordered_grades
        grades[0].should be :KINDERGARTEN
      end
    end
  end

  describe "Correctly identifies grade levels" do
  	describe "--> elementary school grades" do
  	  it "will return true for each elementary school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade).should be true
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade).should be false
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_elementary_school_grade(grade).should be false
  	  	end
  	  end
  	end

  	describe "--> middle school grades" do
  	  it "will return true for each middle school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade).should be false
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade).should be true
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_middle_school_grade(grade).should be false
  	  	end
  	  end
  	end

  	describe "--> high school grades" do
  	  it "will return true for each high school grade and false otherwise" do
  	  	GradeLevelType.elementary.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade).should be false
  	  	end
  	  	GradeLevelType.middle.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade).should be false
  	  	end
  	  	GradeLevelType.high.each do |grade|
  	  	  GradeLevelType.is_high_school_grade(grade).should be true

  	  	end
  	  end
  	end
  end

  describe "--> translation between grades" do
    it "will return 9th grade when adding 1 to 8th grade" do
      eighth = :EIGHTH_GRADE
      ninth  = GradeLevelType.increment(eighth, 1)
      ninth.should be :NINTH_GRADE
    end

    it "will return 9th grade when subtracting 1 from 10th grade" do
      tenth  = :TENTH_GRADE
      ninth  = GradeLevelType.decrement(tenth, 1)
      ninth.should be :NINTH_GRADE
    end

    it "will return nil when adding any number to 12th grade" do
      twelfth    = :TWELFTH_GRADE
      thirteenth = GradeLevelType.increment(twelfth, 1)
      thirteenth.should be nil
      GradeLevelType.increment(:ELEVENTH_GRADE, 2).should be nil
    end

    it "will return nil when subtracting any number from Kindergarten" do
      kindergarten = :KINDERGARTEN
      preschool    = GradeLevelType.decrement(:KINDERGARTEN, 1)
      preschool.should be nil
      GradeLevelType.decrement(:FIRST_GRADE, 2).should be nil
    end
  end

  describe "Handles edge cases" do
    describe "--> checking if grade is an elementary, middle, or high school grade" do
      it "will handle null input" do
      	GradeLevelType.is_elementary_school_grade(nil).should be false
      	GradeLevelType.is_middle_school_grade(nil).should be false
        GradeLevelType.is_high_school_grade(nil).should be false
      end
    end
  end
end
