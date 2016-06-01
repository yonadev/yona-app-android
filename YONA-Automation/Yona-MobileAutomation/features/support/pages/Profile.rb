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
    button(:profpgr, id: "#{$android_package}:id/viewPager")

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


    def swipe_to
      wlprvwr = profpgr_element
      prfsize=wlprvwr.size
     prfloc=wlprvwr.location
      btmscreen=prfsize[:height]+prfloc[:y]
      COMMON_UI.appium_swipe((prfsize[:width]/2),btmscreen-50,(prfsize[:width]/2),pteditic_element.location[:y],1)

    end

  end



end