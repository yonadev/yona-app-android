@regression
Feature: Unsubscribe Yona app

# Background:
#  Given User has intalled Yona app and launched it
#  And User is on Home page and can see two options Login and Join
#  When User clicks on Join button
#  And Enters first name and last name
#  Then Clicks on Next button
#  And Then enters Mobile number and Nick Name on next screen
#  Then Clicks on Next button on this screen
#  And Clicks on OK button in alert
#  Then Enters pincode fetched from alert
#  Then Sets the pin for application login
#  And Confirms the pin for application login



 @rejectunsub
 Scenario: User logs in to Yona app and then chosse unsubscirb option but clicks on Cancel in confirmation dialog
  Given User enters the pin and login to Yona
  And Click on Settings tab
  Then Selects the Unsubscribe link from settigs screen
  And clicks on Cancel button in confirmation dialog
  And User remains on Settins screen

 @acceptunsub
 Scenario: User logs in to Yona app and then chosse unsubscirb option but clicks on OK in confirmation dialog
  Given  User enters the pin and login to Yona
  And Click on Settings tab
  Then Selects the Unsubscribe link from settigs screen
  And clicks on OK button in confirmation dialog
  Then Navigated to Sighup Home page
#  And Quit the Yona app

 @unsubdualevice
 Scenario: User singup from device A and perfrom Add Device in device B, and after that unsubscribe from device A so he should get appropirate message when tries to login from device B
  Given User is on home page on device B
  Then User has sign to Yona from first device
  And Make a Add device request from settings screen
  Then Click on Login button in another device
  And Enters Mobile number and code generated above
  Then Clicks on VORIGE button
  And User is logged in to new device
  Then User is landed on enter OTP screen
  And Enters pincode fetched from alert
  Then Confirms the pin for application login
  And Trigger unsubscribe from device A API call
  Then Push the app in backgroun
  And Enters pincode fetched from alert
  Then Gets a error message stating user with given id not found
  And clicks on OK button in confirmation dialog
  Then Navigated to Sighup Home page
