if (ARGV.size != 1)
  puts " How to use... "
  puts " ./schema-upversion-all.rb <XSD File>"
  exit
end

filename = ARGV[0]

xsd = File.read(filename)
upversioned_xsd = xsd.gsub(%r{<sli:schemaVersion>\d*</sli:schemaVersion>}, '<sli:schemaVersion>999999</sli:schemaVersion>')
File.open(filename, 'w') {|f| f.write(upversioned_xsd)}
