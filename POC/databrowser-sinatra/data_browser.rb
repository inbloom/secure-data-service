require 'rubygems'
require 'rest_client'
require 'sinatra'
require "sinatra/reloader" if development?

enable :sessions

before do
    @api_url ||= "https://devapp1.slidev.org/"
    @app_url ||= "http://mlane.devapp1.slidev.org:4567/db/"
    @sessionId = request.cookies["iPlanetDirectoryPro"]
    #session["iPlanetDirectoryPro"] ||= @session_cookie
end

get '/' do
    redirect to('/db/')
end

get '/db/*' do |uri|
    if( uri.empty? )
        uri = "api/rest/home"
        session["path"] = []
    end
    session["path"].push(create_link(uri))
    @path = session["path"]

    @res = RestClient.get("#{@api_url + uri}?sessionId=#{@sessionId}",
        { :accept => :json } )

    modify_hrefs!(@res)
    erb :index
end

helpers do
    def modify_hrefs!(source)
        source.gsub!(@api_url, @app_url)
    end
    def create_link(uri)
        "<a href=/db/#{uri}>#{uri.split('/')[2]}</a>"
    end
end
