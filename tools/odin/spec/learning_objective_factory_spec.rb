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

require_relative 'spec_helper'
require_relative '../lib/Shared/EntityClasses/enum/GradeLevelType'
require_relative '../lib/Shared/util'
require_relative '../lib/WorldDefinition/learning_objective_factory'

# specifications for learning objective factory
describe "LearningObjectiveFactory" do

  let(:scenario) { {'NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE' => 3} }
  let(:factory)  { LearningObjectiveFactory.new(scenario) }

  describe "#build_learning_objectives" do
    describe "--> for any elementary school grade" do
      it "will build a map with 6 academic subject keys, each containing specified number of learning objectives" do
        GradeLevelType.elementary.each do |grade|
          objectives = factory.build_learning_objectives(grade)
          objectives.keys.size.should eq 6
          objectives.each do |subject, learning_objectives|
            learning_objectives.size.should eq 3
            learning_objectives.each do |learning_objective|
              learning_objective.grade.should   eq grade
              learning_objective.subject.should eq subject
            end
          end
        end
      end
    end

    describe "--> for any middle school grade" do
      it "will build a map with 10 academic subject keys, each containing specified number of learning objectives" do
        GradeLevelType.middle.each do |grade|
          objectives = factory.build_learning_objectives(grade)
          objectives.keys.size.should eq 10
          objectives.each do |subject, learning_objectives|
            learning_objectives.size.should eq 3
            learning_objectives.each do |learning_objective|
              learning_objective.grade.should   eq grade
              learning_objective.subject.should eq subject
            end
          end
        end
      end
    end

    describe "--> for any high school grade" do
      it "will build a map with 21 academic subject keys, each containing specified number of learning objectives" do
        GradeLevelType.high.each do |grade|
          objectives = factory.build_learning_objectives(grade)
          objectives.keys.size.should eq 21
          objectives.each do |subject, learning_objectives|
            learning_objectives.size.should eq 3
            learning_objectives.each do |learning_objective|
              learning_objective.grade.should   eq grade
              learning_objective.subject.should eq subject
            end
          end
        end
      end
    end
  end

  describe "#negative testing" do
    let(:scenario) { {'NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE' => nil} }
    describe "--> failing to find NUM_LEARNING_OBJECTIVES_PER_SUBJECT_AND_GRADE property for elementary school grade" do
      it "will build a map with 6 academic subject keys, each containing 2 learning objectives" do
        GradeLevelType.elementary.each do |grade|
          objectives = factory.build_learning_objectives(grade)
          objectives.keys.size.should eq 6
          objectives.each do |subject, learning_objectives|
            learning_objectives.size.should eq 2
            learning_objectives.each do |learning_objective|
              learning_objective.grade.should   eq grade
              learning_objective.subject.should eq subject
            end
          end
        end
      end
    end
  end
end