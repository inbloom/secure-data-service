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
require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/staffAssociationInterchangeGenerator'
require_relative '../lib/OutputGeneration/XML/validator'
require 'factory_girl'

describe 'StaffAssociationInterchangeGenerator' do
  let(:path) { File.join( "#{File.dirname(__FILE__)}/", "../generated/InterchangeStaffAssociation.xml" ) }
  let(:interchange) { File.open( path, 'w')}
  let(:generator) {StaffAssociationInterchangeGenerator.new(interchange, 1)}
  let(:staff) {FactoryGirl.build(:staff)}

  describe '<<' do
    it 'will write a staff to edfi' do

      generator.start()
      puts staff.to_yaml
      generator << staff

      generator.finalize()

      validate_file( path ).should be true

    end
  end
end

