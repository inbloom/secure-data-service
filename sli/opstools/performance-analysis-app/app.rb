require 'sinatra'
require_relative 'models/report'

configure do
  set :oauth_id     => "cjC1hCfED3"
  set :oauth_secret => "c2P4Cj4laLiHmC78Zde689iTs0T5hJCTjHOd4XcwtZIoJiRO"
  set :schoolId => ""  
  set :type => "staff" 
end

enable :sessions

get '/' do
  login_uri = "#{api_uri}/oauth/authorize?Realm=SandboxIDP&response_type=code&client_id=#{settings.oauth_id}&redirect_uri=#{app_auth_uri}"
  redirect login_uri
end

get '/dashboard' do
    erb :dashboard
end

get '/generateReport' do
  Report.generate_latest_report()
end

get '/generateEndPointReport/*' do
  Report.generate_end_point_report params[:splat]
end

get '/generateEndPointReportForBuild/*' do
  puts params[:splat]
  Report.generate_end_point_report_for_build params[:splat]
end

post '/clear' do
  session.clear
end

def api_uri
  'https://api.sandbox.slcedu.org/api'
end

def app_uri
  'http://localhost:9393'
end

def client
  @@client ||= RestHelper.new
end
