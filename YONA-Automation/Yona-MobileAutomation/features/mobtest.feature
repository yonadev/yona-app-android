Feature: MobTest setup
  As a user I want to verify my framework works

  @mobtest
  Scenario: Start Application and take screenshot
    Given Application is up and running
    Then I take a screenshot and save it to ~/Screenshots
