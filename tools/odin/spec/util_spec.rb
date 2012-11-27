require_relative 'spec_helper'
require_relative '../lib/Shared/util.rb'

describe "Util" do
  describe "Generates correct interchange XML preamble for each supported entity" do
    describe "#build_header_footer" do
      it "build an XML header and footer" do
        header, footer = build_header_footer( "StudentParent")
        expected_header  = <<-HEADER
<?xml version="1.0"?>
<InterchangeStudentParent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/SLI-Interchange-StudentParent.xsd ">
HEADER

        header.should eq expected_header
        
        expected_footer = "</InterchangeStudentParent>"
        footer.should eq expected_footer
        
      end
    end
   
  end

end