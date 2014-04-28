class ApplicationAuthorization < SessionResource
  cattr_accessor :cur_edorg

  self.collection_name = 'applicationAuthorization'

  schema do
    string 'appId'
    boolean 'authorized'
    string 'edorgs' # Really, an array of hashes
  end

 class << self
    def element_path(id, prefix_options={}, query_options=nil)
      prefix_options, query_options = split_options(prefix_options) unless query_options
      query_options[:edorg] = cur_edorg if cur_edorg
      "#{prefix(prefix_options)}#{collection_name}/#{id}#{query_string(query_options)}"
    end
 end

end
