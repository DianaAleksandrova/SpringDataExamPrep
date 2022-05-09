package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PassengerSeedDto;
import softuni.exam.models.entity.Passenger;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PassengerServiceImpl implements PassengerService {
    private static final String PASSENGER_FILE_PATH = "src/main/resources/files/json/passengers.json";

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final TownRepository townRepository;

    public PassengerServiceImpl(PassengerRepository passengerRepository,
                                ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, TownRepository townRepository) {
        this.passengerRepository = passengerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Path.of(PASSENGER_FILE_PATH));
    }

    @Override
    public String importPassengers() throws IOException {
        StringBuilder builder = new StringBuilder();
        PassengerSeedDto[] passengerSeedDto = gson.fromJson(readPassengersFileContent(), PassengerSeedDto[].class);

        Arrays.stream(passengerSeedDto)
                .filter(passengerSeedDto1 -> {
                    boolean isValid = validationUtil.isValid(passengerSeedDto1);
                    builder.append(isValid ? String.format("Successfully imported Passenger %s - %s",
                            passengerSeedDto1.getLastName(), passengerSeedDto1.getEmail())
                            : "Invalid Passenger");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(passengerSeedDto1 -> {
                    Passenger passenger = modelMapper.map(passengerSeedDto1, Passenger.class);
                    passenger.setTown(townRepository.findByName(passengerSeedDto1.getTown()));
                    return passenger;
                })
                .forEach(passengerRepository::save);

        return builder.toString();

    }
    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {
        StringBuilder builder = new StringBuilder();

        passengerRepository.findPassengersOrderByTicketsCountDescendingThenByEmail()
                .forEach(passenger -> {
                    builder.append(String.format("Passenger %s  %s\n" +
                            "\tEmail - %s\n" +
                            "\tPhone - %s\n" +
                            "\tNumber of tickets - %d\n",
                            passenger.getFirstName(),passenger.getLastName(),
                            passenger.getEmail(),
                            passenger.getPhoneNumber(),
                            passenger.getTickets().size()));
                });
        return builder.toString();
    }
}
