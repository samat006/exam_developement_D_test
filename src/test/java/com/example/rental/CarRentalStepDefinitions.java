package com.example.rental;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class CarRentalStepDefinitions {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarRentalService carRentalService;

    private List<Car> allCars;
    private boolean rentalResult;

    @Before
    public void setup() {
        // Clear the repository before each scenario
        carRepository.getAllCars().clear();
    }

    @Given("the following cars are in the system")
    public void givenCarsInSystem(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            Car car = new Car(
                row.get("registrationNumber"),
                row.get("model"),
                Boolean.parseBoolean(row.get("available"))
            );
            carRepository.addCar(car);
        }
    }

    @When("I request the list of all cars")
    public void whenRequestListOfCars() {
        allCars = carRentalService.getAllCars();
    }

    @Then("I should see {int} cars in the list")
    public void thenIShouldSeeNCars(int expectedCount) {
        assertEquals(expectedCount, allCars.size());
    }

    @Then("the car with registration {string} should be available")
    public void thenCarShouldBeAvailable(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        assertTrue(car.isPresent());
        assertTrue(car.get().isAvailable());
    }

    @Then("the car with registration {string} should not be available")
    public void thenCarShouldNotBeAvailable(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        assertTrue(car.isPresent());
        assertFalse(car.get().isAvailable());
    }

    @When("I rent the car with registration {string}")
    public void whenIRentCar(String registrationNumber) {
        rentalResult = carRentalService.rentCar(registrationNumber);
    }

    @Then("the rental should be successful")
    public void thenRentalShouldBeSuccessful() {
        assertTrue(rentalResult);
    }

    @When("I return the car with registration {string}")
    public void whenIReturnCar(String registrationNumber) {
        carRentalService.returnCar(registrationNumber);
    }
}