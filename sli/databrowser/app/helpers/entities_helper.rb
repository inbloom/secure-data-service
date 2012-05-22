module EntitiesHelper

  def get_table_fields(entities)
    tableFields = Array.new
    return tableFields if entities.nil? || (entities.is_a?(Array) && entities.empty?)

    entity = entities.first
    if (entity.has_key?('entityType') && VIEW_CONFIG.has_key?(entity['entityType']))
      tableFields = VIEW_CONFIG[entity['entityType']]
    end

    if tableFields.empty?
      entity.each do |key, val|
        next if val.is_a?(Array) || val.is_a?(Hash)
        tableFields.push(key)
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
    url.gsub(APP_CONFIG['api_base'], "#{request.protocol}#{request.host_with_port}/entities") unless url.nil?
  end

  def build_links(hash)
    html = ""
    if hash.is_a?(Array)
      html << '<ul>'
      hash.each do |link|
        html << '<li>' << link_to(t(link["rel"]), localize_url(link["href"])) << '</li>'
      end
      html << '</ul>'
    else
      html << link_to(t(hash["rel"]), localize_url(hash["href"]))
    end
    html
  end

  def display_entity (entity)
    html ||= ""
    if entity.is_a?(Hash)
      entity.each do |key, value|
        html << "<dt><b>#{t(key)}:</b></dt>"
        if key == 'links' || key == 'link'
          html << "<dd>#{build_links(value)}</dd>"
        else
          html << "<dd>#{display_entity(value)}</dd>"
        end
      end

    elsif entity.is_a?(Array)
      entity.each { |item| html << display_entity(item) }
    else
      html << entity.to_s
    end
    html
  end

end
