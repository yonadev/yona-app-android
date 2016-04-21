module PageObject
  module Navigation
    def on(klass)
      klass.new(@driver)
    end
  end
end
