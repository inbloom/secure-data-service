require 'nokogiri'

class Sample
  attr_accessor :elapsed_time, :latency, :timestamp, :success_flag, :label, :response_code, :response_message,
                :thread_name, :data_type, :bytes
  def initialize(sample)
    hash = {}
    sample.each do |pair|
      hash[pair[0]] = pair[1]
    end

    @elapsed_time = hash["t"].to_i
    @latency = hash["lt"].to_i
    @timestamp = hash["ts"].to_i
    @success_flag = (hash["s"] == "true")
    @label = hash["lb"]
    @response_code = hash["rc"]
    @response_message = hash["rm"]
    @thread_name = hash["tn"]
    @data_type = hash["dt"]
    @bytes = hash["by"].to_i

    @label = "simple-idp" if @label.include?("simple-idp")
  end
end

JMETER_EXEC = "/Users/joechung/apache-jmeter-2.8/bin/jmeter"
JMETER_PROP = "/Users/joechung/gitrepo/sli/tools/jmeter/joe_local.properties"
JMETER_JMX = "/Users/joechung/gitrepo/sli/tools/jmeter/single-student.jmx"
RESULT_DIR = "/Users/joechung/gitrepo/sli/tools/jmeter/result"

def clear_dir
  if File.directory? RESULT_DIR
    Dir.foreach(RESULT_DIR) {|f| fn = File.join(RESULT_DIR, f); File.delete(fn) if f != '.' && f != '..'}
  end
end

def run_jmeter(filename, threads)
  fullpath = File.join(RESULT_DIR, filename)
  File.delete(fullpath) if File.exist? fullpath

  pid = Process.spawn("#{JMETER_EXEC} -n -q #{JMETER_PROP} -t #{JMETER_JMX} -l #{fullpath} -Jthreads=#{threads}")
  Process.wait(pid)
end

def get_results(filename)
  fullpath = File.join(RESULT_DIR, filename)
  doc = Nokogiri.XML(File.open(fullpath, 'rb'))

  results = {}
  doc.css('httpSample').each do |sample|
    sample = Sample.new(sample)
    results[sample.label] = [] if results[sample.label].nil?
    results[sample.label] << sample
  end
  results
end

#10.times do |x|
#  n_threads = x * 10
#  run_jmeter("test#{n_threads}", n_threads)
#end

results = {}
(1..10).each do |n|
  threads = n * 5
  samples = get_results("test#{threads}")["Get student by ID"]
  results[:categories] = [] if results[:categories].nil?
  results[:min] = [] if results[:min].nil?
  results[:median] = [] if results[:median].nil?
  results[:max] = [] if results[:max].nil?
  results[:categories] << threads
  samples.sort! {|x,y| x.elapsed_time <=> y.elapsed_time}
  results[:min] << samples[0].elapsed_time
  results[:median] << samples[samples.size/2].elapsed_time
  results[:max] << samples[-1].elapsed_time
  #get_results("test#{threads}").each do |label, samples|
  #  samples.sort! {|x,y| x.elapsed_time <=> y.elapsed_time}
  #
  #  p "#{label} [#{samples[0].elapsed_time}, #{samples[samples.size/2].elapsed_time}, #{samples[-1].elapsed_time}]"
  #end
end

p results