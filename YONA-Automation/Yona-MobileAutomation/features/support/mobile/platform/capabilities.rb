require 'yaml'
require 'appium_lib'
require_relative '../../utilities'

module Platform
  module Capabilities

    include Utils


    def setup_ios
      if ENV['TARGET'] == 'sauce'
        capabilities = sauce_ios_capabilities
      else
        capabilities = {caps: ios_capabilities}
      end
      Appium::Driver.new(capabilities).start_driver
    end

    def setup_android
      if ENV['TARGET'] == 'sauce'
        capabilities = sauce_android_capabilities
      else
        capabilities = {caps: android_capabilities}
        puts 'Setting android package ' + capabilities[:caps][:appPackage]
      end
      Appium::Driver.new(capabilities).start_driver
    end

    def setup_web
      browser = ENV['BROWSER']
      Selenium::WebDriver.for browser.to_sym
    end

    def android_capabilities
      caps = android_settings
      capabilities =
          {
              platformName: 'Android',
              # fullReset: true,
              noReset: true,
              app: caps[:apk_path],
              appPackage: caps[:android_package],
              appActivity: caps[:android_activity]
          }
      if ENV['ANDROID_PHONE'] == 'emulator'
        capabilities = capabilities.merge(deviceName: 'Android Emulator', avd: ENV['DEVICE'])
      else
        capabilities = capabilities.merge(deviceName: ENV['DEVICE'])
      end

      capabilities
    end

    def sauce_android_capabilities
      cap = android_settings
      # app = cap[:apk_path].split('/').select{|element| element.include?'.apk'}.first
      sauce_user = 'cbhanushali'
      sauce_key = '3c41efb2-f88e-406f-ba68-f9096b78a413'
      {
          caps: {
              platformName: 'Android',
              # app: cap[:apk_path],
              appPackage: cap[:android_package],
              appActivity: cap[:android_activity],
              :'appium-version' => '1.5.0',
              platformVersion: ENV['VERSION'],
              deviceName: ENV['DEVICE'],
              app: 'sauce-storage:Yona_android.apk',
              # name: app,
              :'access-key' => sauce_key
          },
          appium_lib: {
              wait: 15,
              server_url: "http://#{sauce_user}:#{sauce_key}@ondemand.saucelabs.com:80/wd/hub",
              sauce_username: sauce_user,
              sauce_access_key: sauce_key
          }
      }
    end


    def ios_capabilities
      caps = ios_settings
      caps[:app_path] = ENV['IOS_DERIVED_DATA_PATH']+ '/' + caps[:app] if caps[:app_path].nil?

      capabilities =
          {
              deviceName: ENV['DEVICE'],
              platformVersion: ENV['VERSION'],
              platformName: 'iOS',
              preLaunch: true,
              fullReset: true,
              app: caps[:app_path]
          }
      if ENV['IOS_PHONE'] == 'device'
        capabilities.merge!(uiud: ENV['UIUD'], bundleId: caps[:bundle_id])
      end

      capabilities
    end

    def sauce_ios_capabilities
      cap = ios_settings
      cap[:app_path] = ENV['IOS_DERIVED_DATA_PATH']+'/'+cap[:app] if cap[:app_path].nil?

      sauce_user = %x[echo $SAUCE_USER].strip
      sauce_key = %x[echo $SAUCE_KEY].strip
      app = cap[:app_path].split('/').select{|element| element.include?'.app'}.first.gsub('.app','.zip')
      {
          caps: {
              platformName: 'iOS',
              :'appium-version' => '1.3.7',
              platformVersion: ENV['VERSION'],
              deviceName: 'iPhone 6',
              app: "sauce-storage:#{app}",
              :'access-key' => sauce_key,
              name: app
          },
          appium_lib: {
              wait: 15,
              server_url: "http://#{sauce_user}:#{sauce_key}@ondemand.saucelabs.com:80/wd/hub",
              sauce_username: sauce_user,
              sauce_access_key: sauce_key
          }
      }
    end

    def start_selenium_driver
      self.send("setup_#{ENV['PLATFORM']}")
    end

  end
end