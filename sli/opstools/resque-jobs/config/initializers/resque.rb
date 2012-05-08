ENV["REDISTOGO_URL"] ||= "redis://localhost:6379/"

uri = URI.parse(ENV["REDISTOGO_URL"])
Resque.redis = Redis.new(:host => uri.host, :port => uri.port, :password => uri.password)

Dir["#{Rails.root}/app/jobs/*.rb"].each { |file| require file }

require 'resque_scheduler'
Resque.schedule = YAML.load_file("#{Rails.root}/config/resque_schedule.yml")
