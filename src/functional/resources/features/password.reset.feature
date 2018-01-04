Feature: Password reset

  Background:
    Given there is a user

  Scenario: User receives password reset code
    When the user resets password
    Then the password reset email is sent to him

  Scenario: User can use reset code to set new password
    Given the user has requested to reset the password
    When the user sets the new password using correct code
    Then he can log in using new password