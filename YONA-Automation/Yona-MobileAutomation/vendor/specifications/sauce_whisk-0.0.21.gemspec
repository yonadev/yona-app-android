# -*- encoding: utf-8 -*-
# stub: sauce_whisk 0.0.21 ruby lib

Gem::Specification.new do |s|
  s.name = "sauce_whisk"
  s.version = "0.0.21"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Dylan Lacey"]
  s.date = "2016-02-25"
  s.description = "A Wrapper for the Sauce Labs REST API."
  s.email = ["github@dylanlacey.com"]
  s.homepage = "http://www.github.com/dylanlacey/sauce_whisk"
  s.licenses = ["MIT"]
  s.rubygems_version = "2.4.8"
  s.summary = "Sauce_Whisk lets you mix extra data into your Sauce test results! Fetch and update Job details, screenshots, videos and logs."

  s.installed_by_version = "2.4.8" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<rest-client>, ["~> 1.8.0"])
      s.add_runtime_dependency(%q<json>, ["~> 1.8.1"])
      s.add_development_dependency(%q<vcr>, ["~> 2.9.0"])
      s.add_development_dependency(%q<webmock>, ["~> 1.21.0"])
      s.add_development_dependency(%q<rspec>, ["~> 3.3.0"])
      s.add_development_dependency(%q<rake>, ["~> 10.4.2"])
    else
      s.add_dependency(%q<rest-client>, ["~> 1.8.0"])
      s.add_dependency(%q<json>, ["~> 1.8.1"])
      s.add_dependency(%q<vcr>, ["~> 2.9.0"])
      s.add_dependency(%q<webmock>, ["~> 1.21.0"])
      s.add_dependency(%q<rspec>, ["~> 3.3.0"])
      s.add_dependency(%q<rake>, ["~> 10.4.2"])
    end
  else
    s.add_dependency(%q<rest-client>, ["~> 1.8.0"])
    s.add_dependency(%q<json>, ["~> 1.8.1"])
    s.add_dependency(%q<vcr>, ["~> 2.9.0"])
    s.add_dependency(%q<webmock>, ["~> 1.21.0"])
    s.add_dependency(%q<rspec>, ["~> 3.3.0"])
    s.add_dependency(%q<rake>, ["~> 10.4.2"])
  end
end
