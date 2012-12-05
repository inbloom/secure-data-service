## Tests the student parent generator with mock entities

require_relative '../../lib/OutputGeneration/XML/studentGenerator.rb'

describe "StaffAssociationInterchangeGenerator" do
  context "create a new instance of Student" do
    let(:id) {41}
    let(:birthday) {Date.new(2000, 9, 1)}
    let(:student) {Student.new(id, birthday)}

    it "#generate" do
      students = [ :student ]
        studentParent = StudentParentInterchangeGenerator::StudentGenerator.new( students)
        studentParent.template_path = "#{File.dirname(__FILE__)}/interchangeTemplates"
        File.open("generated/SeanInterchangeStudent.xml", 'w') do |studentParentFile|
 
       studentParentFile << studentParent.render
       end
    end

  end
end

