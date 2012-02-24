# transform /v1/entity/<Place Holder Id>
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)$/ do |version, uri, template|
  version + uri + Transform(template)
end

# transform /v1/entity/<Place Holder Id>/association
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)$/ do |version, uri, template, assoc|
  version + uri + Transform(template) + assoc
end

# transform /v1/entity/<Place Holder Id>/association/entity
Transform /^(\/[\w-]+\/)([\w-]+\/)(<.+>)(\/[\w-]+)(\/[\w-]+)$/ do |version, uri, template, assoc, entity|
  version + uri + Transform(template) + assoc + entity
end