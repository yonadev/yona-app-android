# -*- encoding: utf-8 -*-
# stub: appium_lib 8.0.2 ruby lib

Gem::Specification.new do |s|
  s.name = "appium_lib"
  s.version = "8.0.2"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["code@bootstraponline.com"]
  s.date = "2016-01-29"
  s.description = "Ruby library for Appium."
  s.email = ["code@bootstraponline.com"]
  s.homepage = "https://github.com/appium/ruby_lib"
  s.licenses = ["http://www.apache.org/licenses/LICENSE-2.0.txt"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.9.3")
  s.rubygems_version = "2.4.8"
  s.summary = "Ruby library for Appium"

  s.installed_by_version = "2.4.8" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_runtime_dependency(%q<selenium-webdriver>, ["~> 2.49"])
      s.add_runtime_dependency(%q<awesome_print>, ["~> 1.6"])
      s.add_runtime_dependency(%q<json>, ["~> 1.8"])
      s.add_runtime_dependency(%q<tomlrb>, ["~> 1.1"])
      s.add_runtime_dependency(%q<nokogiri>, ["~> 1.6.6"])
      s.add_development_dependency(%q<posix-spawn>, ["~> 0.3"])
      s.add_development_dependency(%q<hashdiff>, ["~> 0.2.2"])
      s.add_development_dependency(%q<spec>, ["~> 5.3.4"])
      s.add_development_dependency(%q<fakefs>, ["~> 0.6.7"])
      s.add_development_dependency(%q<rake>, ["~> 10.4"])
      s.add_development_dependency(%q<yard>, ["~> 0.8"])
      s.add_development_dependency(%q<rubocop>, ["~> 0.30.1"])
    else
      s.add_dependency(%q<selenium-webdriver>, ["~> 2.49"])
      s.add_dependency(%q<awesome_print>, ["~> 1.6"])
      s.add_dependency(%q<json>, ["~> 1.8"])
      s.add_dependency(%q<tomlrb>, ["~> 1.1"])
      s.add_dependency(%q<nokogiri>, ["~> 1.6.6"])
      s.add_dependency(%q<posix-spawn>, ["~> 0.3"])
      s.add_dependency(%q<hashdiff>, ["~> 0.2.2"])
      s.add_dependency(%q<spec>, ["~> 5.3.4"])
      s.add_dependency(%q<fakefs>, ["~> 0.6.7"])
      s.add_dependency(%q<rake>, ["~> 10.4"])
      s.add_dependency(%q<yard>, ["~> 0.8"])
      s.add_dependency(%q<rubocop>, ["~> 0.30.1"])
    end
  else
    s.add_dependency(%q<selenium-webdriver>, ["~> 2.49"])
    s.add_dependency(%q<awesome_print>, ["~> 1.6"])
    s.add_dependency(%q<json>, ["~> 1.8"])
    s.add_dependency(%q<tomlrb>, ["~> 1.1"])
    s.add_dependency(%q<nokogiri>, ["~> 1.6.6"])
    s.add_dependency(%q<posix-spawn>, ["~> 0.3"])
    s.add_dependency(%q<hashdiff>, ["~> 0.2.2"])
    s.add_dependency(%q<spec>, ["~> 5.3.4"])
    s.add_dependency(%q<fakefs>, ["~> 0.6.7"])
    s.add_dependency(%q<rake>, ["~> 10.4"])
    s.add_dependency(%q<yard>, ["~> 0.8"])
    s.add_dependency(%q<rubocop>, ["~> 0.30.1"])
  end
end
