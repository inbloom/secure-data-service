=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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

def stripInterchange(interchangeName)
  interchangeName[11..-5]
end

def runShellCommand(command)
  `#{command}`
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

## Loads the scenario default values from 'scenario' and applies the specified scenario overrides
## Returns the resulting YAML file.
def load_scenario (scenario_name, configYAML)
  scenarioDefaults = YAML.load_file(File.join(File.dirname(__FILE__), '/../../scenarios/defaults/base_scenario'))

  if ( scenario_name.nil? )
    scenario_name = configYAML['scenario']
  end

  scenarioYAML = scenarioDefaults.merge!(YAML.load_file(File.join(File.dirname(__FILE__), '/../../scenarios', scenario_name )))
end

## Zips up the Interchange.xml files and .ctl file in odin/generated dir
## The relative directory is such that this should be called from tools/odin/lib/odin.rb
def genDataZip(dir='generated', file_name='OdinSampleDataSet.zip', zip_dir='generated')
  # make sure the target file does not already exist
  # if it does, rename it to a tmp file
  runShellCommand("zip -j #{dir}/#{file_name} #{zip_dir}/*.xml #{zip_dir}/*.ctl")
end

def genDataUnzip(dir='generated', file_name='OdinSampleDataSet.zip', unzip_dir='OdinSampleDataSet')
  runShellCommand("unzip #{dir}/#{file_name} -d #{dir}/#{unzip_dir}")
end
