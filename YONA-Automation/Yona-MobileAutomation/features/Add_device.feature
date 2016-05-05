@Add_device
Feature: Once signup to Yona application, user should be able to login to Yona from another device using Add Device functionality.



  Scenario: Login from new device
    Given User has navigated to Login screen from Welcome screen
    Then User has sign to Yona from first device
    And Make a Add device request from settings screen
    Then Click on Login button in another device
    And Enters Mobile number and code generated above
    Then Clicks on VORIGE button
    And User is logged in to new device