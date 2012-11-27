require_relative 'spec_helper'
require_relative '../lib/Shared/util.rb'

describe "Util" do
  describe "Generates correct interchange XML preamble for each supported entity" do
    describe "#build_header" do
      it "build an XML header" do
        header = build_header( "StudentParent")
        expected  = <<-HEADER
<?xml version="1.0"?>
<InterchangeStudentParent xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/SLI-Interchange-StudentParent.xsd ">
HEADER

        header.should eq expected
      end
    end
    describe "#build_footer" do
      it "builds an XML footer" do
        footer = build_footer( "StudentParent")
        expected = "</InterchangeStudentParent>"
        footer.should eq expected
      end
    end
  end

end