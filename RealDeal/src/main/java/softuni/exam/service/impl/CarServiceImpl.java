package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarSeedDto;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class CarServiceImpl implements CarService {

    private static final String CAR_FILE_PATH = "src/main/resources/files/json/cars.json";

    private final CarRepository carRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    public CarServiceImpl(CarRepository carRepository, ValidationUtil validationUtil,
                          ModelMapper modelMapper, Gson gson) {
        this.carRepository = carRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString(Path.of(CAR_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException {
        StringBuilder builder = new StringBuilder();
        CarSeedDto[] carSeedDtos = gson.fromJson(readCarsFileContent(),CarSeedDto[].class);

        Arrays.stream(carSeedDtos)
                .filter(carSeedDto -> {
                    boolean isValid = validationUtil.isValid(carSeedDto);
                    builder.append(isValid ? String.format("Successfully imported car - %s - %s",
                            carSeedDto.getMake(),carSeedDto.getModel())
                            : "Invalid car");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(carSeedDto -> modelMapper.map(carSeedDto, Car.class))
                .forEach(carRepository::save);
        return builder.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {
        StringBuilder builder = new StringBuilder();
        carRepository.findCarOrderByPictureDescThanByOrderAndByMake()
                .forEach(car -> {
                    builder.append(String.format
                            ("Car make - %s, model - %s\n" +
                                    "\tKilometers - %d\n" +
                                    "\tRegistered on - %s\n" +
                                    "\tNumber of pictures - %d\n",
                                    car.getMake(),car.getModel(),
                                    car.getKilometers(),
                                    car.getRegisteredOn(),
                                    car.getPictures().size()));
                    builder.append(System.lineSeparator());
                });
        return builder.toString();
    }

    @Override
    public Car findById(Long carId) {
        return carRepository.findById(carId).orElse(null);
    }
}
