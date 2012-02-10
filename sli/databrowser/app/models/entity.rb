class Entity < SessionResource
  self.site = APP_CONFIG['api_base']
  
  def self.get_simple_and_complex(parameters)
    base = get(parameters)
    entity = []
    if base.is_a?(Array)
      type = base.first['type']
      base.each do |single|
        one = Hash.new
        one[:simple] = build_simple_hash(@type, single)
        one[:complex] = single
        entity.push(one)
      end
    else
      entity.push({:complex => entity, :simple => nil})
    end
    entity
  end
  
  def self.build_simple_hash(type, hash)
    return nil if hash.nil?
    type = get_basic_types(hash) if type.nil?
    one = {}
    final_type = type.split('/').last
    one[final_type] = value_for_simple_view(type, hash)
    one
  end
  
  def self.value_for_simple_view (type, hash)
    return nil if hash.nil? or type.nil?
    hash[type] unless type.include? '/'
    type_split = type.split '/'
    temp_hash = hash
    type_split.each do |split|
      temp_hash = temp_hash[split]
    end
    temp_hash
  end
  
  def self.get_basic_types(hash)
    types = []
    counter = 0
    hash.keys do |key|
      if counter == 4
        break
      end
      if hash[key].is_a?(Hash) or hash[key].is_a?(Array)
        types.push(key)
        counter += 1
      end
    end
    types
  end
end
