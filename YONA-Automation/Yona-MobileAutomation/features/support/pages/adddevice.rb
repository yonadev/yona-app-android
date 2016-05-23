require_relative '../../../features/support/pages/common/APIs_Yona'
require_relative '../../../features/support/pages/common/common_UI_Functions'
require 'json'
#------------------------------------------------------------------
# Class with Yona API methods for varous operations
#------------------------------------------------------------------
# Generates random mobile number and makes call to
# add_user method for Post request of Add User

puts "Mobile = #{$strMobliNumb}"
  class AddDevice<MobTest::Base



      $yona_pwd='1234567'
      $newDevicePwd="id72rS"
      $newDeviceUri

      android do
        button(:login, xpath: '//android.widget.Button[2]')
        button(:vorige, xpath: '//android.widget.Button[1]')
        text_field(:mobnumber, xpath: '//android.widget.LinearLayout[1]/android.widget.EditText[1]')
        text_field(:passcode, xpath: '//android.widget.LinearLayout[2]/android.widget.EditText[1]')
        element(:chlgs_title, xpath: '//android.widget.TextView[@text="CHALLENGES"]')
        element(:otpscreen_title, id: "#{$android_package}:id/passcode_title")
        puts "package=#{$android_package}"

        # Check for Android Native Keybaord and dissmiss it if present
        def checkKeyBoard
          if (ENV['PLATFORM'] == 'android')
            if(COMMON_UI.checkUIKeyboard)
              @driver.navigate.back
            end
          end
        end

      end

      def enterDetails(strMbNum)
        checkKeyBoard
        mobnumber_element.send_keys strMbNum
        sleep 2
        checkKeyBoard
        passcode_element.send_keys $newDevicePwd
        sleep 2
        checkKeyBoard
      end

      # Makes a POST API call to create user
       def singUpAPIs(strMobileNumb)
         begin
           url="http://85.222.227.142/users/"
           payLoad = {
               "firstName": "Richard",
               "lastName": "Quin",
               "mobileNumber":strMobileNumb,
               "nickname": "RQ"
           }.to_json
           COMMONAPIS.add_user(url,payLoad)
         rescue => e
           puts "Exceptions occurred #{e.message}"
         end
       end

      def addNewDevic(strMobile)
        begin
          puts "URI = #{$newDeviceUri}"
          COMMONAPIS.newDevicePutRequest($newDeviceUri)
        rescue => e
          puts "Exceptions occurred #{e.message}"
        end
      end


      def landed_on_OTP?
        bool = false
        sleep 4
        begin
          bool=self.otpscreen_title_element.displayed?
        rescue Exception => e
          puts "Message=#{e.message}"
          return false
        end
        bool
      end

  end

