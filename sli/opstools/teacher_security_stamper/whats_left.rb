require File.dirname(__FILE__) + '/slc_fixer'
`cd ../../acceptance-tests;bundle exec rake importSandboxData > /dev/null`
connection = Mongo::Connection.new('localhost', 27017, :pool_size => 5, :pool_timeout => 5, :safe => {:wtimeout => 500})
log = Logger.new(STDOUT)
log.level = Logger::WARN
db = connection['sli']
fixer = SLCFixer.new(db, log)
fixer.start
skip = ['system.indexes', 'application', 'aggregationDefinition', 'realm', 'applicationAuthorization', 'realm', 'roles', 'system.js', 'tenant', 'assessment', 'learningStandard', 'learningObjective', 'sectionSchoolAssociation', 'educationOrganizationAssociation', 'educationOrganizationSchoolAssociation', 'error', 'securityEvent', 'courseSectionAssociation', 'teacher', 'school', 'userSession', 'custom_entities', 'userAccount', 'adminDelegation', 'calendarDate', 'studentCompetencyObjective']
stamped = {}
unstamped = []
db.collection_names.sort.each do |name|
  if !skip.include? name
    exists = db[name].find({'metaData.teacherContext' => {'$exists' => true}}).count
    stamped[name] = db[name].find({'metaData.teacherContext' => {'$exists' => false}}).count if exists > 0
    unstamped << name if exists == 0
  end
end

puts "Collections that are stamped:\t\tRemaining:"
width = 40
stamped.each do |s, count|
  gap = ""
  (width - s.length).times {|i| gap << " "}
  puts "\t#{s}#{gap}#{count}"
end
puts "Unstamped"
unstamped.each {|s| puts "\t #{s}"}
