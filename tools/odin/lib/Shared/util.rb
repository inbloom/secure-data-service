################### Should be moved into utilities file #########################################
def stripInterchange(interchangeName)
  interchangeName[11..-5]
end

## Creates a control file based on the xml files already generated
def genCtlFile(dir='generated')
  File.open("#{dir}/ControlFile.ctl", 'w+') do |f|
    Dir["#{dir}/*.xml"].each do |int|
      # Derive the Interchange type, name, and hash from the filename
      # --> Interchange<file_type>.xml
      # --> /Fully/qualified/path/<file_name>.xml
      # --> file_hash = md5(fully_qualified_file_name)
      file_type = stripInterchange(int[int.rindex('/')+1..-1])
      file_name = int[int.rindex('/')+1..-1]
      file_hash = Digest::MD5.file(int).hexdigest
      f.write "edfi-xml,#{file_type},#{file_name},#{file_hash}\n"
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

