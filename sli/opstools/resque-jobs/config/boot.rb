require 'rubygems'
require 'rails/commands/server'


# Set up gems listed in the Gemfile.
gemfile = File.expand_path('../../Gemfile', __FILE__)
begin
  ENV['BUNDLE_GEMFILE'] = gemfile
  require 'bundler'
  Bundler.setup
rescue Bundler::GemNotFound => e
  STDERR.puts e.message
  STDERR.puts "Try running `bundle install`."
  exit!
end if File.exist?(gemfile)



module Rails
  class Server
    alias :default_options_alias :default_options
    def default_options
      default_options_alias.merge!(:Port => 4242)
    end    
  end
end