Feature:User would like to invite his/her friend to start using Yona app.

  Scenario: User invites a friend by manually adding all details.
    Given User has signup to yona application
    When User clicks on Friends tab.
    Then Click on plus button under Overview tab.
    And Click on  Invite a Friend button without entering mandatory fields
    But Validation message to enter values for mandatory fields is displayed
    Then User enters missing values and click on Invite a Friend button
    And Request is sent to friend and user is navigated back to Friends tab

  Scenario: User invites a friend by selecting a friend from address book
    Given User wants to invites a friend from address book but address book access permissions are set to off
    When User clicks on Friends tab for adding a friend
    Then Click on plus button on Overview tab.
    And Clicks on Uit adresboek
    And Gets a popup to allow access to Address book
    But User clicks on Cancel button
    Then User is redirected back to manual details screen.
    And User again clicks on Uit adresboek button
    Then Clicks on Allow butotn in Address book permissions popup.
    And User is navigated to Address book from where contacts can be selected.
    Then User tab on any one contact from address bool
    And Details of selected contacts are populated on friend request page
    Then User clicks on "Invite a Friend" button.
    And Request is sent to friend and user is navigated back to Friends tab

