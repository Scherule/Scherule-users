Feature: Registration feature

  Scenario: User can register but remains inactive
    Given there is a registration request
    And this request is complete
    When this request is issued
    Then the user is created
    But the user remains inactive
    And the user is sent an email with confirmation code

#  Scenario: User can confirm his account
#    Given the user has issued a valid registration request
#    When he uses valid confirmation code
#    Then the user becomes active
#
#  Scenario: User cannot activate his account using wrong confirmation code
#    Given the user has issued a valid registration request
#    When he uses invalid confirmation code
#    Then he receives an error
#    And the user is still inactive
#
#  Scenario: User cannot use confirmation code twice
#    Given the user has already activated his account
#    When he uses the confirmation code used for confirming the account
#    Then he receives an error
#    And the user is still active