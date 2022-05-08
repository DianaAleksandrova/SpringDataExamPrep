package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CitySeedDto;
import softuni.exam.models.entity.City;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {
    private static final String CITIES_FILE_PATH ="src/main/resources/files/json/cities.json";

    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final CountryService countryService;

    public CityServiceImpl(CityRepository cityRepository, ModelMapper modelMapper, ValidationUtil validationUtil,
                           Gson gson,  CountryService countryService) {
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.countryService = countryService;
    }

    @Override
    public boolean areImported() {
        return cityRepository.count() > 0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(Path.of(CITIES_FILE_PATH));
    }

    @Override
    public String importCities() throws IOException {
        StringBuilder builder = new StringBuilder();
        CitySeedDto[] citySeedDto = gson.fromJson(readCitiesFileContent(),CitySeedDto[].class);

        Arrays.stream(citySeedDto)
                .filter(citySeedDto1 -> {
                    boolean isValid = validationUtil.isValid(citySeedDto1);
                    Optional<City> city = cityRepository.findByCityName(citySeedDto1.getCityName());
                    if (city.isPresent()){
                        isValid = false;
                    }
                    builder.append(isValid ? String.format("Successfully imported city %s - %d",
                            citySeedDto1.getCityName(),citySeedDto1.getPopulation())
                            : "Invalid city");
                    builder.append(System.lineSeparator());
                    return isValid;
                })
                .map(citySeedDto1 -> {
                    City city = modelMapper.map(citySeedDto1,City.class);
                    city.setCountry(countryService.findById(citySeedDto1.getCountry()));
                    return city;
                })
                .forEach(cityRepository::save);

        return builder.toString();
    }

    @Override
    public City findById(Long cityId) {
        return cityRepository.findById(cityId).orElse(null);
    }
}
