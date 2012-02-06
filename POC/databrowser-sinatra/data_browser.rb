require 'rest_client'
require 'sinatra'
require "sinatra/reloader" if development?

enable :sessions

before do
    @api_url ||= "https://devapp1.slidev.org/api/rest/"
    @self_url ||= request.url.sub(request.path, '/')
    @sessionId = request.cookies["iPlanetDirectoryPro"]
end

get '/*' do |uri|
    pass if request.path_info == "/favicon.ico"
    uri = "home" if uri.empty?

    @res = RestClient.get("#{@api_url + uri}?sessionId=#{@sessionId}",
        { :accept => :json }) { |response|
        redirect to "#{response.headers[:www_authenticate]}?RelayState=#{request.url}" if response.code == 401
        response
    }

    set_path(uri)
    links_to_self!(@res)
    erb :index
end

helpers do
    def links_to_self!(source)
        source.gsub!(@api_url, @self_url)
    end
    def create_link(uri)
        "<a href=/#{uri}>#{uri.split('/')[0]}</a>"
    end
    def set_path(uri)
        session["path"] = [] if uri == "home"
        session["path"].push(create_link(uri))
        @path = session["path"]
    end
end
