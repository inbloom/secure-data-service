class SessionModel < ActiveResource::Base
  cattr_accessor :id
  Rails.logger.debug { "Session ID: #{@id}" }
  class << self
    ## Remove format from the url.
     def element_path(id, prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{collection_name}/#{id}?sessionId=#{self.id}#{query_string(query_options)}"
       Rails.logger.debug { "element_path: #{something}" }
       something
     end

     ## Remove format from the url.
     def collection_path(prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       something = "#{prefix(prefix_options)}#{collection_name}?sessionId=#{self.id}#{query_string(query_options)}"
       Rails.logger.debug { "collection_path: #{something}" }
       something
     end
  end
end