Feature: User can log in with valid username/password-combination

    Scenario: user can login with correct password
        Given user is on the login page
        When  correct username "vihannes" and correct password "pass" are entered
        Then  user will be redirected to "index"

    Scenario: user can not login with incorrect password
        Given user is on the login page
        When  correct username "vihannes" and wrong password "tacosalsa" are entered
        Then  user will be redirected to ""

    Scenario: nonexistent user can not login to
        Given user is on the login page
        When  nonexistent username "foo" and password "bar123" are entered
        Then  user will be redirected to ""