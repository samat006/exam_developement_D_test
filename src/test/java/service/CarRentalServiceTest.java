package service;


import com.example.Car;
import com.example.repository.CarRepository;
import com.example.service.CarRentalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarRentalServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarRentalService carRentalService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldRentAvailableCar() {
        Car car = new Car("RENT123", "Toyota", true);
        when(carRepository.findByRegistrationNumber("RENT123")).thenReturn(Optional.of(car));

        boolean result = carRentalService.rentCar("RENT123");

        assertTrue(result);
        assertFalse(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    public void shouldNotRentUnavailableCar() {
        Car car = new Car("RENT456", "Nissan", false);
        when(carRepository.findByRegistrationNumber("RENT456")).thenReturn(Optional.of(car));

        boolean result = carRentalService.rentCar("RENT456");

        assertFalse(result);
        verify(carRepository, never()).updateCar(any());
    }

    @Test
    public void shouldReturnCar() {
        Car car = new Car("RETURN123", "Honda", false);
        when(carRepository.findByRegistrationNumber("RETURN123")).thenReturn(Optional.of(car));

        carRentalService.returnCar("RETURN123");

        assertTrue(car.isAvailable());
        verify(carRepository).updateCar(car);
    }

    @Test
    public void spyTestUpdateCarCalledOnceOnRent() {
        CarRepository spyRepo = spy(new CarRepository());
        CarRentalService service = new CarRentalService();
        // Setter style injection
        service.setCarRepository(spyRepo);

        Car car = new Car("SPY001", "SpyCar", true);
        spyRepo.addCar(car);

        boolean rented = service.rentCar("SPY001");

        assertTrue(rented);
        verify(spyRepo, times(1)).updateCar(any(Car.class));
    }
}
