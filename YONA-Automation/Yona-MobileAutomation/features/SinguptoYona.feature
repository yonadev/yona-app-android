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
    Given User is logged in to Yona app and on Challenges tab
    And Click on NOGO goal
    Then Click on add goal button
    And select the category on next page.
    Then Click on Save goal button
    And Goal with selected category is added as NOGO challenge
    Then Select the goal and delete it
    And Goal is deleted and user is navigated back to Challenges home screen

  @addTimeFrClng
  Scenario: User wants to Add TimeFrm Challenge
   Given User is logged in to Yona app and on Challenges tab
#    Given User is already on challenges tab
    Then Click on Timzone goal
    And Click on add goal button
    Then select the category of Timezone goal on next page
    And Click on add goal button
    Then Specify the time duration and click on OK button
    And Click on Save goal button
    Then Edit the goal and delete time duration
    And Click on add goal button
    Then Specify the time duration and click on OK button
    And Click on Save goal button
    And Select the goal and delete it
    Then Goal is deleted and user is navigated back to Challenges home screen


  @addCrdClng
  Scenario: User wants to Add Credit Challenge
    Given User is logged in to Yona app and on Challenges tab
#    Given User is already on challenges tab
    Then Click on Credit goals
    And Click on add goal button
    Then select the category on next page.
    And Specify the time duration in minutes
    Then Click on Save goal button
    Then Edit the goal and change the duration
    Then Click on Save goal button
    Then Select the goal and delete it
    And Goal is deleted and user is navigated back to Challenges home screen

  @viewProfile
  Scenario: User can view his profile
    Given User is logged in to Yona app and on Profile page
    Then click on Profile icon on top left corner
    And Able to view details like first name,last name, nick name,mobile number and edit icon
