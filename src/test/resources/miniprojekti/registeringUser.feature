Feature: User can register a new user

    Scenario: user can create a new user
    Given user is on the login page
    When user types username and password to register
    Then new user is created