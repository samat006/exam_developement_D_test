package com.example.rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarRentalService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    public boolean rentCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        if (car.isPresent() && car.get().isAvailable()) {
            car.get().setAvailable(false);
            carRepository.updateCar(car.get());
            return true;
        }
        return false;
    }

    public void returnCar(String registrationNumber) {
        Optional<Car> car = carRepository.findByRegistrationNumber(registrationNumber);
        car.ifPresent(c -> {
            c.setAvailable(true);
            carRepository.updateCar(c);
        });
    }

    public boolean addCarIfNotExists(Car car) {
        Optional<Car> existingCar = carRepository.findByRegistrationNumber(car.getRegistrationNumber());
        if (existingCar.isPresent()) {
            return false;
        }
        carRepository.addCar(car);
        return true;
    }

    public List<Car> findCarsByModel(String model) {
        return carRepository.findByModel(model);
    }
}