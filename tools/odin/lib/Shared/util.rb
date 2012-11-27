################### Should be moved into utilities file #########################################
def stripInterchange(interchangeName)
  interchangeName[11..-5]
end

def genCtlFile(dir='generated')
  File.open("generated/ControlFile.ctl", 'w') do |f|
    Dir["#{File.dirname(__FILE__)}/#{dir}/*.xml"].each do |int|
    # I don't like how I get the ingestion file type below
      f.write "edfi-xml,#{stripInterchange(int[int.rindex('/')+1..-1])},#{int[int.rindex('/')+1..-1]},#{Digest::MD5.file(int).hexdigest}\n"
    end
  end
end

## Builds a header and footer for the given interchange XML file.

def build_header_footer( interchange_name )

  header = <<-HEADER
<?xml version="1.0"?>
<Interchange#{interchange_name} xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://ed-fi.org/0100"
xsi:schemaLocation="http://ed-fi.org/0100 ../../sli/edfi-schema/src/main/resources/edfiXsd-SLI/SLI-Interchange-#{interchange_name}.xsd">
HEADER

  footer = "</Interchange#{interchange_name}>"

  return header, footer
end

