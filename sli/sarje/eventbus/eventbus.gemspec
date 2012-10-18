# -*- encoding: utf-8 -*-
require File.expand_path('../lib/eventbus/version', __FILE__)

Gem::Specification.new do |gem|
  gem.authors       = ["Stephan Altmueller"]
  gem.email         = ["saltmueller@wgen.net"]
  gem.description   = %q{TODO: Write a gem description}
  gem.summary       = %q{TODO: Write a gem summary}
  gem.homepage      = ""

  gem.files         = Dir['conf/*yml', 'lib/**/*.rb', 'test/*.rb', 'test/data/*.json', '*rb'] + ['Gemfile','Rakefile', 'eventbus.gemspec']
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.name          = "eventbus"
  gem.require_paths = ["lib"]
  gem.version       = Eventbus::VERSION
end