@regression
Feature: User has signed up to Yona from device A and would like to add anoter device B

  Background:
    Given User has installed Yona app in device B

  Scenario: User wants to add anoter device B to his Yona account
    Given User has navigated to Login screen from Welcome screen
    Then User has sign to Yona from first device
    And Make a Add device request from settings screen
    Then Click on Login button in another device
    And Enters Mobile number and code generated above
    Then Clicks on VORIGE button
    And User is logged in to new device
    Then User is landed on enter OTP screen
#    Then Enters pincode fetched from alert
    And Sets the pin for application login
    Then Confirms the pin for application login
