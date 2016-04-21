# -*- encoding: utf-8 -*-
# stub: mobpage 0.0.1 ruby lib

Gem::Specification.new do |s|
  s.name = "mobpage"
  s.version = "0.0.1"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.require_paths = ["lib"]
  s.authors = ["Milton Davalos"]
  s.date = "2015-12-08"
  s.description = "Page Object DSL for iOS and Android mobile testing"
  s.email = "miltondavalos@gmail.com"
  s.homepage = "https://github.com/miltondavalos/MobPage"
  s.licenses = ["MIT"]
  s.rubygems_version = "2.4.8"
  s.summary = "Page Object DSL for mobile testing"

  s.installed_by_version = "2.4.8" if s.respond_to? :installed_by_version

  if s.respond_to? :specification_version then
    s.specification_version = 4

    if Gem::Version.new(Gem::VERSION) >= Gem::Version.new('1.2.0') then
      s.add_development_dependency(%q<appium_lib>, [">= 0"])
      s.add_development_dependency(%q<selenium-webdriver>, [">= 0"])
    else
      s.add_dependency(%q<appium_lib>, [">= 0"])
      s.add_dependency(%q<selenium-webdriver>, [">= 0"])
    end
  else
    s.add_dependency(%q<appium_lib>, [">= 0"])
    s.add_dependency(%q<selenium-webdriver>, [">= 0"])
  end
end
