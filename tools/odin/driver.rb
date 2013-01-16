=begin

Copyright 2012 Shared Learning Collaborative, LLC

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
# uncomment this out to enable profiling.
#require 'perftools'
#PerfTools::CpuProfiler.start("/tmp/odin_profile")


require_relative 'lib/odin'

# Arg is assumed to be scenario name. If no name is provided, use what's specified in config.yml.
scenario = nil
if ARGV.length > 0
  tmp = ARGV.last();
  if File.file?("scenarios/#{tmp}")
    scenario = tmp
  else
    puts "Specified scenario (\"#{tmp}\") does not exist.\n"
    exit(1)
  end
  unless ARGV.index("--normalgc").nil?
    puts "disabling deffered garbage collection"
    DeferredGarbageCollector.disable
  end
end

o = Odin.new
o.generate( scenario )

# re-enable garbage collection if we disabled it elsewhere.
GC.enable
GC.start

#PerfTools::CpuProfiler.stop
