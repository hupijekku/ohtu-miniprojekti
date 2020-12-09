Feature: As a user I want to be able to add a reading tip

    Scenario: New tip can be added with proper values
        Given user is logged in
        And add is selected
        When new tip is entered with proper values
        Then new tip is added

    Scenario: Adding a new tip will give errors if values are not correct
        Given user is logged in
        And add is selected
        When new tip is entered with invalid values
        Then system will respond with errors