Feature: User would like to change its PIN in case forgot or want to change for security reasons.

  Scenario: User forgot the PIN and would like to change PIN from Login screen
    Given User has signup to Yona application but forgots the PIN while trying to access Yona applcation.
    When User opens up Yona app and prompted for entering the PIN
    Then Click on PIN resest link he is displayed a message stating that he will received a SMS code in give time duartion
    And when clicks on OK button in popup he is navigated to enter OTP screen
    Then Waits for SMS to be recieved on registered device.
    And Enter the OTP recieved in SMS
    Then Navigated to create Passcode screen where he can set new PIN
    And On entering new PIN navigated to confirm PIN screen
    Then User confirms the PIN by entering same PIN code again
    And User is logged in to Yona on confirming the PIN


  Scenario: User want to change PIN as a security measure
    Given User has signup to Yona application.
    When User clicks on Settings tab
    And Click on Change PIN option.
    Then Prompted for entering current PIN
    And User provides current PIN
    Then Navigated to enter Passcode screen for setting up new PIN
    And Enters new PIN on this page and navigated next page for confirming the PIN
    Then Confirm the new PIN
    And User is logged in to Yona on confirming the PIN