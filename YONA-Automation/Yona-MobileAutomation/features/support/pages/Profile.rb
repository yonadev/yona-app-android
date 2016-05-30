class Profile<MobTest::Base

  android do
    button(:ptviewic, id: "#{$android_package}:id/leftIcon")
    label(:ptfname, id: "#{$android_package}:id/first_name_layout")
    button(:ptlname, id: "#{$android_package}:id/last_name_layout")
    button(:ptnickname, id: "#{$android_package}:id/nick_name_layout")
    button(:ptmobilen, id: "#{$android_package}:id/mobile_number_layout")
    button(:pteditic, id: "#{$android_package}:id/rightIcon")
    button(:ptprofimg, id: "#{$android_package}:id/profileImage")
    buttons(:menutab, id: "#{$android_package}:id/tab_image")

    def isflddisplayed(wbele)
      begin
        if (wbele.displayed?)
          puts "#{wbele.attribute("text")} is dipslayed"
          boolel=true
        end
      rescue Exception=>e
        puts e.message
        return false
      end
      return boolel
    end


    def swipe_to_bottom
      middle = self.ptlname_element
      @driver.execute_script('mobile: scrollTo', element: middle.ref)
    end

  end



end