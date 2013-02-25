#!/usr/bin/env ruby

`ruby startEverything.rb`

puts "\n\nStarting Smoke Tests\n"
pid = Process.spawn('bundle exec rake smokeTests')
Process.wait(pid)
