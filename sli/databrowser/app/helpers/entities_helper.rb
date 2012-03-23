module EntitiesHelper

  def get_table_fields(entities)
    tableFields = Array.new
    return tableFields if entities.nil? || (entities.is_a?(Array) && entities.empty?)

    entity = entities.first
    if(entity.has_key?('entityType') && VIEW_CONFIG.has_key?(entity['entityType']))
      tableFields = VIEW_CONFIG[entity['entityType']]
    end

    if tableFields.nil?
      entity.each do |key,val|
        next if val.is_a?(Array) || val.is_a?(Hash)
        tableFields.push(val)
        break if tableFields.length >= 5
      end
    end

    return tableFields
  end

  def value_for_table_view (type, entity)
    return nil if entity.nil? or type.nil?
    return entity[type] unless type.include? '/'
    temp_hash = entity
    type_split = type.split '/'
    type_split.each do |split|
      if temp_hash.is_a?(Array)
        temp_hash = temp_hash.first[split]
      elsif temp_hash.has_key? split
        temp_hash = temp_hash[split]
      else
        return nil
      end
    end
    temp_hash
  end

  def localize_url(url)
    url.gsub( APP_CONFIG['api_base'], "http://#{request.host_with_port}/entities") unless url.nil?
  end
  
  def build_links(hash)
  	html = ""
  	if hash.is_a?(Array)
  		html << '<ul>'
  		hash.each do |link|
  			html << '<li>' << link_to( t(link["rel"]), localize_url(link["href"]) ) << '</li>'
  		end
  		html << '</ul>'
  	else
  		html << link_to( t(hash["rel"]), localize_url(hash["href"]) )
  	end
  	html
  end 

  def display_hash (hash = {}, indent = 0)
  	html = ""
    return hash if hash.empty? or !hash.is_a?(Hash)
    html << "<dl>" if indent != 0
  	hash.each do |key, value|
  			html << "<dt><b>#{t(key)}:</b></dt>"
  			html << "<dd>"
        if key == 'links' || key == 'link'
          html << build_links(value)
  			elsif value.is_a?(Hash)
  				html << display_hash(value, indent+1)
  			elsif value.is_a?(Array)
  				value.each { |item| html << display_hash(item, indent+1) }
        else
  				html << value.to_s
  			end
  			html << "</dd>"
  	end
    html << "</dl>" if indent != 0
  	html
  end
  
end
