require 'net/http'
require 'rexml/document'

# extract event information
f = File.open("/Users/dkornishev/Documents/git/sli/sli/domain/target/classes/sliXsd/ComplexTypes.xsd")
doc = REXML::Document.new(f)

total=0
appInfoPresent=0
appInfoAbsent=0

doc.elements.each('//xs:element') do |elem|
   hasAppInfo = elem.get_elements('xs:annotation/xs:appinfo')
   puts "#{elem.attribute("name")} -> #{hasAppInfo.size}" 
   
   elem.elements.each('xs:annotation/xs:appinfo') do |appInfo|
     puts appInfo.text
   end
   
   total+=1
   
  if hasAppInfo.size>0
    appInfoPresent+=1
  else
    appInfoAbsent+=1
  end    
end

puts total
puts appInfoPresent
puts appInfoAbsent