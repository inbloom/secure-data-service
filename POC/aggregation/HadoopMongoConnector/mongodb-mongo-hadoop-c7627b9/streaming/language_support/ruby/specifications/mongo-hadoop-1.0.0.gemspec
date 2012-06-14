# -*- encoding: utf-8 -*-

Gem::Specification.new do |s|
  s.name = "mongo-hadoop"
  s.version = "1.0.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Tyler Brock"]
  s.date = "2012-05-20"
  s.description = "Ruby MongoDB Hadoop streaming support"
  s.email = "tyler.brock@gmail.com"
  s.homepage = "http://github.com/mongodb/mongo-hadoop"
  s.require_paths = ["lib"]
  s.rubygems_version = "1.8.24"
  s.summary = "MongoDB Hadoop streaming support"

  if s.respond_to? :specification_version then
    s.specification_version = 3

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<bson>, [">= 0"])
    else
      s.add_dependency(%q<bson>, [">= 0"])
    end
  else
    s.add_dependency(%q<bson>, [">= 0"])
  end
end
