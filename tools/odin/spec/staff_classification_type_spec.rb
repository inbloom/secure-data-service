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

require_relative "../lib/Shared/EntityClasses/enum/StaffClassificationType.rb"

# specifications for staff classification type functions
describe "StaffClassificationType" do
  describe "Correctly translates staff classification types into strings" do
    describe "--> symbol that identifies Leader (doesn't exist)" do
      it "will return nil" do
        StaffClassificationType.to_string(:LEADER).should be_nil
      end
    end
    describe "--> symbol that identifies Principal (exists)" do
      it "will return the string representation for Principal" do
        StaffClassificationType.to_string(:PRINCIPAL).should match("Principal")
      end
    end
  end

  describe "Correctly translates strings into staff classification types" do
    describe "--> string that identifies Leader (doesn't exist)" do
      it "will return nil" do
        StaffClassificationType.to_symbol("Leader").should be_nil
      end
    end
    describe "--> string that identifies Principal" do
      it "will return the staff classification type for Principal" do
        StaffClassificationType.to_symbol("Principal").should eq(:PRINCIPAL)
      end
    end
  end  
end
