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
#require_relative '../lib/EntityCreation/student_builder'
require_relative '../lib/Shared/EntityClasses/student'

describe "Student" do
  context "Instantiate student" do
    let(:id) {41}
    let(:birthday) {Date.new(2000, 9, 1)}
    let(:student) {Student.new(id, birthday)}
    
    it "Can access the init objects of the student_builder instance" do
      student.id.should eq(41)
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
      grMethod = student.method(:race)
      student.distributionTester(grMethod, "White", 720, 780, 1000).should eq("true")
    end
      
    it "returns 15-25% hispanicLatino with true weight set to 20%" do
      hlMethod = student.method(:hispanicLatino)
      student.distributionTester(hlMethod, true, 150, 250, 1000).should eq("true")
    end
    
    it "returns 5-15% economicDisadvantaged with true weight set to 10%" do
      edMethod = student.method(:economicDisadvantaged)
      student.distributionTester(edMethod, true, 80, 120, 1000).should eq("true")
    end 
      
    it "returns 91-97% not limitedEnglish 'not limited' set to 94%" do
      leMethod = student.method(:limitedEnglish)
      student.distributionTester(leMethod, 'NotLimited', 910, 970, 1000).should eq("true")
    end
      
    it "returns 92-98% not disabled with weight set to 95%" do
      daMethod = student.method(:disability)
      student.distributionTester(daMethod, false, 920, 980, 1000).should eq("true")
    end  
      
    it "returns 68-72% schoolFood with 'Full price' set to 70%" do
      sfMethod = student.method(:schoolFood)
      student.distributionTester(sfMethod, 'Full price', 680, 720, 1000).should eq("true")
    end
    
    it "returns 1% Jr suffix" do
      suMethod = student.method(:suffix)
      student.distributionTester(suMethod, 'Jr', 1, 19, 1000).should eq("true")
    end
  end
end

