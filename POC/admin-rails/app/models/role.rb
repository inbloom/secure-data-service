class Role < ActiveResource::Base
  self.site = "https://testapi1.slidev.org/api/rest/admin"
  # @header['ADMIN_SESSION_ID'] = session['ADMIN_SESSION_ID']

  class << self
    ## Remove format from the url.
     def element_path(id, prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
     end

     ## Remove format from the url.
     def collection_path(prefix_options = {}, query_options = nil)
       prefix_options, query_options = split_options(prefix_options) if query_options.nil?
       "#{prefix(prefix_options)}#{collection_name}#{query_string(query_options)}"
     end
  end
end
