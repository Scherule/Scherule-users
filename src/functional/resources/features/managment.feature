Feature: Management feature

  Scenario: Super user can create admin users
    When super user creates admin user
    Then the admin user is created

  Scenario: Super user cannot do anything else
    When super user does any other action
    Then he is forbidden to do so