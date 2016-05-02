#------------------------------------------------------------------
# FOLLOW THE BELLOW EXAMPLE TO CREATE YOUR PAGE OBJECT CLASSES
#------------------------------------------------------------------

class Signup < MobTest::Base

  ios do

    button(:join, xpath: '//UIAApplication[1]/UIAWindow[1]/UIAButton[1]')
    text_field(:text1, xpath: '//UIATextField[1]')
    text_field(:text2, xpath: '//UIATextField[2]')
    text_field(:qutext, xpath:'//UIAStaticText[contains(@name, "<Privacy quote>")]')
    button(:next, xpath: '//UIAButton[@name="NEXT"]')
    text_field(:mobtext, xpath:'//UIAStaticText[contains(@name, "We use your mobile number")]')
    button(:alertOK, xpath: '//UIAAlert[1]/UIACollectionView[1]/UIACollectionCell[1]/UIAButton[1]')
    element(:create_PIN1, name: '1', class: 'UIAKey')
    element(:create_PIN2, name: '2', class: 'UIAKey')
    element(:create_PIN3, name: '3', class: 'UIAKey')
    element(:create_PIN4, name: '4', class: 'UIAKey')

    def clickOK
      alertOK_element.click()
    end

    def enterPin
      create_PIN1_element.click
      create_PIN2_element.click
      create_PIN3_element.click
      create_PIN4_element.click
    end

  end


  def setText(text1, text2)
    # puts "Environment=#{ENV['PLATFORM']}"
    text1_element.send_keys text1;
    text2_element.send_keys text2;
  end

  def next(scrName)

    if (scrName=='namescreen' ) && (ENV['PLATFORM'] == 'ios')
      qutext_element.click();
    end
  else if (scrName=='mobnumscreen') && (ENV['PLATFORM'] == 'ios')
         mobtext_element.click();
       end
  next_element.click();
  end



  android do


    button(:join, xpath: '//android.widget.Button[1]')
    text_field(:text1, xpath: '//android.widget.LinearLayout[1]/android.widget.EditText[1]')
    text_field(:text2, xpath: '//android.widget.LinearLayout[2]/android.widget.EditText[1]')
    button(:next, xpath: '//android.widget.Button[@resource-id="nu.yona.app:id/next"]')
    text_field(:pin1, xpath: '//android.widget.EditText[1]')
    text_field(:pin2, xpath: '//android.widget.EditText[2]')
    text_field(:pin3, xpath: '//android.widget.EditText[3]')
    text_field(:pin4, xpath: '//android.widget.EditText[4]')

    def clickOK
      puts "No popup, we are in Android"
    end

    def enterPin

      pin1_element.send_keys '1234'

    end

  end
    #
  # web do
  #   text_field(:username, id: 'username')
  #   text_field(:password, name: 'password')
  #   button(:login, name: 'login')
  # end
  #
  # ios do
  #   #place your ios specific methods within this block
  # end
  #
  # android do
  #   #place your android specific methods within this block
  # end
  #
  # web do
  #   #place your web specific methods within this block
  # end

  #place methods that apply to all platforms outside the blocks
end
