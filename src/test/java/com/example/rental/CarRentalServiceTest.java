package com.example.rental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarRentalServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    @Test
    public void testGetAllCars() {
        // Arrange
        List<Car> expectedCars = Arrays.asList(
                new Car("ABC123", "Toyota", true),
                new Car("DEF456", "Honda", false)
        );
        when(carRepository.getAllCars()).thenReturn(expectedCars);

        // Act
        List<Car> actualCars = carRentalService.getAllCars();

        // Assert
        assertEquals(expectedCars, actualCars);
        verify(carRepository).getAllCars();
    }

    @Test
    public void testRentCarSuccessful() {
        // Arrange
        String regNum = "ABC123";
        Car car = new Car(regNum, "Toyota", true);
        when(carRepository.findByRegistrationNumber(regNum)).thenReturn(Optional.of(car));

        // Act
        boolean result = carRentalService.rentCar(regNum);

        // Assert
        assertTrue(result);
        assertFalse(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    public void testRentCarNotAvailable() {
        // Arrange
        String regNum = "ABC123";
        Car car = new Car(regNum, "Toyota", false);
        when(carRepository.findByRegistrationNumber(regNum)).thenReturn(Optional.of(car));

        // Act
        boolean result = carRentalService.rentCar(regNum);

        // Assert
        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    public void testRentCarNotFound() {
        // Arrange
        String regNum = "ABC123";
        when(carRepository.findByRegistrationNumber(regNum)).thenReturn(Optional.empty());

        // Act
        boolean result = carRentalService.rentCar(regNum);

        // Assert
        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    public void testReturnCar() {
        // Arrange
        String regNum = "ABC123";
        Car car = new Car(regNum, "Toyota", false);
        when(carRepository.findByRegistrationNumber(regNum)).thenReturn(Optional.of(car));

        // Act
        carRentalService.returnCar(regNum);

        // Assert
        assertTrue(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    public void testReturnCarNotFound() {
        // Arrange
        String regNum = "ABC123";
        when(carRepository.findByRegistrationNumber(regNum)).thenReturn(Optional.empty());

        // Act
        carRentalService.returnCar(regNum);

        // Assert
        verify(carRepository, never()).updateCar(any());
    }

    // TDD - New feature 1: Add car
    @Test
    public void testAddCarIfNotExistsSuccess() {
        // Arrange
        Car car = new Car("ABC123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.empty());

        // Act
        boolean result = carRentalService.addCarIfNotExists(car);

        // Assert
        assertTrue(result);
        verify(carRepository).addCar(car);
    }

    @Test
    public void testAddCarIfNotExistsDuplicate() {
        // Arrange
        Car car = new Car("ABC123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("ABC123")).thenReturn(Optional.of(car));

        // Act
        boolean result = carRentalService.addCarIfNotExists(car);

        // Assert
        assertFalse(result);
        verify(carRepository, never()).addCar(any());
    }

    // TDD - New feature 2: Search by model
    @Test
    public void testFindCarsByModel() {
        // Arrange
        String model = "Toyota";
        List<Car> expectedCars = Arrays.asList(
                new Car("ABC123", model, true),
                new Car("DEF456", model, false)
        );
        when(carRepository.findByModel(model)).thenReturn(expectedCars);

        // Act
        List<Car> actualCars = carRentalService.findCarsByModel(model);

        // Assert
        assertEquals(expectedCars, actualCars);
        verify(carRepository).findByModel(model);
    }

    @Test
    public void testFindCarsByModelNotFound() {
        // Arrange
        String model = "Unknown";
        when(carRepository.findByModel(model)).thenReturn(Collections.emptyList());

        // Act
        List<Car> cars = carRentalService.findCarsByModel(model);

        // Assert
        assertTrue(cars.isEmpty());
        verify(carRepository).findByModel(model);
    }
}