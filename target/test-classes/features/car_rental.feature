# src/test/resources/features/car_rental.feature
Feature: Car Rental Management

  Scenario: List all available cars
    Given the following cars are in the system
      | registrationNumber | model  | available |
      | ABC123             | Toyota | true      |
      | DEF456             | Honda  | false     |
      | GHI789             | BMW    | true      |
    When I request the list of all cars
    Then I should see 3 cars in the list
    And the car with registration "ABC123" should be available
    And the car with registration "DEF456" should not be available
    And the car with registration "GHI789" should be available

  Scenario: Rent a car successfully
    Given the following cars are in the system
      | registrationNumber | model  | available |
      | ABC123             | Toyota | true      |
    When I rent the car with registration "ABC123"
    Then the rental should be successful
    And the car with registration "ABC123" should not be available

  Scenario: Return a rented car
    Given the following cars are in the system
      | registrationNumber | model  | available |
      | ABC123             | Toyota | false     |
    When I return the car with registration "ABC123"
    Then the car with registration "ABC123" should be available