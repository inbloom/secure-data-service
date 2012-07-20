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
      end
    end
  }

  m1.join()

  # print(@vals)

  studentColl = @db['student']
  @vals.each_pair { |key, val|
    student = studentColl.find_one('_id' => key)
    studentColl.update({"_id" => key}, {"$set" => { "aggregations.assessments." + @assmtIDCode + ".HighestEver" => val.to_s()}})
  }
end

puts "Time elapsed #{time} seconds."
puts "Total emit operations: #{@emitCount}"
puts "Total reduce set: #{@vals.length}"