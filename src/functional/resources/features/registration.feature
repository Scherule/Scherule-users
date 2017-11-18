Feature: Registration feature

  Scenario: User can register but remains inactive
    Given there is a registration request
    And this request is complete
    When this request is issued
    Then the user is created
    But the user remains inactive
    And the user is sent an email with confirmation code

  Scenario: User can confirm his account
    Given the user has issued a valid registration request
    When he confirms his account using valid confirmation code
    Then the user becomes active

  Scenario: User cannot activate his account using wrong confirmation code
    Given the user has issued a valid registration request
    When he confirms his account using invalid confirmation code
    Then the user remains inactive

  Scenario: Using confirmation code twice has no effect
    Given the user has already activated his account
    When he uses the confirmation code already used for confirming the account
    Then this action has no effect