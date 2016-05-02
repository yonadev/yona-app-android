require_relative '../../../features/support/pages/common/APIs_Yona'
require 'json'
#------------------------------------------------------------------
# Class with Yona API methods for varous operations
#------------------------------------------------------------------

  class AddDevice<MobTest::Base

       # Generates random mobile number and makes call to
       # add_user method for Post request of Add User
      $strMobliNumb = "+31" + rand(1000000000).to_s
      $yona_pwd='1234567'
      $newDevicePwd="id82rS"
      $newDeviceUri

      android do
        button(:login, xpath: '//android.widget.Button[2]')
        button(:vorige, xpath: '//android.widget.Button[1]')
        text_field(:mobnumber, xpath: '//android.widget.LinearLayout[1]/android.widget.EditText[1]')
        text_field(:passcode, xpath: '//android.widget.LinearLayout[2]/android.widget.EditText[1]')
      end

      def enterDetails
        mobnumber_element.send_keys $strMobliNumb
        passcode_element.send_keys $newDevicePwd
      end


       def singUpAPIs
         begin
           url="http://85.222.227.142/users/"
           payLoad = {
               "firstName": "Richard",
               "lastName": "Quin",
               "mobileNumber": $strMobliNumb,
               "nickname": "RQ"
           }.to_json
           COMMONAPIS.add_user(url,payLoad)
         rescue => e
           puts "Exceptions occurred #{e.message}"
         end
       end

      def addNewDevic
        begin
          puts "URI = #{$newDeviceUri}"
          COMMONAPIS.newDevicePutRequest($newDeviceUri)
        rescue => e
          puts "Exceptions occurred #{e.message}"
        end
      end
      # Makes a POST API call to create user


  end

