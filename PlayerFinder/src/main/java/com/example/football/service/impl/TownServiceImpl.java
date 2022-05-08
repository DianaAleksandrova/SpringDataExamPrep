package com.example.football.service.impl;

import com.example.football.models.dto.TownSeedDto;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;


@Service
public class TownServiceImpl implements TownService {

    private static final String TOWN_FILE_PATH = "src/main/resources/files/json/towns.json";

    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public TownServiceImpl(TownRepository townRepository, Gson gson,
                           ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }


    @Override
    public boolean areImported() {
        return townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWN_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder builder = new StringBuilder();
        TownSeedDto[] townSeedDto = gson.fromJson(readTownsFileContent(),TownSeedDto[].class);

        Arrays.stream(townSeedDto)
                .filter(townSeedDtos -> {
                    boolean isValid = validationUtil.isValid(townSeedDtos);
//                    Optional<Town> town = townRepository.findTownByName(townSeedDtos.getName());
//                    if (town.isPresent()){
//                        isValid = false;
//                    }
                    builder.append(isValid ? String.format("Successfully imported Town %s - %d",
                            townSeedDtos.getName(),townSeedDtos.getPopulation())
                            : "Invalid Town");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(townSeedDtos -> modelMapper.map(townSeedDtos, Town.class))
                .forEach(townRepository::save);

        return builder.toString();
    }

    @Override
    public Town findByName(String name) {
        return townRepository.findByName(name);
    }


}
