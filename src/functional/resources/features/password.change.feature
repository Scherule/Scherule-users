Feature: Password change

  Background:
    Given there is a user

  Scenario: User can change his password
    When the user changes his password
    Then he can log in using new password
    And he cannot log in using old password