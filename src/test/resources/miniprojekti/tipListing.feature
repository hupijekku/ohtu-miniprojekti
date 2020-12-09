Feature: As a user I can list all reading tips

    Scenario: User can see list of all the tips when there are any
        Given user is logged in
        And user is on the frontpage
        Then user can see the list

    Scenario: User can select which type of tips to list
        Given user is logged in
        And user is on the frontpage
        When user selects "Podcast" from the menu
        Then user can see only list of "Podcast"

