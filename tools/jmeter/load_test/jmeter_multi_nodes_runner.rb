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

JMETER_EXEC = "/usr/local/Cellar/jmeter/2.8/bin/jmeter"
JMETER_PROP = "/Users/dliu/sli_home/sli/tools/jmeter/local.properties"
JMETER_JMX_DIR = "/Users/dliu/sli_home/sli/tools/jmeter"
JMETER_JMXS = ["list-attendance.jmx","list-grades.jmx","list-sections.jmx","list-students.jmx","single-student.jmx","update-attendance.jmx","update-gradebooks.jmx"]
RESULT_DIR = "/Users/dliu/sli_home/sli/tools/jmeter/result"
REMOTE_SERVERS = ["localhost","10.71.1.45"]
RUN_NUMBERS = 20
CUT_OFF_TIME = 100000
LOGIN_THREAD = "Login 1-1"
REPORT_FILE = "test_report"

def clear_dir
  if File.directory? RESULT_DIR
    Dir.foreach(RESULT_DIR) {|f| fn = File.join(RESULT_DIR, f); File.delete(fn) if f != '.' && f != '..'}
  end
end

def run_jmeter(jmx_file,result_file, threads)
  result_fullpath = File.join(RESULT_DIR, result_file)
  File.delete(result_fullpath) if File.exist? result_fullpath

  jmx_fullpath = File.join(JMETER_JMX_DIR,jmx_file)
  puts REMOTE_SERVERS.join(',')
  pid = Process.spawn("#{JMETER_EXEC} -n -G #{JMETER_PROP} -t #{jmx_fullpath} -l #{result_fullpath} -Gthreads=#{threads} -Gloops=1 -R #{REMOTE_SERVERS.join(',')}")
  Process.wait(pid)
end

def get_results(filename)
  fullpath = File.join(RESULT_DIR, filename)
  doc = Nokogiri.XML(File.open(fullpath, 'rb'))

  results = {}
  doc.css('httpSample').each do |sample|
    sample = Sample.new(sample)
    results[sample.thread_name] = [] if results[sample.thread_name].nil?
    results[sample.thread_name] << sample
  end
  results
end

def get_login_time(samples)
login_samples = samples[LOGIN_THREAD]
login_time = 0
login_samples.each do |sample|
  login_time += sample.elapsed_time
end
login_time
end

def write_report(results,report_file_name)
report_fullpath = File.join(RESULT_DIR, report_file_name)
File.delete(report_fullpath) if File.exist? report_fullpath
File.open(report_fullpath, "w") do |f|
results.each do |key,value| 
f.printf "%20s\n", key
f.printf "%15s %25s\n", "threads/users", "average resp time(ms)"
value.each do |threads, resp_time|
f.printf "%15s %25s\n", threads, resp_time
end
end
end


end

 clear_dir
 results = {}
 JMETER_JMXS.each do |jmx_file|
 (1..RUN_NUMBERS).each do |n|
  threads = 2 ** n
  user_case = jmx_file.gsub('.jmx','')
  run_jmeter(jmx_file,user_case + threads.to_s, threads)

  samples = get_results(user_case + threads.to_s)
#  login_time = get_login_time(samples)
  results[user_case] = {} if results[user_case].nil?
  total_time = 0
  samples.each do |thread,req_samples|
  if thread != LOGIN_THREAD
  req_samples.each do |sample|
  total_time +=sample.elapsed_time
  end
  end
  end
#  average_resp_time = login_time + total_time/threads
  average_resp_time = 0
  average_resp_time = total_time/threads/REMOTE_SERVERS.size if REMOTE_SERVERS.size >0
  results[user_case] = results[user_case].merge({threads => average_resp_time})
  if average_resp_time > CUT_OFF_TIME || average_resp_time == 0
  break
  end
  #get_results("test#{threads}").each do |label, samples|
  #  samples.sort! {|x,y| x.elapsed_time <=> y.elapsed_time}
  #
  #  p "#{label} [#{samples[0].elapsed_time}, #{samples[samples.size/2].elapsed_time}, #{samples[-1].elapsed_time}]"
  #end
end

 results.each do |key,value|
 p key
 p value
 end
 end
 write_report(results,REPORT_FILE)
