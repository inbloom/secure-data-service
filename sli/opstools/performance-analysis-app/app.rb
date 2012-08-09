require 'sinatra'
require_relative 'models/report'

class App < Sinatra::Base
    configure do
      set :root, File.dirname(__FILE__)
    end
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

end
