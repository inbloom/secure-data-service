# -*- encoding: utf-8 -*-
require File.expand_path('../lib/version', __FILE__)

Gem::Specification.new do |gem|
  gem.authors       = ["Stephan Altmueller"]
  gem.email         = ["saltmueller@wgen.net"]
  gem.description   = %q{ Write a gem description}
  gem.summary       = %q{ Write a gem summary}
  gem.homepage      = ""

  gem.files         = Dir['lib/*.rb', 'test/*.rb'] + ['Gemfile','Rakefile', 'README.md', 'approval.gemspec']
#  gem.files         = `git ls-files`.split($\)    
  gem.executables   = gem.files.grep(%r{^bin/}).map{ |f| File.basename(f) }
  gem.test_files    = gem.files.grep(%r{^(test|spec|features)/})
  gem.name          = "approval"
  gem.require_paths = ["lib"]
  gem.version       = Approval::VERSION
end
