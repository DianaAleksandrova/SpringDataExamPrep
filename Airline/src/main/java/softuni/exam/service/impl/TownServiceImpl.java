package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TownSeedDto;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class TownServiceImpl implements TownService {
    private static final String TOWN_FILE_PATH = "src/main/resources/files/json/towns.json";

    private final TownRepository townRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public TownServiceImpl(TownRepository townRepository, ModelMapper modelMapper,
                           ValidationUtil validationUtil, Gson gson) {
        this.townRepository = townRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
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
                .filter(townSeedDto1 -> {
                    boolean isValid = validationUtil.isValid(townSeedDto1);
                    builder.append(isValid ? String.format("Successfully imported Town %s - %d",
                            townSeedDto1.getName(),townSeedDto1.getPopulation())
                            : "Invalid Town");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(townSeedDto1 -> modelMapper.map(townSeedDto1, Town.class))
                .forEach(townRepository::save);

        return builder.toString();
    }
}
