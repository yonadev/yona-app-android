@Logintest
Feature: New user sign-up to Yona app As a new user I want to have the ability to sign-up to Yona app.


  @demo_login
  Scenario: User wants to Join the Yona
    Given User is on Home page and can see two options Login and Join
    When User clicks on Join button
    And Enters first name and last name
    Then Clicks on Next button
    And Then enters Mobile number and Nick Name on next screen
    Then Clicks on Next button on this screen
#    And Fetch pincode from alert
    And Clicks on OK button in alert
    Then Enters pincode fetched from alert
    Then Sets the pin for application login
    And Confirms the pin for application login
    Then User is landed on Challenges screen


  @addNoGoClng
  Scenario: User wants to Add NOGO Challenge
    Given User is logged on to Yona app
    Then Click on add goal button
    And select the category on next page.
    Then Click on Save goal button
    And Goal with selected category is added as NOGO challenge
    Then Select the goal and delete it
    And Goal is deleted and user is navigated back to Challenges home screen
