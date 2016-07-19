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
    # text_field(:fname, id: "#{$android_package}:id/first_name")
    # text_field(:lname, id: "#{$android_package}:id/last_name")
    # text_field(:mnumber, id: "#{$android_package}:id/mobile_number")
    # text_field(:nickname, id: "#{$android_package}:id/nick_name")
    text_field(:pfeidtimg, xpath: "//android.widget.ImageView[@resource-id='#{$android_package}:id/updateProfileImage']")
    text_field(:fname, xpath: "//android.widget.ImageView[@resource-id='#{$android_package}:id/updateProfileImage']/../../../../descendant::android.widget.EditText[@resource-id='#{$android_package}:id/first_name']")
    text_field(:lname, xpath: "//android.widget.ImageView[@resource-id='#{$android_package}:id/updateProfileImage']/../../../../descendant::android.widget.EditText[@resource-id='#{$android_package}:id/last_name']")
    text_fields(:nickname, xpath: "//android.widget.EditText[@resource-id='#{$android_package}:id/nick_name']")
    text_fields(:mnumber, xpath: "//android.widget.EditText[@resource-id='#{$android_package}:id/mobile_number']")




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
     puts "Coordinates=#{(prfsize[:width]/2)},#{ptfname_element.location[:y]},#{prfsize[:width]/2},#{pteditic_element.location[:y]}"
     COMMON_UI.appium_swipe((prfsize[:width]/2),ptfname_element.location[:y],(prfsize[:width]/2),pteditic_element.location[:y],1)
    end

    def swiepe_onedit
      wlprvwr = profpgr_element
      prfsize=wlprvwr.size
      prfloc=wlprvwr.location
      btmscreen=prfsize[:height]+prfloc[:y]
      # wlnickname=nickname_elements
      # if(wlnickname.size>1)
      #   coordY=wlnickname[1].location[:y]
      # else if(wlnickname.size==1)
      #        coordY=wlnickname[0].location[:y]
      #        end
      # end

      puts "Coordinates=#{(prfsize[:width]/2)},#{lname_element.location[:y]},#{prfsize[:width]/2},#{pfeidtimg_element.location[:y]}"
      COMMON_UI.appium_swipe((prfsize[:width]/2),lname_element.location[:y],(prfsize[:width]/2),pfeidtimg_element.location[:y],1)
    end

    def resetYona
      @driver.reset
    end

    def editDetails
      fname_element.clear
      checkKeyBoard
      fname_element.send_keys 'f_u'
      # checkKeyBoard
      sleep 2
      lname_element.clear
      checkKeyBoard
      lname_element.send_keys 'l_u'
      sleep 2

      # checkKeyBoard
      swiepe_onedit
      sleep 2
      wlnickname=nickname_elements
      puts "Size=#{wlnickname.size}"
      if(wlnickname.size>1)
        wlnickname[1].clear
        checkKeyBoard
        wlnickname[1].send_keys 'n_u'
        # checkKeyBoard
        sleep 1
      else if (wlnickname.size==1)
        wlnickname[0].clear
        checkKeyBoard
        wlnickname[0].send_keys 'n_u'
        # checkKeyBoard
        sleep 1
           end
      end
      sleep 2
     # checkKeyBoard
      begin
        strMob=rand(1000000000).to_s
      end while strMob.length!=9
      wlmobnum=mnumber_elements
      if(wlmobnum.size>1)
        checkKeyBoard
        wlmobnum[1].send_keys strMob
        # checkKeyBoard
        # sleep 1
      else if (wlnickname.size==1)
             checkKeyBoard
             wlmobnum[0].send_keys strMob
             sleep 1
      end

    end
      sleep 2
      checkKeyBoard
    end

    def checkKeyBoard
      if (ENV['PLATFORM'] == 'android')
        if(COMMON_UI.checkUIKeyboard)
          @driver.navigate.back
        end
      end
    end

  end

end