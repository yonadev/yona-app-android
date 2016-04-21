require 'rspec'
require 'active_support/all'
require 'mobpage'
require 'mobmanager'

require_relative '../support/mobile/platform/capabilities'
require_relative '../support/mobile/platform/platforms'
require_relative 'utilities'
require_relative 'base'

include Platform::Capabilities
include Mobile::Appium::Server
include Platform::Android::Common
include Platform::IOS::Common
include Mobile::Platform
include Utils
include MobTest
extend Mobile::Platform

ENV['IOS_DERIVED_DATA_PATH'] = '~/Build/Products/Debug-iphonesimulator'

android do
  start_appium_server unless ENV['TARGET'] == 'sauce'
  prepare_android_phone(android_settings)
end unless ENV['TARGET'] == 'web'

ios do
  start_appium_server unless ENV['TARGET'] == 'sauce'
  prepare_ios_phone(ios_settings)
end unless ENV['TARGET'] == 'web'

World(PageObject::Navigation)
World(Utils)