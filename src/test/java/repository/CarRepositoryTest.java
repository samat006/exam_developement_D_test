package repository;

import com.example.Car;
import com.example.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarRepositoryTest {

    private CarRepository carRepository;

    @BeforeEach
    public void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    public void shouldAddAndFindCar() {
        Car car = new Car("ABC123", "Peugeot", true);
        carRepository.addCar(car);

        Optional<Car> found = carRepository.findByRegistrationNumber("ABC123");
        assertTrue(found.isPresent());
        assertEquals("Peugeot", found.get().getModel());
    }

    @Test
    public void shouldUpdateCarAvailability() {
        Car car = new Car("DEF456", "Renault", true);
        carRepository.addCar(car);

        car.setAvailable(false);
        carRepository.updateCar(car);

        Optional<Car> updated = carRepository.findByRegistrationNumber("DEF456");
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isAvailable());
    }

    @Test
    public void shouldReturnAllCars() {
        carRepository.addCar(new Car("1", "Citroen", true));
        carRepository.addCar(new Car("2", "Opel", false));

        List<Car> cars = carRepository.getAllCars();
        assertEquals(2, cars.size());
    }
}
