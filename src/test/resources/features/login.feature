@smoke
Feature: SauceDemo Login
  Scenario: Success Login
    Given I navigate to the login page
    When I enter valid credentials
    Then I should see the products page