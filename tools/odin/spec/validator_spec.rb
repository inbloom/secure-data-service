require_relative 'spec_helper'
require_relative '../lib/OutputGeneration/XML/validator'

describe "Validator" do
  context "InvalidXML" do

    it "should detect Invalid Ed-Fi XML" do
      valid = validate_file (  File.join( "#{File.dirname(__FILE__)}", "test_data/invalid/InterchangeStudentParent.xml") )
      valid.should be false
    end
  end
end
