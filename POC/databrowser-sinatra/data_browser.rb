=begin

Copyright 2012-2013 inBloom, Inc. and its affiliates.

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
