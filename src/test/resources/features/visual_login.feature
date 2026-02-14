@smoke
Feature: SauceDemo Visual User Login

  Scenario: Success Login with Visual User
    Given I navigate to the login page
    When I login with "visual_user" and "secret_sauce"
    Then I should see the products page