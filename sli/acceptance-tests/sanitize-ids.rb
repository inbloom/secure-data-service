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

require 'json'

# keep in sync with
# org.slc.sli.common.domain.EmbeddedDocumentRelations.java
$embeds = {
  "cohort" => ["studentCohortAssociation"],
  "program" => ["studentProgramAssociation"],
  "section" => ["gradebookEntry",
                "teacherSectionAssociation",
                "studentSectionAssociation"
               ],
  "student" => ["studentParentAssociation",
                "studentDisciplineIncidentAssociation",
                "studentAssessment"
               ]
}

$replacements = {}
$dupe_reps = {}

$UNDER_ID = "_id"
$PADDING = "padding"
$METADATA = "metaData"
$EDORGS = "edOrgs"
$TYPE = "type"


def sanitize parent
  #extract parent ID
  parent_id = parent[$UNDER_ID]
  parent_type = parent[$TYPE]

  if $embeds.include? parent_type
    # add _id to end of parentId, if necessary
    if !parent_id.end_with? $UNDER_ID
      new_parent_id = "#{parent_id}#{$UNDER_ID}"
      add_replacement(parent_id, new_parent_id)
      parent[$UNDER_ID] = new_parent_id
    end

    # ensure embedded entity _id's are good
    parent.each do |key, value|
      if is_embedded_type(parent_type, key, value)
        #for each embedded entity
        value.each do |embedded_entity|
          #extract embedded entity ID
          ee_id = embedded_entity[$UNDER_ID]

          if !id_starts_with_parent_id(ee_id, parent_id)
            #if parent_id not present in embedded
            ee_id = "#{parent_id}#{ee_id}"
          end

          #ensure ee_id ends with '_id'
          ee_id = "#{ee_id}#{$UNDER_ID}" if !ee_id.end_with? $UNDER_ID

          #replace parent_id with $replacements[parent_id]
          ee_id = do_replacement(ee_id, parent_id)

          #update embedded entity ID
          if ee_id != embedded_entity[$UNDER_ID]
            add_replacement(embedded_entity[$UNDER_ID], ee_id)
            embedded_entity[$UNDER_ID] = ee_id
          end
        end #embedded_entity iteration
      end #embedded key
    end #key,value iteration
  end

  #remove padding, if present
  if parent.include? $PADDING
    puts "Removed padding"
    parent.delete($PADDING)
  end

  #remove padding, if present
  if parent.include? $METADATA and parent[$METADATA].include? $EDORGS
    puts "Removed metaData.edOrgs"
    parent[$METADATA].delete($EDORGS)
  end

  parent
end


def add_replacement(old, new)
  if $replacements.include?old and $replacements[old] != new
    if $dupe_reps.include? old
      $dupe_reps[old] = $dupe_reps[old] + [new]
    else
      $dupe_reps[old] = [$replacements[old], new]
    end

    #the common cause of duplicate id is old non-embedded fixture files not being removed
    #let's keep the longer replacement, since the new ID will probably be embedded
    if new.length > $replacements[old].length
      $replacements[old] = new
    end
  else
    $replacements[old] = new
  end
end


def do_replacement(s, r)
  if $replacements.include? r
    s.gsub(r, $replacements[r])
  else
    s
  end
end


def id_starts_with_parent_id(ee_id, parent_id)
  ee_id.slice(0, parent_id.length) == parent_id
end


def sanitize_fixture_file file
  modified_lines = Array.new()
  error = false

  # read each line
  line_num = 0
  File.readlines(file).each do |line|
    line_num = line_num + 1
    if line.length >= 2
      dict = {}
      begin
        dict = JSON.parse line
        modified_lines << JSON.generate(sanitize(dict))
      rescue
        puts "JSON error while parsing #{file}:#{line_num}"
        error = true
      end
    end # length >= 2
  end #lines.each

  if !error
    File.open(file, 'w') { |f| modified_lines.each do |l|
        f.write(l)
        f.write("\n")
      end
    }
  end
end


def is_embedded_type(type, key, value)
  $embeds.include? type and $embeds[type].include? key and value.kind_of?(Array)
end


def find_fixture_files dir
  res = Array.new()
  Dir.glob("#{dir}/*").each do |f|
    if File.file?(f) and f.end_with? '.json'
      res << f
    elsif File.directory?(f)
      res.concat(find_fixture_files(f))
    end
  end
  res
end


def replace_ids dir

  Dir.glob("#{dir}/*").each do |f|
    if File.file?(f) and !f.end_with? '.zip' and !f.end_with? '.xml'
      #open file, check if anything in $replacements is in contents
      f_contents = File.read(f)

      puts f
      $replacements.each do |k, v|
        f_contents.gsub!(/(.)#{k}([^_])/, '\1' + v + '\2')
      end

      File.open(f, 'w') { |f| f.write(f_contents) }
      #if so, do replacement, rewrite
    elsif File.directory?(f)
      replace_ids(f)
    end
  end

end


def main(dir, rep)
  fixture_files = find_fixture_files dir
  fixture_files.each do |f|
    #don't look at 'sif/' files
    if !f.include? 'sif/'
      sanitize_fixture_file f
    end
  end

  replace_ids rep

  #print out duplicate replacements to help resolve failing acceptance tests
  File.open("dupe-replacements", 'w') { |f| f.write(JSON.pretty_generate($dupe_reps)) }
  File.open("replacements", 'w') { |f| f.write(JSON.pretty_generate($replacements)) }
end


if __FILE__ == $PROGRAM_NAME
  if ARGV.size != 2
    puts "Usage: ruby #{$PROGRAM_NAME} <data directory> <ID replacement directory>"
    exit
  end

  directory = ARGV[0]
  replace_path = ARGV[1]

  main(directory, replace_path)
end
