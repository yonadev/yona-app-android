module PageObject
  module Accessors

    def create_standard_methods(name, identifier={}, &block)
      define_method("#{name}_element") do
        if identifier.has_key?(:index)
          index = identifier[:index].to_i
          identifier.delete(:index)
          @driver.find_elements(identifier)[index]
        elsif identifier.has_key?(:text)
          elements = @driver.find_elements(identifier)
          elements.each do |element|
            return element if element.text == identifier[:text]
            nil
          end
        elsif identifier.has_key?(:name) && identifier.has_key?(:class)
          elements = @driver.find_elements(class: identifier[:class])
          elements.each do |element|
            return element if element.attribute('name').strip == identifier[:name].strip
            nil
          end
        else
          @driver.find_element(identifier)
        end
      end
    end

    def create_standard_methods_for_collection(name, identifier={})
      define_method("#{name}_elements") do
        @driver.find_elements(identifier)
      end
    end

    def single(name, identifier={}, &block)
      create_standard_methods(name, identifier)
    end

    def collection(name, identifier={})
      create_standard_methods_for_collection(name, identifier)
    end
  end
end





