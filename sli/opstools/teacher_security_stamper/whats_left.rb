require File.dirname(__FILE__) + '/slc_fixer'
`cd ../../acceptance-tests;bundle exec rake importSandboxData > /dev/null`
connection = Mongo::Connection.new('localhost', 27017, :pool_size => 5, :pool_timeout => 5, :safe => {:wtimeout => 500})
log = Logger.new(STDOUT)
log.level = Logger::WARN
db = connection['sli']
fixer = SLCFixer.new(db, log)
fixer.start
skip = ['system.indexes', 'application', 'aggregationDefinition', 'realm', 'applicationAuthorization', 'realm', 'roles', 'system.js', 'tenant', 'assessment', 'learningStandard', 'learningObjective', 'educationOrganization', 'sectionSchoolAssociation', 'educationOrganizationAssociation', 'educationOrganizationSchoolAssociation', 'error', 'securityEvent', 'courseSectionAssociation']
stamped = []
unstamped = []
db.collection_names.each do |name|
  if !skip.include? name
    exists = db[name].find({'metaData.teacherContext' => {'$exists' => true}}).count
    stamped << name if exists > 0
    unstamped << name if exists == 0
  end
end

puts "Collections that are stamped:"
stamped.each {|s| puts "\t #{s}"}
puts "Unstamped"
unstamped.each {|s| puts "\t #{s}"}
