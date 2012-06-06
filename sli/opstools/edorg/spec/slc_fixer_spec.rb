require_relative "../slc_fixer"

describe SLCFixer do
  # describe '#fix_students' do
  #   before(:each) do
  #     connection = Mongo::Connection.new('localhost', 27017)
  #     @db = connection['sli']
  #     @fixer = SLCFixer.new(@db)
  #     `cd ../../acceptance-tests;bundle exec rake importSandboxData`
  #     @fixer.fix_students
  #   end
  #   it "should update all students who are associated to a school" do
  #     failed = []
  #     @db['student'].find({'metaData.edOrgs'=> {'$exists' => false }}).each {|s| failed << s['_id']}
  #     assoc = []
  #     @db['studentSchoolAssociation'].find.each {|s| assoc << s['body']['studentId']}
  #     assoc.uniq!
  #     associated = @db['student'].count - assoc.size
  #     puts associated
  #     associated.should == failed.size
  #   end
  #   it "should not update a student if what exists matches what we found" do
  #     first_count = @fixer.count
  #     @fixer.fix_students
  #     final = first_count - @fixer.count
  #     final.should_not equal(first_count)
  #   end
  # end
  
  describe '#fix_staff' do
    before(:each) do
      connection = Mongo::Connection.new('localhost', 27017)
      @db = connection['sli']
      @fixer = SLCFixer.new(@db)
      `cd ../../acceptance-tests;bundle exec rake importSandboxData > /dev/null`
      @fixer.fix_staff
    end
    it "should update all staff who are associated to an edorg" do
      failed = []
      @db['staff'].find({'metaData.edOrgs'=> {'$exists' => false }}).each {|s| failed << s['_id']}
      assoc = []
      @db['staffEducationOrganizationAssociation'].find.each {|s| assoc << s['body']['staffReference']}
      assoc.uniq!
      unassociated = @db['staff'].count - assoc.size
      unassociated.should == failed.size
    end
  end
  describe '#start' do
    before(:each) do
      connection = Mongo::Connection.new('localhost', 27017)
      @db = connection['sli']
      @fixer = SLCFixer.new(@db)
      `cd ../../acceptance-tests;bundle exec rake importSandboxData > /dev/null`
      @fixer.start
    end
    it "should have stamped nearly everything with an edorg" do
      filtered = ['tenant', 'userSession', 'realm', 'userAccount', 'roles', 'realm', 'application', 'applicationAuthorization',
        'system.indexes', 'system.js', 'school']
      @db.collections.each do |collection|
        col_count = collection.count
        stamped_count = collection.find({'metaData.edOrgs' => {"$exists" => true}}).count
        col_count.should be >= stamped_count
        stamped_count.should be > 0, "#{collection.name} has nothing stamped" unless filtered.include? collection.name
      end
    end
  end
end