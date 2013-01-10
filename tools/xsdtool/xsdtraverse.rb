require 'net/http'
require 'rexml/document'

def getType(elem)
  puts "inspecting #{elem.name}"
  if elem.name == "complexType"
    return elem
  else
    return getType(elem.parent)
  end
end

# extract event information
f = File.open("/Users/dkornishev/Documents/git/sli/sli/domain/target/classes/sliXsd/ComplexTypes.xsd")
doc = REXML::Document.new(f)

doc.elements.each('//xs:element') do |elem|
  appinfoArray = elem.get_elements('xs:annotation/xs:appinfo')
  puts "#{elem.attribute("name")} -> #{appinfoArray.size}"

  if appinfoArray.size>0
    appinfo = appinfoArray[0]
  else
    puts "adding appinfo"
    appinfo = REXML::Element.new("xs:appinfo",elem.get_elements("xs:annotation")[0])
  end

  if appinfo.get_elements("sli:WriteEnforcement").empty?
    puts "adding write enforcement"
    writeEnforcement = REXML::Element.new("sli:WriteEnforcement",appinfo)
    writeEnforcement.add_text("WRITE_GENERAL");
  end

  puts appinfo.get_elements("sil:WriteEnforcement").size

  if appinfo.get_elements("sli:ReadEnforcement").empty?
    puts "adding read enforcement"
    readEnforcement = REXML::Element.new("sli:ReadEnforcement",appinfo)
    readEnforcement.add_text("READ_GENERAL")
  end

  publicEntities = ["assessment", "learningObjective", "learningStandard", "school", "educationOrganization"]

  type = getType(elem)
  puts type.attribute("name")
  if publicEntities.include? type.attribute("name").to_s
    appinfo.get_elements("sli:WriteEnforcement")[0].text=("WRITE_PUBLIC")
  end

end

f = File.open("ComplexTypes2.xsd","w")
f.write(doc.to_s)
f.close()

puts "ALL DONE"

