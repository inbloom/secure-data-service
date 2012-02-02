require 'rest_client'
require 'sinatra'
require "sinatra/reloader" if development?

enable :sessions

before do
    @api_url ||= "https://devapp1.slidev.org/api/rest/"
    @app_url ||= request.url.sub(request.path, '/')
    @sessionId = request.cookies["iPlanetDirectoryPro"]
end

get '/*' do |uri|
    if( uri.empty? )
        uri = "home"
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
        "<a href=/#{uri}>#{uri.split('/')[0]}</a>"
    end
end
