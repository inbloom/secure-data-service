require 'base64'
require 'rexml/document'

module IDP
  class SAMLRequest
    def initialize(encoded_string)
      zipped_data = Base64.decode64(encoded_string)
      zstream = Zlib::Inflate.new(-Zlib::MAX_WBITS)
      xml = zstream.inflate(zipped_data)
      zstream.finish
      zstream.close
      @doc = REXML::Document.new xml
    end
  
    def destination
      @doc.root.attributes['Destination']
    end
  end
end