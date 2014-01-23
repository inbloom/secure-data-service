#!/usr/bin/env ruby
#This will generate a client_id and client_key for a new inbloom application. This is useful when configuring API to bootstrap the initial applications
#Client_id is 10 chars, a mix of numbers, and upper/lower case letters
#client_secret is 48 characters, a mix of numbers, and upper/lower case letters
puts "Client_ID: #{Array.new(10){[*'0'..'9', *'a'..'z', *'A'..'Z'].sample}.join}"
puts "Client_Secret: #{Array.new(48){[*'0'..'9', *'a'..'z', *'A'..'Z'].sample}.join}"
