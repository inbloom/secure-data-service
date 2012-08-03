# A basic driver for a delta stamper.
#
# It has a few parameters that tell it about the queue it should be listening
# to and it should respond to the pop_work method that returns a hash that
# looks like this:
#  { type: "student", id: "blahblah", tenant: test, wrap_up: true}
#
# It uses two methods to do its' job, the first is get_work which will run
# poll the queue constantly for work using exponential backoff. If it finds
# work it will call the do_work method with the work hash that was explained
# above.
#
# That method will create the stamper based on the "type" in the work hash and
# set up that delta stamper and set it to go.
class DeltaStamper
  require 'stamper'
  require 'mongo'
  
  attr_accessor :queue, :db_host, :db_port, :database

  def initialize(db_host = 'localhost', db_port = 27017, database = 'sli', queue = nil)
    @queue = queue
    @db_host = db_host
    @db_port = db_port
    @database = database
    @threads = []
    @log = Logger.new(STDOUT)
    @log.level = Logger::WARN
  end
  def get_work(forever = true)
    timeout = 2
    while (forever)
      work = nil || queue.pop_work if queue
      timeout = 2 unless work.nil?
      @threads << Thread.new { do_work(work) unless work.nil? }
      if work.nil?
        sleep(timeout)
        timeout **= 2 unless timeout >= 3600
      end
    end
  end
  def do_work(work = {})
    db = Mongo::Connection.new(@db_host, @db_port)[@database]
    stamper = nil
    case work[:type]
    when "student"
     stamper = StudentStamper.new(db, work[:id], work[:tenant])
    end
    if !stamper.nil?
      stamper.should_wrap_up = work[:wrap_up]
      stamper.stamp
    else
      @log.error "No stamper exists for #{work[:type]}"
    end
  end
end


stamper = DeltaStamper.new
