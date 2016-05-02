module MobTest
  class Base
    extend Mobile::Platform
    extend PageObject::Accessors

    def initialize(selenium_driver)
      puts "BASE Ruby"
      @driver = selenium_driver
    end
  end
end