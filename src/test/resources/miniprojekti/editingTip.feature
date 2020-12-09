Feature: User can edit previously added tips

    Scenario: user can edit title of the book
        Given user is logged in
        And user has created a book
        When user edits the title
        Then the tip gets saved
