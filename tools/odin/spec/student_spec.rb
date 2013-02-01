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
require_relative '../lib/Shared/EntityClasses/student.rb'
require_relative 'spec_helper'

describe "Student" do
  context "create a new instance of Student" do
    let(:id) {41}
    let(:birthday) {Date.new(2000, 9, 1)}
    let(:student) {Student.new(id, birthday)}
    
    it "Can access the init objects of the student_builder instance" do
      student.id.should eq(41)
      student.year_of
      student.rand.should_not be_nil
    end
    
    it "Can access demographics.state class variable" do
      student.state.should eq("NY")
    end
    
    it "will choose gender" do
      student.sex.should match(/^Male|^Female/)
    end
      
    it "will match the city in choices.yml" do
      student.city.should eq("New York")
    end

    it "will match the HARDCODED zip code"  do
      student.postalCode.should eq("10292")
    end

    it "should generate a first name" do
      student.firstName.should_not be_nil
    end
    
    it "should have a valid email that ends in fakemail.com" do
      student.email.should match(/.*?@fakemail.com/)
    end
    
    it "should have a login id that ends in fakemail.com" do
      student.loginId.should match(/.*?@fakemail.com/)
    end
    
    it "should have a valid address in demographics" do
      student.address.should match(/[0-9]+\s[a-z|A-z]+/)
    end

    it "should return a boolean value for disability" do
      student.disability.should_not be_nil
    end
    
    it "should return student id of 41" do
      student.id.should eq(41)
    end
    
    it "should choose the race of a student from list" do
      student.race.should_not be_nil
    end
    
    it "returns 55-65% white with white weighted distribution set to 60%" do
      distributionTester(:race, "White", 720, 780, 1000).should eq("true")
    end
      
    it "returns 15-25% hispanicLatino with true weight set to 20%" do
      distributionTester(:hispanicLatino, true, 150, 250, 1000).should eq("true")
    end
    
    it "returns 5-15% economicDisadvantaged with true weight set to 10%" do
      distributionTester(:economicDisadvantaged, true, 80, 120, 1000).should eq("true")
    end 
      
    it "returns 91-97% not limitedEnglish 'not limited' set to 94%" do
      distributionTester(:limitedEnglish, 'NotLimited', 910, 970, 1000).should eq("true")
    end
      
    it "returns 92-98% not disabled with weight set to 95%" do
      distributionTester(:disability, false, 920, 980, 1000).should eq("true")
    end  
      
    it "returns 68-72% schoolFood with 'Full price' set to 70%" do
      distributionTester(:schoolFood, 'Full price', 680, 720, 1000).should eq("true")
    end
    
    it "returns 1% Jr suffix" do
      distributionTester(:suffix, 'Jr', 1, 19, 1000).should eq("true")
    end
  end
end

def distributionTester(inMethod, tracer, lo, hi, iters)
  i = 0
  hit = 0
  (0..iters).each{|i|
    s = Student.new(i, Date.new(2000, 9, 1))
    if s.method(inMethod).call == tracer
      hit += 1
    end
    i += 1
  }

  if hit.between?(lo, hi)
    puts "PASS: #{tracer} was #{hit}/#{iters}, expected %0.1f \%" % ((((hi.to_f+lo)/2)/iters) * 100)
    return "true"
  else
    puts "FAIL: #{tracer} was #{hit}/#{iters}, expected %0.1f \%" % ((((hi.to_f+lo)/2)/iters) * 100)
    return "false"
  end
end
