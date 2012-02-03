module EntitiesHelper
  def display_hash (hash = {}, indent = 0)
    if indent != 0
      concat("<dl>")
    end
    hash.each do |key, value| 
      if key != 'id'
        concat("<dt><b> #{key} :</b></dt>")
        concat("<dd>")
        if value.is_a?(Hash) 
          display_hash(value, indent+1)  
        end
        value unless value.is_a?(Hash) or value.is_a?(Array)
        if value.is_a?(Array)
          value.each do |item| 
            display_hash(item, indent+1)
          end
        end
        concat("</dd>")
      end 
    end 
    if indent != 0 
      concat("</dl>")
    end
  end 
  def build_links(hash = {})
  	link_to hash[:rel], hash[:href]
  end
end
