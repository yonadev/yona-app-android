Feature: View profile of a friend
  Scenario: User wants to view friends profiel from list of approved friends
    Given User has sighup to Yona application
    When Click on Friend tab and clicks on friend record
    Then Navigated to Frield profiel page a
    And Can view friend's profile details like name, profile pic, mobiel number, nick name etc but cannot edit it
