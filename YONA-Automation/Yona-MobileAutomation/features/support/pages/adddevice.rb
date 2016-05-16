require_relative '../../../features/support/pages/common/APIs_Yona'
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
      end

      def enterDetails(strMbNum)
        mobnumber_element.send_keys strMbNum
        passcode_element.send_keys $newDevicePwd
      end


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
      # Makes a POST API call to create user


      def landed_on_OTP?
        bool = false
        sleep 4
        begin
          puts "Displayed=#{otpscreen_title_element.displayed?}"
          bool=self.otpscreen_title_element.displayed?

        rescue Exception => e
          puts "Message=#{e.message}"
          return false
        end
        bool
      end


  end

