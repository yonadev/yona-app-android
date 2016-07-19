@regression
Feature: As a new user I want to have the ability to sign-up to Yona app.

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
    Then Check for permission popup and click OK if found
    Then User is landed on Challenges screen


  Scenarios: User enters invalid credentials during Sighup flow
    Given User is on Home page and can see two options Login and Join
    And  Clicks on Join button
    Then Leaves the firs name and last name empty and clicks on Next button
    And User gets a validation message to enter values for mandatory fields First Name and Last Name
    Then User enters first name and last name then clicks on Next buttion
    And On next screen does not enter mobile number and nick name and clicks on Next button
    Then User gets a validation message to enter values for mandatory fields Mobile Number and Nick Name
    And User enters values for Mobile Number and Nick Name and clicks on Next button
    Then User is navigated to OTP screen where he can enter OTP received in sms
    And User enters invalid OTP code
    Then Gets a validation message saying invalid OTP
    Then User enter valid OTP code
    And Navigated to next screen where prompted for setting up Passcode
    Then User enters four digit numeric code
    And On next screen again prompted for confirming the code where he enter different code than entered on previous screen.
    Then Navigated back to Passcode creation screen with message passcode doesn't match
    And This time again enters a new code
    And Confirms the same code on Confirm Passcode screen
    Then User is signed up to Yona and navigated to Challenges screen.









