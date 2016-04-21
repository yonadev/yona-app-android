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


