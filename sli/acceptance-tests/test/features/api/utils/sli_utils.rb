require 'yaml'

def assert(bool, message = 'assertion failure')
  raise message unless bool
end

class PropLoader
  @@yml = YAML.load_file(File.join(File.dirname(__FILE__),'properties.yml'))
  def self.getProps
    return @@yml
  end
end
