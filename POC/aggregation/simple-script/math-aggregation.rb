require 'rubygems'
require 'mongo'
require 'bson'
require 'thread'
require 'benchmark'

time = Benchmark.realtime do
  @mutex = Mutex.new
  @cv = ConditionVariable.new

  @conn = Mongo::Connection.new("localhost", 27017, :pool_size => 8, :pool_time => 5)
  @db = @conn['sli']
  @emit = nil

  @assmtIDCode = "Grade 7 2011 State Math";

  assessments = @db['assessment']
  @assessmentId = assessments.find_one('body.assessmentIdentificationCode.ID' => @assmtIDCode)

  if @assessmentId.nil? then
    puts("\"" + @assmtIDCode + "\" not in the SLI database. Nothing to aggregate.")
    exit(0)
  else
    @assessmentId = @assessmentId['_id']
  end
  puts(@assessmentId)

  @emitCount = 0

  def get_highest_ever_math_score
      count = 0

        coll = @db['student']
        saa = @db['studentAssessmentAssociation']

        coll.find.each { |row|
            saa.find('body.studentId' => row['_id']).each { |result|
                body = result['body']

                if !body.nil? && body['assessmentId'] == @assessmentId then
                  scores = body['scoreResults']
                  if !scores.nil? then
                    scores.each { |score|
                      if score['assessmentReportingMethod'] == 'Scale score' then
                        @mutex.synchronize do
                          @emit = [ row['_id'], score['result'].to_f() ]
                          @emitCount = @emitCount + 1
                          @cv.signal
                          Thread.pass
                          @cv.wait(@mutex)
                        end
                      end
                    }
                  end
                end
                Thread.pass
            }
        }
        @cv.signal
  end

  m1 = Thread.new { get_highest_ever_math_score() }

  @vals = Hash.new
  r = Thread.new {
    loop do
      @mutex.synchronize do
        @cv.wait(@mutex)
        if @emit.nil? == false then
          id = @emit[0]
          val = @emit[1]

          if @vals[id].nil? || val > @vals[id] then
            @vals[id] = val
          end
        end
        @cv.signal
      end
    end
  }

  m1.join()
  r.kill()

  # print(@vals)

  studentColl = @db['student']
  @vals.each_pair { |key, val|
    student = studentColl.find_one('_id' => key)
    studentColl.update({"_id" => key}, {"$set" => { "aggregations.assessments." + @assmtIDCode + ".HighestEver" => val.to_s()}})
  }


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

      @mutex.synchronize do
        if assessment.nil? then
          @emit = [row['_id'], nil]
        else
          @emit = [row['_id'], assessment['HighestEver'].to_f()]
        end
        @cv.signal
        Thread.pass
        @cv.wait(@mutex)
      end
      @emitCount = @emitCount + 1
    }
    @cv.signal
  end
end

@rvals = Hash.new
r = Thread.new {
  loop do
    @mutex.synchronize do
      @cv.wait(@mutex)
      if @emit.nil? == false then

        schools = @db['studentSchoolAssociation']
        school = schools.find_one('body.studentId' => @emit[0])
        schoolId = school['body']['schoolId']
        val = @emit[1]

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
      @cv.signal
    end
  end
}

t1 = Thread.new { emit_student_scores() }

t1.join()

puts(@rvals)

puts "Time elapsed #{time} seconds."
puts "Total emit operations: #{@emitCount}"
puts "Total reduce set: #{@vals.length + @rvals.length}"