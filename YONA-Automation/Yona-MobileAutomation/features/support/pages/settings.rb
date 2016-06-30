require 'appium_lib'

class Settings<MobTest::Base

  android do
    dialog(:unsubscribedl, id: "android:id/message")
    button(:join, id: "#{$android_package}:id/join")
    button(:okunsub, id: "android:id/button1")
    button(:cancelunsub, id: "android:id/button2")
    label(:settingslbl, id: "#{$android_package}:id/toolbar_title")
    label(:unsublink, xpath: "//android.widget.TextView[@text='Unsubscribe']")
    # label(:unsublink, xpath: "//android.widget.TextView']")





    def clickUnsubscribe
      wl=unsublink_element
      if(wl!=nil)
        wl.click
      end
    end

    def clickCancel
      blflg=checkdialog
      if blflg
        cancelunsub_element.click
      end
    end

    def checkdialog
      begin
        if (unsubscribedl_element.displayed?)
          return true
        else
          return false
        end
      rescue Exception => e
        puts e.message
        return false
      end
    end

    def settingsScreen
      # bool=false
      begin
        wl=settingslbl_element
        if((wl!=nil) && (wl.attribute('text')=='SETTINGS'))
          return true
        else
          return false
        end
      rescue Exception => e
        puts e.message
        return false
      end

    end

    def clickOk
      blflg=checkdialog
      if blflg
        okunsub_element.click
      end
    end

    def onHomepage?
      begin
        wl = join_element
        if wl.displayed?
          return true
        else
          return false
        end
      rescue Exception => e
        puts e.message
        return false
      end
    end

    def minimizeandrelaunchapp
      @driver.press_keycode 3
      sleep 2
      @driver.press_keycode 187
      sleep 2
      # puts "Size #{(@driver.manage().window().size[:height])/2}"
      # puts "Size #{(@driver.manage().window().size[:width])/2}"
      hght= (@driver.manage().window().size[:height])-200
      wdt=(@driver.manage().window().size[:width])/2
      # puts "Height=#{hght} and width=#{wdt}"
      COMMON_UI.appium_tap(1,1,0.5,wdt,hght)
      sleep 2


    end

  end




end