# -*- encoding: utf-8 -*-
# stub: gherkin 3.2.0 ruby lib

Gem::Specification.new do |s|
  s.name = "gherkin"
  s.version = "3.2.0"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["G\u{e1}sp\u{e1}r Nagy", "Aslak Helles\u{f8}y", "Steve Tooke"]
  s.date = "2016-01-12"
  s.description = "Gherkin parser"
  s.email = "cukes@googlegroups.com"
  s.homepage = "https://github.com/cucumber/gherkin"
  s.licenses = ["MIT"]
  s.rdoc_options = ["--charset=UTF-8"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.3")
  s.rubygems_version = "2.4.8"
  s.summary = "gherkin-3.2.0"

  s.installed_by_version = "2.4.8" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<bundler>, ["~> 1.7"])
      s.add_development_dependency(%q<rake>, ["~> 10.4"])
      s.add_development_dependency(%q<rspec>, ["~> 3.3"])
      s.add_development_dependency(%q<coveralls>, ["< 0.8.8", "~> 0.8"])
    else
      s.add_dependency(%q<bundler>, ["~> 1.7"])
      s.add_dependency(%q<rake>, ["~> 10.4"])
      s.add_dependency(%q<rspec>, ["~> 3.3"])
      s.add_dependency(%q<coveralls>, ["< 0.8.8", "~> 0.8"])
    end
  else
    s.add_dependency(%q<bundler>, ["~> 1.7"])
    s.add_dependency(%q<rake>, ["~> 10.4"])
    s.add_dependency(%q<rspec>, ["~> 3.3"])
    s.add_dependency(%q<coveralls>, ["< 0.8.8", "~> 0.8"])
  end
end
