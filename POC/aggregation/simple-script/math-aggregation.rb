require 'rubygems'
require 'mongo'
require 'bson'
require 'thread'
require 'benchmark'

time = Benchmark.realtime do
  @conn = Mongo::Connection.new("devparallax.slidev.org", 27017, :pool_size => 8, :pool_timeout => 5)
  @db = @conn['sli']
  @emit = nil
  @vals = Hash.new
  @rvals = Hash.new

  @assmtIDCode = "Grade 7 2011 State Math";

  assessments = @db['assessment']
  @assessment = assessments.find_one('body.assessmentIdentificationCode.ID' => @assmtIDCode)

  if @assessment.nil? then
    puts("\"" + @assmtIDCode + "\" not in the SLI database. Nothing to aggregate.")
    exit(0)
  else
    @assessmentId = @assessment['_id']
  end
  puts(@assessmentId)

  @emitCount = 0

  def get_highest_ever_math_score
      count = 0

      coll = @db['student']
      saa = @db['studentAssessmentAssociation']

      coll.find({'metaData.tenantId'=>1}, { :fields => {'body.studentId'=>1,'body.assessmentId'=>1,'body.scoreResults'=>1}}).each { |row|
        saa.find('body.studentId' => row['_id']).each { |result|
          body = result['body']

          if !body.nil? && body['assessmentId'] == @assessmentId then
            scores = body['scoreResults']
            if !scores.nil? then
              scores.each { |score|
                if score['assessmentReportingMethod'] == 'Scale score' then
                  @emit = [ row['_id'], score['result'].to_f() ]
                  @emitCount = @emitCount + 1
                  if (@emitCount % 1000) == 0 then
                    puts @emitCount.to_s()
                  end
                  reduce_highest_ever()
                end
              }
            end
          end
        }
      }
      write_highest_ever()
  end

  def reduce_highest_ever
    if @emit.nil? == false then
      id = @emit[0]
      val = @emit[1]

      if @vals[id].nil? || val > @vals[id] then
        @vals[id] = val
      end
    end
  end

  def write_highest_ever
    studentColl = @db['student']
    @vals.each_pair { |key, val|
      student = studentColl.find_one('_id' => key)
      studentColl.update({"_id" => key}, {"$set" => { "aggregations.assessments." + @assmtIDCode + ".HighestEver" => val.to_s()}})
    }
  end


  # roll up to the school level.
  def emit_student_scores
    coll = @db['student']
    assessment = nil
      coll.find.each { |row|
      if row['aggregations'].nil? == false then
        aggregations = row['aggregations']
        if aggregations['assessments'].nil? == false then
          assessments = aggregations['assessments']
          if assessments[@assmtIDCode].nil? == false then
            assessment = assessments[@assmtIDCode]
          end
        end
      end

      if assessment.nil? then
        @emit = [row['_id'], nil]
      else
        @emit = [row['_id'], assessment['HighestEver'].to_f()]
      end
      reduce_student_score()
      @emitCount = @emitCount + 1
    }
    write_school_aggregate()
end

def reduce_student_score
  if @emit.nil? == false then

    schools = @db['studentSchoolAssociation']
    school = schools.find_one('body.studentId' => @emit[0])

    if (school.nil? == false && school['body'].nil? == false && school['body']['schoolId'].nil? == false) then
      schoolId = school['body']['schoolId']
      val = @emit[1]
    else
      return
    end

    if @rvals[schoolId].nil? then
      @rvals[schoolId] = Hash.new
      @rvals[schoolId]['W'] = 0
      @rvals[schoolId]['B'] = 0
      @rvals[schoolId]['S'] = 0
      @rvals[schoolId]['E'] = 0
      @rvals[schoolId]['!'] = 0
      @rvals[schoolId]['-'] = 0
    end

    if val.nil? then
      @rvals[schoolId]['-'] = @rvals[schoolId]['-'] + 1
    elsif val < 6 || val > 33 then
      @rvals[schoolId]['!'] = @rvals[schoolId]['!'] + 1
    elsif val >= 6 && val <= 14 then
      @rvals[schoolId]['W'] = @rvals[schoolId]['W'] + 1
    elsif val >= 15 && val <= 20 then
      @rvals[schoolId]['B'] = @rvals[schoolId]['B'] + 1
    elsif val >=21 && val <= 27 then
      @rvals[schoolId]['S'] = @rvals[schoolId]['S'] + 1
    elsif val >= 28 && val <=33 then
      @rvals[schoolId]['E'] = @rvals[schoolId]['E'] + 1
    else
      @rvals[schoolId]['!'] = @rvals[schoolId]['!'] + 1
    end
  end
end

  def write_school_aggregate
    edOrgCollection = @db['educationOrganization']
    @rvals.each_pair { |key, val|
      edOrg = edOrgCollection.find_one('_id' => key)
      edOrgCollection.update({"_id" => key}, {"$set" => { "aggregations.assessments." + @assmtIDCode + ".Proficiency" => val }})
    }
  end

get_highest_ever_math_score()
emit_student_scores()

end

puts "Time elapsed #{time} seconds."
puts "Total emit operations: #{@emitCount}"
puts "Total reduce set: #{@vals.length + @rvals.length}"
