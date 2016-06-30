@regression
Feature: Me Profile


  Background:
    Given User has intalled Yona app and launched it
    And User is on Home page and can see two options Login and Join
    When User clicks on Join button
    And Enters first name and last name
    Then Clicks on Next button
    And Then enters Mobile number and Nick Name on next screen
    Then Clicks on Next button on this screen
    And Clicks on OK button in alert
    Then Enters pincode fetched from alert
    Then Sets the pin for application login
    And Confirms the pin for application login

  @viewprofile
  Scenario: User can view his profile
    Given User is logged in to Yona app and on Profile page
    Then click on Profile icon on top left corner
    And Able to view details like first name,last name, nick name,mobile number and edit icon
    Then Quit the Yona app

