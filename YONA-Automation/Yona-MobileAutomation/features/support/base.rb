module MobTest
  class Base
    extend Mobile::Platform
    extend PageObject::Accessors

    def initialize(selenium_driver)
      @driver = selenium_driver
    end
  end
end