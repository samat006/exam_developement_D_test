package com.example.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@ContextConfiguration(classes = CarRentalApplication.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarRentalService carRentalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetAllCars() throws Exception {
        // Arrange
        List<Car> cars = Arrays.asList(
                new Car("ABC123", "Toyota", true),
                new Car("DEF456", "Honda", false)
        );
        when(carRentalService.getAllCars()).thenReturn(cars);

        // Act & Assert
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].registrationNumber").value("ABC123"))
                .andExpect(jsonPath("$[0].model").value("Toyota"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].registrationNumber").value("DEF456"))
                .andExpect(jsonPath("$[1].model").value("Honda"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    public void testRentCarSuccess() throws Exception {
        // Arrange
        when(carRentalService.rentCar("ABC123")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/cars/rent/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testRentCarFailure() throws Exception {
        // Arrange
        when(carRentalService
        .rentCar("ABC123")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/cars/rent/ABC123"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void testReturnCar() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/cars/return/ABC123"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddCarSuccess() throws Exception {
        // Arrange
        Car car = new Car("ABC123", "Toyota", true);
        when(carRentalService.addCarIfNotExists(any(Car.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(content().string("Car added successfully"));
    }

    @Test
    public void testAddCarFailure() throws Exception {
        // Arrange
        Car car = new Car("ABC123", "Toyota", true);
        when(carRentalService.addCarIfNotExists(any(Car.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/cars/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Car with this registration number already exists"));
    }

    @Test
    public void testSearchByModel() throws Exception {
        // Arrange
        List<Car> cars = Arrays.asList(
                new Car("ABC123", "Toyota", true),
                new Car("DEF456", "Toyota", false)
        );
        when(carRentalService.findCarsByModel("Toyota")).thenReturn(cars);

        // Act & Assert
        mockMvc.perform(get("/cars/search").param("model", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Toyota"))
                .andExpect(jsonPath("$[1].model").value("Toyota"));
    }

    @Test
    public void testSearchByModelNoResults() throws Exception {
        // Arrange
        when(carRentalService.findCarsByModel(anyString())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/cars/search").param("model", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}